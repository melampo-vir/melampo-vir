package it.cnr.isti.vir.features;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.junit.Test;

import net.semanticmetadata.lire.imageanalysis.LireFeature;



public class TestLireMp7FeatureExtraction {

	
	@Test
	public void testScalableColorFeatureExtractor()
			throws Exception {
		
		String features = extractFeatures("net.semanticmetadata.lire.imageanalysis.ScalableColor", "/images/cluj_avram_iancu.jpg");
		assertNotNull(features);
		assertEquals(features.length(), 167);
		
	}

	protected String extractFeatures(String extractorClassName, String fileName) throws Exception {
		
		LireFeature scalableColorExtractor = (LireFeature)Class.forName(extractorClassName)
				.newInstance();
				InputStream in = getClass().getResourceAsStream(fileName);
				
		
		BufferedImage image = ImageIO.read(in);
		scalableColorExtractor.extract( image );
		String features = scalableColorExtractor.getStringRepresentation();
		System.out.println(features);
		return features;
	}
	
}
