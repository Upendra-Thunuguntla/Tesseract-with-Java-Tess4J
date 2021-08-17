package Tesseract.Tess4j;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import upendra.tess4j.conversions.Advanced;
import upendra.tess4j.conversions.Direct;

public class OCRTest{


	@Test
	public void advancedDeskewDisabled() throws IOException, InterruptedException {
		Advanced adv = new Advanced();
		adv.convert();
		assertTrue(true);
	}
	
	@Test
	public void directConversion() throws IOException, InterruptedException {
		Direct dir = new Direct();
		dir.convert();
		assertTrue(true);
	}
}
