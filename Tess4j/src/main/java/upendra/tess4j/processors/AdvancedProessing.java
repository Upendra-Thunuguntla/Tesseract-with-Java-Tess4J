package upendra.tess4j.processors;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import upendra.tess4j.constants.Constants;
import upendra.tess4j.utils.Log;
import upendra.tess4j.utils.Tools;

public class AdvancedProessing implements Runnable{

	//For Conversion
	File inputTifFile;
	String outputPDFPath;
	String filename_without_extention;

	//For Splitting and Merging
	String extention = "tif";
	File splitTIFLocation;
	File splitPDFLocation;

	//For Monitoring Purpose
	Boolean wait;

	public AdvancedProessing(File inputFile, String outputPath, Boolean wait) {
		this.inputTifFile = inputFile;
		this.outputPDFPath = outputPath;
		this.filename_without_extention = Tools.getFileName(inputFile);
		this.wait=wait;
	}

	@Override
	public void run() {
		if (wait)
			Tools.wait(1000 * 60 * 1);
		
		processTIFFile();
	}

	public void processTIFFile() {

		try {
			ImageInputStream is = ImageIO.createImageInputStream(inputTifFile);
			if ( is == null || is.length() == 0) {
				throw new NullPointerException("Empty File");
			}
			Iterator<ImageReader> iter = ImageIO.getImageReaders(is);
			if (iter == null || !iter.hasNext()) {
				throw new IOException("Image file format not supported by ImageIO : "+inputTifFile.getName());
			}

			ImageReader reader = iter.next();

			reader.setInput(is);

			Integer totalPages = reader.getNumImages(true);
			logInfo("Total Pages - " + totalPages);
			splitPDFLocation = new File(Constants.TMP+Constants.SEP+filename_without_extention+Constants.SEP+"SplitPDFs");
			splitTIFLocation = new File(Constants.TMP+Constants.SEP+filename_without_extention+Constants.SEP+"SplitTIFs");

			splitPDFLocation.mkdirs();
			splitTIFLocation.mkdirs();

			ExecutorService ex = Executors.newFixedThreadPool(Constants.THREAD_COUNT_1);

			for (int i=0;i<totalPages;i++) {
				ex.execute(new SplitAndProcess(filename_without_extention, extention, splitTIFLocation.getAbsolutePath(), i, reader.read(i)));
			}

			ex.shutdown();
			while(!ex.isTerminated()) {}


		}catch (Exception e ) {
			logError(e);
			throw new RuntimeException(e.getMessage());
		}
	}


	private void logInfo(String message) {
		Log.info(inputTifFile.getName()+" : "+message);
	}

	private void logError(String message) {
		Log.error(inputTifFile.getName()+" : "+message);
	}

	private void logError(Exception ex) {
		logError(Tools.writeException(ex));
	}
}
