package it.cnr.isti.vir.features.lire;

import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.lire.vd.LireBasicFeatures;
import it.cnr.isti.vir.features.lire.vd.LireCEDD;
import it.cnr.isti.vir.features.lire.vd.LireColorLayout2;
import it.cnr.isti.vir.features.lire.vd.LireColorLayout;
import it.cnr.isti.vir.features.lire.vd.LireEdgeHistogram2;
import it.cnr.isti.vir.features.lire.vd.LireEdgeHistogram;
import it.cnr.isti.vir.features.lire.vd.LireFCTH;
import it.cnr.isti.vir.features.lire.vd.LireFeatures;
import it.cnr.isti.vir.features.lire.vd.LireFuzzyColorHistogram;
import it.cnr.isti.vir.features.lire.vd.LireGabor;
import it.cnr.isti.vir.features.lire.vd.LireGeneralColorLayout;
import it.cnr.isti.vir.features.lire.vd.LireHSVColorHistogram;
import it.cnr.isti.vir.features.lire.vd.LireJpegCoefficientHistogram;
import it.cnr.isti.vir.features.lire.vd.LireScalableColor2;
import it.cnr.isti.vir.features.lire.vd.LireScalableColor;
import it.cnr.isti.vir.features.lire.vd.LireSimpleColorHistogram;
import it.cnr.isti.vir.features.lire.vd.LireTamura;
import it.cnr.isti.vir.features.lire.vd.VirLireFeatures;
import it.cnr.isti.vir.similarity.metric.Metric;

public class LireMetric2 implements Metric<IFeaturesCollector> {
	
	private static long distCount = 0;
	public static  FeatureClassCollector reqFeatures;
	private LireFeatures.FEATURES feature = LireFeatures.FEATURES.FCTH;
		
	public final long getDistCount() {
		return distCount;
	}
	
	public static final double wSAPIR_Norm = 1.0 / ( 1.5+4.5+2.5 ); 

	public static final double[] wSAPIR = {
		1.5  * 1.0/300.0,  //CL
		4.5  * 1.0/68.0, //EH
		2.5  * 1.0/3000.0  //SC
	};
	
	public void setRequestedFeaturesClasses(Class... featureClasses) {		
		reqFeatures = new FeatureClassCollector(featureClasses);
	}
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2 ) {
	
		return distance(f1,f2, Double.MAX_VALUE);
	}
	
	public void setFeature(LireFeatures.FEATURES feature) {
		this.feature = feature;
	}
	
	
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2, double max ) {
		distCount++;
		double dist = Double.MAX_VALUE;
		//double dist = VirLireFeatures.mpeg7XMDistance(( VirLireFeatures)f1.getFeature(VirLireFeatures.class), ( VirLireFeatures)f2.getFeature(VirLireFeatures.class));
		try {
			switch (feature) {
         	case FCTH:
         		dist = LireFCTH.mpeg7XMDistance( (LireFCTH) f1.getFeature(LireFCTH.class), (LireFCTH) f2.getFeature(LireFCTH.class) );
         		break;
         	case CEDD:
         		dist = LireCEDD.mpeg7XMDistance( (LireCEDD) f1.getFeature(LireCEDD.class), (LireCEDD) f2.getFeature(LireCEDD.class) );
         		break;
         	case FuzzyColorHistogram:
         		dist = LireFuzzyColorHistogram.mpeg7XMDistance( (LireFuzzyColorHistogram) f1.getFeature(LireFuzzyColorHistogram.class), (LireFuzzyColorHistogram) f2.getFeature(LireFuzzyColorHistogram.class) );
         		break;
         	case EdgeHistogram:
         		dist = LireEdgeHistogram2.mpeg7XMDistance( (LireEdgeHistogram2) f1.getFeature(LireEdgeHistogram2.class), (LireEdgeHistogram2) f2.getFeature(LireEdgeHistogram2.class) );
         		break;
         	case ScalableColor:
         		dist = LireScalableColor2.mpeg7XMDistance( (LireScalableColor2) f1.getFeature(LireScalableColor2.class), (LireScalableColor2) f2.getFeature(LireScalableColor2.class) );
         		break;
         	case ColorLayout:
         		dist = LireColorLayout2.mpeg7XMDistance( (LireColorLayout2) f1.getFeature(LireColorLayout2.class), (LireColorLayout2) f2.getFeature(LireColorLayout2.class) );
         		break;
         	case BasicFeatures:
         		dist = LireBasicFeatures.mpeg7XMDistance( (LireBasicFeatures) f1.getFeature(LireBasicFeatures.class), (LireBasicFeatures) f2.getFeature(LireBasicFeatures.class) );
         		break;
         	case Gabor:
         		dist = LireGabor.mpeg7XMDistance( (LireGabor) f1.getFeature(LireGabor.class), (LireGabor) f2.getFeature(LireGabor.class) );
         		break;
         	case GeneralColorLayout:
         		dist = LireGeneralColorLayout.mpeg7XMDistance( (LireGeneralColorLayout) f1.getFeature(LireGeneralColorLayout.class), (LireGeneralColorLayout) f2.getFeature(LireGeneralColorLayout.class) );
         		break;
         	case HSVColorHistogram:
         		dist = LireHSVColorHistogram.mpeg7XMDistance( (LireHSVColorHistogram) f1.getFeature(LireHSVColorHistogram.class), (LireHSVColorHistogram) f2.getFeature(LireHSVColorHistogram.class) );
         		break;
         	case JpegCoefficientHistogram:
         		dist = LireJpegCoefficientHistogram.mpeg7XMDistance( (LireJpegCoefficientHistogram) f1.getFeature(LireJpegCoefficientHistogram.class), (LireJpegCoefficientHistogram) f2.getFeature(LireJpegCoefficientHistogram.class) );
         		break;
         	case SimpleColorHistogram:
         		dist = LireSimpleColorHistogram.mpeg7XMDistance( (LireSimpleColorHistogram) f1.getFeature(LireSimpleColorHistogram.class), (LireSimpleColorHistogram) f2.getFeature(LireSimpleColorHistogram.class) );
         		break;
         	case Tamura:
         		dist = LireTamura.mpeg7XMDistance( (LireTamura) f1.getFeature(LireTamura.class), (LireTamura) f2.getFeature(LireTamura.class) );
         		break;
         	default:
                System.out.println("Error retrieving lire feature metric");
                break;
		 }
		} catch (Exception e) {
			dist = Double.MAX_VALUE;
		}
		
         		
         	
         		
//		//EH
//		dist += wSAPIR[1] * LireEdgeHistogram.mpeg7XMDistance( (LireEdgeHistogram) f1.getFeature(LireEdgeHistogram.class), (LireEdgeHistogram) f2.getFeature(LireEdgeHistogram.class) );
//		if ( dist > max ) return -dist;
//		
//		//SC
//		dist += wSAPIR[2] * LireScalableColor.mpeg7XMDistance( (LireScalableColor) f1.getFeature(LireScalableColor.class), (LireScalableColor) f2.getFeature(LireScalableColor.class) );
//		if ( dist > max ) return -dist;	
//		
//		//CL
//		dist += wSAPIR[0] * LireColorLayout.mpeg7XMDistance( (LireColorLayout) f1.getFeature(LireColorLayout.class), (LireColorLayout) f2.getFeature(LireColorLayout.class) );
		if ( dist > max ) return -dist;	
			
		return dist;
	}
	
	public String toString() {
		return this.getClass().toString();
	}
}
