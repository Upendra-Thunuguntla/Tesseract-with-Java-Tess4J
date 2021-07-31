package upendra.tess4j.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.tess4j.ITesseract.RenderedFormat;

public class Constants {

	private Constants() {}

	public static final String 
	SEP				= File.separator,
	PROCESSING_FOLDER = Config.getString("Input"),
	INPUT 			= PROCESSING_FOLDER + SEP + "Input",
	OUTPUT			= PROCESSING_FOLDER + SEP + "Output",
	CONVERTED 		= PROCESSING_FOLDER + SEP + "Converted",
	TMP 			= PROCESSING_FOLDER + SEP + "tmp",
	TESS_DATA 		= PROCESSING_FOLDER + SEP + "TessData"
	;

	
	public static final Integer 
	THREAD_COUNT_1 = Integer.parseInt(Config.getString("ThreadCount1")),
	THREAD_COUNT_2 = Integer.parseInt(Config.getString("ThreadCount2")),
	DATA_PATH_COUNT = Integer.parseInt(Config.getString("MultipleDataPathCount"));


	public static final Boolean
	isAdvancedProcessingEnabled = "Y".equals(Config.getString("AdvancedProcessing")),
	isDeskewEnabled = "Y".equals(Config.getString("Deskew")),
	isWatchFolderEnabled = "Y".equals(Config.getString("WatchFolder"));

	
	public static final List<RenderedFormat> outputFormat = new ArrayList<RenderedFormat>() {
		private static final long serialVersionUID = -4389040553456467156L;
		{add(RenderedFormat.PDF);}
	};

	
}
