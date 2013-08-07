package it.cnr.isti.vir.classification;

import it.cnr.isti.vir.util.ClassIDs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;

public class LabelClasses {
	static final Class[] idsLabel = {
		StringLabel.class
	};
	
//	static final Hashtable<Class, Integer> labelIDsHT = ClassIDs.getClassIDsHT(idsLabel);
	static final Constructor[] constructors  	= ClassIDs.getConstructors(idsLabel, DataInput.class);
	static final Constructor[] constructors_NIO  = ClassIDs.getConstructors(idsLabel, ByteBuffer.class);

	
	public static final Class getClass(int id) throws LabelClassException {
		return idsLabel[id];
	}
	
	public static final int getClassID_safe(Class featureClass)  {
		for ( int i=0; i<idsLabel.length; i++ ) {
			if ( featureClass.equals(idsLabel[i]))
				return i;
		}
		return -1;		
	}
	
	public static final int getClassID(Class featureClass) throws LabelClassException {
		int id = getClassID_safe(featureClass);
		if ( id<0)
			throw new LabelClassException("FeatureClass not found");
		return id;
	}
	
	public static final AbstractLabel readData(DataInput in ) throws IOException {
		int clID = in.readByte();
		
		if ( clID == -1 ) return null;
		
		try {
			return (AbstractLabel) constructors[clID].newInstance(in);//idClass.getConstructor(DataInput.class).newInstance(in);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static final AbstractLabel readData(ByteBuffer in ) throws IOException {
		int clID = in.get();
		
		if ( clID == -1 ) return null;
		
		try {
			return (AbstractLabel) constructors_NIO[clID].newInstance(in);//idClass.getConstructor(DataInput.class).newInstance(in);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}

	public static final void writeData(AbstractLabel cl, DataOutput out) throws IOException {
		if ( cl == null ) {
			out.writeByte((byte) -1);
			return;
		}
		Class c = cl.getClass();
		for ( int i=0; i<idsLabel.length; i++ ) {
			if ( cl.getClass().equals(idsLabel[i])) {
				out.writeByte((byte) i);
				cl.writeData( out );
				return;
			}			
		}	
		throw new IOException("ClassLabel not found");
	}
	
//	static final  Hashtable<Class, Integer> labelIDsHT = getFeaturesIDsHT();
//	static final  Hashtable<Integer, Class> idsLabelHT = getIDsFeaturesHT();
	
//	public static final Hashtable<Class, Integer> getFeaturesIDsHT() {
//		
//		Hashtable<Class, Integer> ht = new Hashtable<Class, Integer> ();
//		ht.put( ClassLabelString.class,	100);
//	
//		return ht;
//	}
//	
//	public static final Hashtable<Integer, Class> getIDsFeaturesHT() {
//		Hashtable<Integer, Class> ht = new Hashtable<Integer, Class>();
//		for ( Iterator<Map.Entry<Class, Integer>> it = labelIDsHT.entrySet().iterator(); it.hasNext(); ) {
//			Map.Entry<Class, Integer> curr = it.next();
//			ht.put(curr.getValue(), curr.getKey());
//		}
//		return ht; 
//	}

	
	
}
