package it.cnr.isti.vir.features;

import it.cnr.isti.vir.features.lire.vd.CcDominantColor;
import it.cnr.isti.vir.features.lire.vd.LireCEDD;
import it.cnr.isti.vir.features.lire.vd.LireColorLayout;
import it.cnr.isti.vir.features.lire.vd.LireColorLayout2;
import it.cnr.isti.vir.features.lire.vd.LireEdgeHistogram;
import it.cnr.isti.vir.features.lire.vd.LireEdgeHistogram2;
import it.cnr.isti.vir.features.lire.vd.LireFCTH;
import it.cnr.isti.vir.features.lire.vd.LireScalableColor;
import it.cnr.isti.vir.features.lire.vd.LireScalableColor2;
import it.cnr.isti.vir.features.metadata.GPSData;
import it.cnr.isti.vir.util.ClassIDs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class FeatureClasses {

	static final Class[] idsFeatures = {
		GPSData.class,
		LireScalableColor.class,
		LireColorLayout.class,
		LireEdgeHistogram.class,
		LireColorLayout2.class,
		LireScalableColor2.class,
		LireEdgeHistogram2.class,
		LireFCTH.class,
		LireCEDD.class,
		CcDominantColor.class
	};
	
	static final Hashtable<Class, Integer> featuresIDsHT = ClassIDs.getClassIDsHT(idsFeatures);
	static final Constructor[] constructors  = ClassIDs.getConstructors(idsFeatures, DataInput.class);
	static final Constructor[] constructors2 = ClassIDs.getConstructors(idsFeatures, DataInput.class, IFeaturesCollector.class);
	static final Constructor[] constructors_NIO  = ClassIDs.getConstructors(idsFeatures, ByteBuffer.class);
	static final Constructor[] constructors2_NIO = ClassIDs.getConstructors(idsFeatures, ByteBuffer.class, IFeaturesCollector.class);
	

	public static final void writeData(DataOutput out, IFeature fc ) throws IOException {
		out.writeByte(getClassID(fc.getClass()));
		fc.writeData(out);
	}
	
	public static final Hashtable<Integer, Class> getIDsFeaturesHT() {
		Hashtable<Integer, Class> ht = new Hashtable<Integer, Class>();
		for ( Iterator<Map.Entry<Class, Integer>> it = featuresIDsHT.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<Class, Integer> curr = it.next();
			ht.put(curr.getValue(), curr.getKey());
		}
		return ht; 
	}
	
	public static final Class getClass(int id)  {
		return idsFeatures[id];
	}
	
	public static final byte getClassID(Class featureClass) {
		Integer id = featuresIDsHT.get(featureClass);
		if ( id == null ) {
			System.err.println("Feature class " + featureClass.getName() + "not found");
			return -1;
		}
//		if ( id == null ) throw new Exception("Feature class not found");
		return id.byteValue();
	}
	
	
	
	public static final IFeature readData(DataInput in ) throws IOException {
		return readData(in, null);
	}
	
	public static final IFeature readData(ByteBuffer in, IFeaturesCollector fc ) throws IOException {
		byte id = in.get();

		try {
			if ( constructors2_NIO[id] != null )
				return (IFeature) constructors2_NIO[id].newInstance(in, fc);
			if ( constructors_NIO[id] != null )
				return (IFeature) constructors_NIO[id].newInstance(in);
		} catch (Exception e2) {
			e2.printStackTrace();
			return null;
		}
		return null;
		
	}
	
	public static final IFeature readData(DataInput in, IFeaturesCollector fc ) throws IOException {
		byte id = in.readByte();

		try {
			if ( constructors2[id] != null )
				return (IFeature) constructors2[id].newInstance(in, fc);
			if ( constructors[id] != null )
				return (IFeature) constructors[id].newInstance(in);
		} catch (Exception e2) {
			e2.printStackTrace();
			return null;
		}
		return null;
		
	}
}
