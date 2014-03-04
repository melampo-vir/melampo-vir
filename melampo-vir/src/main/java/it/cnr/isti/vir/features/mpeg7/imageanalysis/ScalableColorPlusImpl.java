package it.cnr.isti.vir.features.mpeg7.imageanalysis;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;

import net.semanticmetadata.lire.imageanalysis.LireFeature;
import net.semanticmetadata.lire.imageanalysis.ScalableColor;

public class ScalableColorPlusImpl extends ScalableColor {

	int hsHistLevels = 16;
	int[] hHist = new int[hsHistLevels];
	int[] sHist = new int[hsHistLevels]; 
	int[] hDistribution; 
	int[] sDistribution;
	public final static int THRESHOLD_DOMINANT_COLORS_SHARE = 85;
	public final static int THRESHOLD_DOMINANT_COLORS_COUNT = 3;
	
	private ImageType category = null; 
	private Integer thresholdDominantColorsShare;
	private Integer thresholdDominantColorsCount;
	
	public enum ImageType {GRAYSCALE, SEPIA, COLOR};
			
	@Override
	protected void _Quant(int H, int S, int V, int m, int n) {
		super._Quant(H, S, V, m, n);
		
		int hIndex = H / hsHistLevels;            // 256 levels = (16_hsHistLevels * 16_hsHistLevels)
		int sIndex = S / hsHistLevels;            // 256 levels = (16_hsHistLevels * 16_hsHistLevels)
		hHist[hIndex]++;
		sHist[sIndex]++;
   }

	protected int getDominantColorsShareOrDefault(){
		if(thresholdDominantColorsShare != null)
			return thresholdDominantColorsShare;
		else return THRESHOLD_DOMINANT_COLORS_SHARE;
	}
	
	protected int getDominantColorsCountOrDefault(){
		if(thresholdDominantColorsCount != null)
			return thresholdDominantColorsCount;
		else return THRESHOLD_DOMINANT_COLORS_COUNT;
	}
	
	public void setThresholdDominantColorsShare(int threshold){
		this.thresholdDominantColorsShare = threshold;
	}
	
	public void setThresholdDominantColorsCount(int threshold){
		this.thresholdDominantColorsCount = threshold;
	}
	
	public int[] getHDistribution(){
		if(hDistribution == null)
			hDistribution = computeOrderedDistribution(hHist);
		return hDistribution;
	}

	public int[] getSDistribution(){
		if(sDistribution == null)
			sDistribution = computeOrderedDistribution(sHist);
		
		return sDistribution; 
	}

	protected int[] computeOrderedDistribution(int[] histogram) {
		int[] distribution = Arrays.copyOf(histogram,histogram.length);
		Arrays.sort(distribution);
		float size = _ySize * _xSize;
		for (int i = 0; i < distribution.length; i++) {
			distribution[i] = (int)(distribution[i]*100 / size); 
		}
		return distribution;
	}
	
	protected int computeDominantValueCount(int[] distribution, int percentage){
		int sum = 0;
		int count = 0;
		for (int i = 0; i < distribution.length; i++) {
			sum+= distribution[distribution.length - (i+1)];//distribution is ordered in ascending order
			count++;
			if(sum >= percentage)
				return count;
		}		
		return count;
	}
	
	public int getDominantHCount(int percentage){
		return computeDominantValueCount(getHDistribution(), percentage);
	}
	
	public int getDominantSCount(int percentage){
		return computeDominantValueCount(getSDistribution(), percentage);
	}
	
	/**
	 * 
	 * @param dominantColorsShare - the share of the dominant colors in the image pixels count as percentage
	 */
	public void computeCategorization(int dominantColorsShare, int dominantComponentsCount){
		if(getDominantHCount(dominantColorsShare) <= dominantComponentsCount)
			if(getDominantSCount(dominantColorsShare) <= dominantComponentsCount)
				category = ImageType.GRAYSCALE;
			else
				category = ImageType.SEPIA;
		else
			category = ImageType.COLOR;
	}
	
	public ImageType getCategory(){
		if(category == null)
			computeCategorization(getDominantColorsShareOrDefault(), getDominantColorsCountOrDefault());
		return category;
	}
	
	public boolean isGrayScale(){
		return ImageType.GRAYSCALE.equals(getCategory());
	}
	
	public boolean isSepia(){
		return ImageType.SEPIA.equals(getCategory());
	}
	
	public boolean isColor(){
		return ImageType.COLOR.equals(getCategory());
	}
	
	protected LireFeature extractFeatures(String extractorClassName,
			File imageFile) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, IOException {
		
		InputStream in = new FileInputStream(imageFile);
				
		BufferedImage image = ImageIO.read(in);
		extract( image );
		return this;
	}
	
}
