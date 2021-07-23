package upendra.tess4j.utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Tools {

	private Tools() {}
	
	public static String writeException(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.flush();
		pw.close();
		return sw.toString();
	}

	public static String getFileName(File file) {
		return getFileName(file.getName());
	}

	public static String getFileName(String fileName) {
		int index = fileName.lastIndexOf(".");
		if (index > 0) {
			return fileName.substring(0,index);}
		return fileName;
	}

	public static void wait(int duration) {
		Long now = System.currentTimeMillis();
		Long sleepTime = now + duration;

		while(now<sleepTime) {
			if(now != System.currentTimeMillis()) {
				now = System.currentTimeMillis();}}
	}
}
