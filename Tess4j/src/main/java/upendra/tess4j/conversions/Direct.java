package upendra.tess4j.conversions;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import net.sourceforge.tess4j.Tesseract;
import upendra.tess4j.processors.SimpleProcessing;
import upendra.tess4j.utils.Constants;
import upendra.tess4j.utils.Tools;

public class Direct implements Conversion{

	static final Logger log = Logger.getLogger(Direct.class);

	public void convert() throws IOException, InterruptedException {
		File input = new File(Constants.INPUT);
		Integer dataset = 0;
		ExecutorService exe = Executors.newFixedThreadPool(Constants.THREAD_COUNT_1);
		log.info("Performing Direct Conversion");

		for (File f: input.listFiles()) {

			String tessDataFolder = Constants.TESS_DATA+Constants.SEP+"tessdata_"+(dataset%Constants.DATA_PATH_COUNT);
			Tesseract tess = new Tesseract();
			tess.setDatapath(tessDataFolder);
			exe.execute(new SimpleProcessing(tess, f, Constants.OUTPUT+Constants.SEP+Tools.getFileName(f), false));
			tess = null;
			dataset++;
		}

		exe.shutdown();
		while(!exe.isTerminated()) {}
		if (Constants.isWatchFolderEnabled.booleanValue())
			watchAndConvert();

	}

	public void watchAndConvert() throws IOException, InterruptedException{
		Integer dataset = 0;
		ExecutorService exe = Executors.newFixedThreadPool(Constants.THREAD_COUNT_1);

		WatchService watchService = FileSystems.getDefault().newWatchService();
		Path path = Paths.get(Constants.INPUT);
		path.register(watchService,ENTRY_CREATE);

		WatchKey key;
		while ((key = watchService.take()) != null) {
			for (WatchEvent<?> event : key.pollEvents()) {
				log.info(event.kind()+ " : " + event.context() );
				Tesseract tess = new Tesseract();
				String tessDataFolder = Constants.TESS_DATA+Constants.SEP+"tessdata_"+(dataset%Constants.DATA_PATH_COUNT);
				tess.setDatapath(tessDataFolder);
				exe.execute(new SimpleProcessing(tess, new File(Constants.INPUT+Constants.SEP+event.context()), Constants.OUTPUT+Constants.SEP+Tools.getFileName(event.context().toString()), true));
				tess = null;
				dataset++;
			}
			key.reset();
		}
	}
}


