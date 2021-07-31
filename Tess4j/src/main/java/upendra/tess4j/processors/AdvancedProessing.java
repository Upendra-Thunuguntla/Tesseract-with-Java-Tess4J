package upendra.tess4j.processors;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.log4j.Logger;

import net.sourceforge.tess4j.Tesseract;
import upendra.tess4j.utils.Constants;
import upendra.tess4j.utils.Tools;

public class AdvancedProessing implements Runnable{


	static final Logger log = Logger.getLogger(AdvancedProessing.class);

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
		if (wait.booleanValue())
			Tools.wait(Constants.WAIT_TIME);

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
			logInfo("Started Splitting");
			for (int i=0;i<totalPages;i++) {
				ex.execute(new SplitAndProcess(filename_without_extention, extention, splitTIFLocation.getAbsolutePath(), i, reader.read(i),splitPDFLocation));
			}

			ex.shutdown();
			while(!ex.isTerminated()) {}
			logInfo("Splitting Complete");
			doOCR();

		}catch (Exception e ) {
			logError(e);
			throw new RuntimeException(e.getMessage());
		}
	}


	private void doOCR() {

		Integer dataset = 0;
		ExecutorService exe = Executors.newFixedThreadPool(Constants.THREAD_COUNT_1);
		log.info("Performing OCR Conversion");

		for (File f: splitTIFLocation.listFiles()) {

			String tessDataFolder = Constants.TESS_DATA+Constants.SEP+"tessdata_"+(dataset%Constants.DATA_PATH_COUNT);
			System.out.println(tessDataFolder);
			Tesseract tess = new Tesseract();
			tess.setDatapath(tessDataFolder);
			tess.setTessVariable("user_defined_dpi", "300");
			exe.execute(new SimpleProcessing(tess, f, splitPDFLocation.getAbsolutePath()+Constants.SEP+Tools.getFileName(f),0));
			tess = null;
			dataset++;
		}

		exe.shutdown();
		while(!exe.isTerminated()) {}
	}

	private void logInfo(String message) {
		log.info(inputTifFile.getName()+" : "+message);
	}

	private void logError(String message) {
		log.error(inputTifFile.getName()+" : "+message);
	}

	private void logError(Exception ex) {
		logError(Tools.writeException(ex));
	}
}
