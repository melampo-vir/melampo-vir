package it.cnr.isti.vir.similarity.metric;

import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.lire.vd.LireColorLayout;
import it.cnr.isti.vir.features.lire.vd.LireEdgeHistogram;
import it.cnr.isti.vir.features.lire.vd.LireScalableColor;

public class LireMetric implements Metric<IFeaturesCollector> {
	
	private static long distCount = 0;
	public static final FeatureClassCollector reqFeatures = new FeatureClassCollector(
			LireColorLayout.class,
			LireScalableColor.class,
			LireEdgeHistogram.class );
		
	public final long getDistCount() {
		return distCount;
	}
	
	public static final double wSAPIR_Norm = 1.0 / ( 1.5+4.5+2.5 ); 

	public static final double[] wSAPIR = {
		1.5  * 1.0/300.0,  //CL
		4.5  * 1.0/68.0, //EH
		2.5  * 1.0/3000.0  //SC
	};
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2 ) {
	
		return distance(f1,f2, Double.MAX_VALUE);
	}
	
	
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2, double max ) {
		distCount++;
		double dist = 0;

		//EH
		dist += wSAPIR[1] * LireEdgeHistogram.mpeg7XMDistance( (LireEdgeHistogram) f1.getFeature(LireEdgeHistogram.class), (LireEdgeHistogram) f2.getFeature(LireEdgeHistogram.class) );
		if ( dist > max ) return -dist;
		
		//SC
		dist += wSAPIR[2] * LireScalableColor.mpeg7XMDistance( (LireScalableColor) f1.getFeature(LireScalableColor.class), (LireScalableColor) f2.getFeature(LireScalableColor.class) );
		if ( dist > max ) return -dist;	
		
		//CL
		dist += wSAPIR[0] * LireColorLayout.mpeg7XMDistance( (LireColorLayout) f1.getFeature(LireColorLayout.class), (LireColorLayout) f2.getFeature(LireColorLayout.class) );
		if ( dist > max ) return -dist;	
			
		return dist;
	}
	
	public String toString() {
		return this.getClass().toString();
	}
}
