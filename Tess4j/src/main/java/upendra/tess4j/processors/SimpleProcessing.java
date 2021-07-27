package upendra.tess4j.processors;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import org.apache.commons.io.FileUtils;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import upendra.tess4j.constants.Constants;
import upendra.tess4j.utils.Log;
import upendra.tess4j.utils.Tools;

public class SimpleProcessing implements Runnable{

	File inputFile;
	String output;
	String fileName;
	Tesseract tess;

	//For Monitoring Purpose
	Boolean wait=false;


	public SimpleProcessing(Tesseract tess, File inputFile, String output, Boolean wait) {
		this.inputFile = inputFile;
		this.output = output;
		this.fileName = Tools.getFileName(inputFile);
		this.tess = tess;
		this.wait = wait;
	}

	public SimpleProcessing(Tesseract tess, File inputFile, String output, Integer empty) {
		this.inputFile = inputFile;
		this.output = output;
		this.fileName = Tools.getFileName(inputFile);
		this.tess = tess;
	}


	@Override
	public void run() {
		if (wait) {
			Tools.wait(1000 * 60 * 5);
			Instant start = Instant.now();
			runConversion();
			Instant stop = Instant.now();
			logInfo("Completed in : " + Duration.between(start, stop));

		}else {
			runSplitConversion();
		}
	}


	public void runConversion() {
		try {
			logInfo("Started Converting");
			tess.createDocuments(inputFile.getAbsolutePath(), output, Constants.outputFormat);
			logInfo("Finished Converting");
			FileUtils.moveFileToDirectory(inputFile, new File(Constants.CONVERTED), false);
		}catch (TesseractException | IOException e) {
			logError(e);
			//TODO Add to Failed List
			throw new RuntimeException(e.getMessage());
		}
	}

	public void runSplitConversion() {
		try {
			tess.createDocuments(inputFile.getAbsolutePath(), output, Constants.outputFormat);
		}catch (TesseractException e) {
			logError(e);
			throw new RuntimeException(e.getMessage());
		}
	}


	private void logInfo(String message) {
		Log.info(inputFile.getName()+" : "+message);
	}

	private void logError(String message) {
		Log.error(inputFile.getName()+" : "+message);
	}

	private void logError(Exception ex) {
		logError(Tools.writeException(ex));
	}
}
