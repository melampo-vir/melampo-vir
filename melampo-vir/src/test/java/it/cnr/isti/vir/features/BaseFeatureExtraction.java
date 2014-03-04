package it.cnr.isti.vir.features;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.semanticmetadata.lire.imageanalysis.LireFeature;

public class BaseFeatureExtraction {

	public BaseFeatureExtraction() {
		super();
	}

	protected String extractFeaturesAsString(String extractorClassName, String fileName)
			throws Exception {
				
				LireFeature featureExtractor = extractFeatures(
						extractorClassName, fileName);
				String features = featureExtractor.getStringRepresentation();
				System.out.println(features);
				return features;
			}

	protected LireFeature extractFeatures(String extractorClassName,
			String fileName) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, IOException {
		
		LireFeature featureExtractor = (LireFeature)Class.forName(extractorClassName)
				.newInstance();
				InputStream in = getClass().getResourceAsStream(fileName);
				
		BufferedImage image = ImageIO.read(in);
		featureExtractor.extract( image );
		return featureExtractor;
	}

}