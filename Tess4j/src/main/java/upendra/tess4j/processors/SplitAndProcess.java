package upendra.tess4j.processors;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.recognition.software.jdeskew.ImageDeskew;

import upendra.tess4j.constants.Constants;
import upendra.tess4j.utils.Log;

public class SplitAndProcess implements Runnable{

	//Attributes
	String filename;
	String extention;
	String outputFolderPath;
	int i;
	BufferedImage bi;



	/**
	 * @param filename
	 * @param extention
	 * @param outputFolderPath
	 * @param i
	 * @param bi
	 */
	public SplitAndProcess(String filename, String extention, String outputFolderPath, int i, BufferedImage bi) {
		this.filename = filename;
		this.extention = extention;
		this.outputFolderPath = outputFolderPath;
		this.i = i;
		this.bi = bi;
	}

	//For Deskew
	static final Double MIN_DESKEW_THRESHOLD = 0.05d;




	@Override
	public void run() {

		File outputFile = new File(outputFolderPath+Constants.SEP+i+"."+extention);
		try {
			if (Constants.isDeskewEnabled.booleanValue())
				ImageIO.write(deSkewImage(bi),extention,outputFile);
			else
				ImageIO.write(bi,extention,outputFile);
			
			outputFolderPath=null; bi=null;
		} catch (IOException e) {
			Log.error(filename + " : Error at page " + i + ". Cancelling Split Operation");
			throw new RuntimeException(e.getMessage());
		}
	}

	public static BufferedImage deSkewImage(BufferedImage bi) {
		ImageDeskew id = new ImageDeskew(bi);
		Double imageSkewAngle = id.getSkewAngle();

		if ((imageSkewAngle> MIN_DESKEW_THRESHOLD || imageSkewAngle < -(MIN_DESKEW_THRESHOLD))) {
			bi = rotateImage(bi, -imageSkewAngle);
		}

		return bi;
	}

	private static BufferedImage rotateImage(BufferedImage bi, Double degrees) {
		int w = bi.getWidth();
		int h = bi.getHeight();

		BufferedImage res = new BufferedImage(w, h, bi.getType());
		Graphics2D g = res.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);
		g.rotate(Math.toRadians(degrees), w/2, h/2);
		g.drawImage(bi,null,0,0);

		return res;
	}
}
