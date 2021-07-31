package upendra.tess4j.conversions;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import net.sourceforge.tess4j.util.LoadLibs;
import upendra.tess4j.utils.Constants;

public interface Conversion{
	public abstract void convert() throws IOException, InterruptedException;
	public abstract void watchAndConvert() throws IOException, InterruptedException;
	
	
	public static void createMultipleTessDataFolders() throws IOException {
		File tessDatares = LoadLibs.extractTessResources("tessdata");
		File tessDataFolder = new File(Constants.TESS_DATA);

		if (tessDataFolder.listFiles().length < Constants.DATA_PATH_COUNT) {
			for (int i=0;i<Constants.DATA_PATH_COUNT;i++) {
				FileUtils.copyDirectory(tessDatares, new File(tessDataFolder.getAbsolutePath()+Constants.SEP+"tessdata_"+i),true);
			}
		}
	}

	public static void setupFolderStrucure() {
		File[] files = new File[] {
				new File(Constants.INPUT),
				new File(Constants.OUTPUT),
				new File(Constants.CONVERTED),
				new File(Constants.TESS_DATA),
				new File(Constants.TMP)
		};
		for (File f:files) {
			f.mkdirs();
		}
	}

}
