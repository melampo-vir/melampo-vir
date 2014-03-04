package it.cnr.isti.vir.features;

import static org.junit.Assert.*;

import org.junit.Test;




public class TestLireMp7FeatureExtraction extends BaseFeatureExtraction {

	
	@Test
	public void testScalableColorFeatureExtractor()
			throws Exception {
		
		String image = "/images/cluj_avram_iancu.jpg";
		String features = extractFeaturesAsString("net.semanticmetadata.lire.imageanalysis.ScalableColor", image);
		assertNotNull(features);
		assertEquals(features.length(), 167);
		System.out.println("Scalable Color for :" + image);
		System.out.println(features);
		
	}

	@Test
	public void testColorLayoutFeatureExtractor()
			throws Exception {
		
		final String image = "/images/cluj_avram_iancu.jpg";
		String features = extractFeaturesAsString("net.semanticmetadata.lire.imageanalysis.ColorLayout", "/images/cluj_avram_iancu.jpg");
		assertNotNull(features);
		//assertEquals(features.length(), 167);
		System.out.println("Color layout :" + image);
		System.out.println(features);
	}
	
}
