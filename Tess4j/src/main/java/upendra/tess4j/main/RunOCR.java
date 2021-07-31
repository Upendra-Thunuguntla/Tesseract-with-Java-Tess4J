package upendra.tess4j.main;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import org.apache.log4j.Logger;

import upendra.tess4j.conversions.Advanced;
import upendra.tess4j.conversions.Conversion;
import upendra.tess4j.conversions.Direct;
import upendra.tess4j.utils.Constants;


public class RunOCR {

	static final Logger log = Logger.getLogger(RunOCR.class);

	private RunOCR() {}

	public static void main( String[] args ) throws IOException, InterruptedException
	{
		Instant start = Instant.now();
		Conversion.setupFolderStrucure();
		Conversion.createMultipleTessDataFolders();
		
		
		if (Constants.isAdvancedProcessingEnabled.booleanValue()) {
			Advanced advanced = new Advanced();
			if (Constants.isWatchFolderEnabled.booleanValue()) {
				advanced.watchAndConvert();
			}else {
				advanced.convert();
			}
		}else {
			Direct direct = new Direct();
			if (Constants.isWatchFolderEnabled.booleanValue()) {
				direct.watchAndConvert();
			}else {
				direct.convert();
			}
		}

		Instant stop = Instant.now();
		log.info("Completed in : " + Duration.between(start, stop));
	}
}
