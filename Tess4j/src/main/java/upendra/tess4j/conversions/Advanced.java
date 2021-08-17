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

import upendra.tess4j.processors.AdvancedProessing;
import upendra.tess4j.utils.Constants;
import upendra.tess4j.utils.Tools;

public class Advanced implements Conversion{


	static final Logger log = Logger.getLogger(Advanced.class);

	@Override
	public void convert() throws IOException, InterruptedException {
		File input = new File(Constants.INPUT);
		ExecutorService exe = Executors.newFixedThreadPool(Constants.THREAD_COUNT_1);
		log.info("Prerforming Advanced Conversion");

		for (File f: input.listFiles()) {
			exe.execute(new AdvancedProessing(f, 
					Constants.OUTPUT+Constants.SEP+Tools.getFileName(f)+".pdf", false));
		}

		exe.shutdown();
		while(!exe.isTerminated()) {}
		if (Constants.isWatchFolderEnabled.booleanValue())
			watchAndConvert();
	}

	@Override
	public void watchAndConvert() throws IOException, InterruptedException {
		ExecutorService exe = Executors.newFixedThreadPool(Constants.THREAD_COUNT_1);

		WatchService watchService = FileSystems.getDefault().newWatchService();
		Path path = Paths.get(Constants.INPUT);
		path.register(watchService,ENTRY_CREATE);

		WatchKey key;
		while ((key = watchService.take()) != null) {
			for (WatchEvent<?> event : key.pollEvents()) {
				log.info(event.kind()+ " : " + event.context() );
				exe.execute(new AdvancedProessing(new File(Constants.INPUT+Constants.SEP+event.context()), 
						Constants.OUTPUT+Constants.SEP+Tools.getFileName(event.context().toString())+".pdf", true));
			}
			key.reset();
		}
	}

}
