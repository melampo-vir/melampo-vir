package it.cnr.isti.vir.features;

import it.cnr.isti.vir.util.ClassIDs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Hashtable;

public class FeaturesCollectors {

	static final Class[] fccIDclass = {
		FeaturesCollectorArr.class };
	
	static final Hashtable<Class, Integer> idclassFCCHT = ClassIDs.getClassIDsHT(fccIDclass);
	static final Constructor[] constructors 	= ClassIDs.getConstructors(fccIDclass, DataInput.class);
	static final Constructor[] constructorsNIO 	= ClassIDs.getConstructors(fccIDclass, ByteBuffer.class);
	
//	static final Hashtable<Class, Integer> idclassFCCHT = getFCCIDsHT();
	//static final  Hashtable<Integer, Class> fccIDclassHT = getIDsFCCHT();										
	//	static final byte version = 0;
	
//	public static final Hashtable<Class, Integer> getFCCIDsHT() {
//		Hashtable<Class, Integer> temp = new Hashtable<Class, Integer> ();
//		for ( int i=0; i<fccIDclass.length; i++) {
//			temp.put( fccIDclass[i], i);
//		}
//		return temp;
//	}
//	public static final Hashtable<Class, Integer> getFCCIDsHT() {
//		
//		Hashtable<Class, Integer> temp = new Hashtable<Class, Integer> ();
//		int i=1;
//		temp.put( FeaturesCollectorHT.class, 				i++);
//		temp.put( FeaturesCollectorHTwithID.class, 			i++);
//		temp.put( FeaturesCollectorHTwithIDClassified.class,i++);
//		temp.put( SAPIRObject.class, 						i++);
//		
//		return temp;
//	}
//	
//	public static final Hashtable<Integer, Class> getIDsFCCHT() {
//		Hashtable<Integer, Class> ht = new Hashtable<Integer, Class>();
//		for ( Iterator<Map.Entry<Class, Integer>> it = idclassFCCHT.entrySet().iterator(); it.hasNext(); ) {
//			Map.Entry<Class, Integer> curr = it.next();
//			ht.put(curr.getValue(), curr.getKey());
//		}
//		return ht; 
//	}
	
	public static final Class getClass(int id)  {
		if ( id < 0 ) return null;
		return fccIDclass[id];
	}
	
	public static final Integer getClassID(Class featureClass) {
		Integer id = idclassFCCHT.get(featureClass);
		if ( id == null ) System.err.println("FeatureCollector class " + featureClass.getName() + "not found");
//		if ( id == null ) throw new Exception("Feature class not found");
		return id;
	}
	
	public static final IFeaturesCollector readData(DataInput in ) throws IOException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
//		byte version = in.readByte();
		int fccID = in.readInt();
	
		return (IFeaturesCollector) constructors[fccID].newInstance(in); 
	}

	public static final IFeaturesCollector readData(ByteBuffer buf ) throws IOException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		int fccID = buf.getInt();
		
		return (IFeaturesCollector) constructorsNIO[fccID].newInstance(buf); 
	}
	
	public static final void writeData( DataOutput out, IFeaturesCollector fc) throws IOException {
		out.writeInt( getClassID(fc.getClass()) );
		fc.writeData(out);
	}

	public static Class readClass( DataInput in ) throws IOException {
		return getClass(in.readInt());
	}

	public static void writeClass(Class fcClass, DataOutput out) throws IOException {
		if ( fcClass == null ) out.writeInt(-1);
		else out.writeInt(getClassID(fcClass));		
	}
	
}
