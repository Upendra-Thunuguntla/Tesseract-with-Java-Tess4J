package upendra.tess4j.processors;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.recognition.software.jdeskew.ImageDeskew;

public class SplitAndProcess implements Runnable{


	//For Deskew
	static final Double MIN_DESKEW_THRESHOLD = 0.05d;

	@Override
	public void run() {

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
