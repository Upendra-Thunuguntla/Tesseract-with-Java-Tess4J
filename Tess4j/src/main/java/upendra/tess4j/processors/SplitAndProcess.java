package upendra.tess4j.processors;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.recognition.software.jdeskew.ImageDeskew;

import upendra.tess4j.utils.Constants;


public class SplitAndProcess implements Runnable{

	
	static final Logger log = Logger.getLogger(SplitAndProcess.class);
	
	//Attributes
	String filename;
	String extention;
	String outputFolderPath;
	int i;
	BufferedImage bi;
	File pdfOutFolder;


	/**
	 * @param filename
	 * @param extention
	 * @param outputFolderPath
	 * @param i
	 * @param bi
	 */
	public SplitAndProcess(String filename, String extention, String outputFolderPath, int i, BufferedImage bi, File pdfOutFolder) {
		this.filename = filename;
		this.extention = extention;
		this.outputFolderPath = outputFolderPath;
		this.i = i;
		this.bi = bi;
		this.pdfOutFolder = pdfOutFolder;
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
			
			if (i%5==0)
				System.gc();
			
			if (i%10==0)
				log.info(filename+" : " +i);
		} catch ( IOException e) {
			log.error(filename + " : Error at page " + i + ". Cancelling Split Operation");
			throw new RuntimeException(e.getMessage());
		}
	}

	public static BufferedImage deSkewImage(BufferedImage bi) {
		ImageDeskew id = new ImageDeskew(bi);
		Double imageSkewAngle = id.getSkewAngle();

		if ((imageSkewAngle> MIN_DESKEW_THRESHOLD || imageSkewAngle < -(MIN_DESKEW_THRESHOLD))) {
			bi = rotateImage(bi, -imageSkewAngle);
		}
		id = null;imageSkewAngle=null;
		return bi;
	}

	private static BufferedImage rotateImage(BufferedImage bi, Double degrees) {
		int w = bi.getWidth();
		int h = bi.getHeight();
		
		BufferedImage res = 
				new BufferedImage(
						w, h, 
						12);
		
		//bi.getType());
		//TODO Error in getType for one image - check why
		
		Graphics2D g = res.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);
		g.rotate(Math.toRadians(degrees), w/2, h/2);
		g.drawImage(bi,null,0,0);
		g=null;bi=null;degrees=null;
		return res;
	}
}
