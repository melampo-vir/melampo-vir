package it.cnr.isti.vir.features.mpeg7.imageanalysis;

import static org.junit.Assert.*;
import it.cnr.isti.vir.features.BaseFeatureExtraction;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import net.semanticmetadata.lire.imageanalysis.LireFeature;

import org.junit.Test;

public class TestScalableColorPlusExtractor extends BaseFeatureExtraction {

	@Test
	public void testScalableColorPlusFeatureExtractorColor() throws Exception {

		System.out.println("TEST WITH COLOR IMAGE ......");
		String image = "/images/cluj_avram_iancu.jpg";
		ScalableColorPlusImpl featureExtractor = performFeatureExtraction(image);
		printHSDistributions(featureExtractor);
		// assertTrue(featureExtractor.isColor());
	}

	@Test
	public void testScalableColorPlusFeatureExtractorGray() throws Exception {

		System.out.println("TEST WITH (almost) GRAYSCALE IMAGE ......");

		String image = "/images/grayscale/amersfoort11.jpg";
		ScalableColorPlusImpl featureExtractor = performFeatureExtraction(image);
		printHSDistributions(featureExtractor);
		// assertTrue(featureExtractor.isGrayScale());
	}

	@Test
	public void testScalableColorPlusFeatureExtractorSepia() throws Exception {

		System.out.println("TEST WITH (almost) SEPIA IMAGE ......");
		String image = "/images/sepia/amersfoort32.jpg";
		ScalableColorPlusImpl featureExtractor = performFeatureExtraction(image);
		printHSDistributions(featureExtractor);
		// assertTrue(featureExtractor.isColor());
	}

	@Test
	public void testGrayScaleDetection() throws Exception {

		final String folderName = "/images/grayscale/";
		performFeatureExtractionForAll(folderName, true, false);

		// System.out.println("TEST WITH (almost) SEPIA IMAGE ......");
		// String image = "/images/sepia/amersfoort31.jpg";
		// ScalableColorPlusImpl featureExtractor =
		// performFeatureExtraction(image);
		// printHSDistributions(featureExtractor);
		// assertTrue(featureExtractor.isColor());
	}

	protected void performFeatureExtractionForAll(final String folderName, boolean grayscale, boolean sepia)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, IOException {
		URL imageFolder = getClass().getResource(folderName);
		File[] grayScaleImages = new File(imageFolder.getFile())
				.listFiles();

		for (int i = 0; i < grayScaleImages.length; i++) {
			ScalableColorPlusImpl featureExtractor = performFeatureExtraction(folderName
					+ grayScaleImages[i].getName());
			printHSDistributions(featureExtractor);
			if(grayscale) assertTrue(featureExtractor.isGrayScale());
			if(sepia) assertTrue(featureExtractor.isSepia());
		}
	}

	@Test
	public void testSepiaDetection() throws Exception {
		final String folderName = "/images/sepia/";
		performFeatureExtractionForAll(folderName, false, true);
		
	}

	protected void printHSDistributions(ScalableColorPlusImpl featureExtractor) {
		System.out.println("hHist: " + Arrays.toString(featureExtractor.hHist));
		System.out.println("hDistribution: "
				+ Arrays.toString(featureExtractor.getHDistribution()));
		final int threshold = 85;
		System.out.println("hDominantValuesCount: "
				+ featureExtractor.getDominantHCount(threshold));
		System.out.println("sHist: " + Arrays.toString(featureExtractor.sHist));
		System.out.println("sDistribution: "
				+ Arrays.toString(featureExtractor.getSDistribution()));
		System.out.println("sDominantValuesCount: "
				+ featureExtractor.getDominantSCount(threshold));
	}

	protected ScalableColorPlusImpl performFeatureExtraction(String image)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, IOException {
		LireFeature featureExtractor = extractFeatures(
				"it.cnr.isti.vir.features.mpeg7.imageanalysis.ScalableColorPlusImpl",
				image);
		assertNotNull(featureExtractor);
		System.out.println("Scalable Color for :" + image);
		return (ScalableColorPlusImpl) featureExtractor;
	}
}
