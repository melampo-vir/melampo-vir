package it.cnr.isti.vir.id;

import it.cnr.isti.vir.util.ClassIDs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class IDClasses  {
	
	static final Class[] identifiersIDclass = {
//		Integer.class,
//		Long.class,
//		String.class,		
		IDInteger.class,
		IDLong.class,
		IDString.class
	};
	
	static final Hashtable<Class, Integer> idclassIentifiersHT = ClassIDs.getClassIDsHT(identifiersIDclass);
	static final Constructor[] constructors = ClassIDs.getConstructors(identifiersIDclass, DataInput.class);
	static final Constructor[] constructors_NIO = ClassIDs.getConstructors(identifiersIDclass, ByteBuffer.class);
	
//	static final  Hashtable<Integer, Class> identifiersIDclassHT = getIDsFeaturesHT();
	
	public static final Hashtable<Integer, Class> getIDsFeaturesHT() {
		Hashtable<Integer, Class> ht = new Hashtable<Integer, Class>();
		for ( Iterator<Map.Entry<Class, Integer>> it = idclassIentifiersHT.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<Class, Integer> curr = it.next();
			ht.put(curr.getValue(), curr.getKey());
		}
		return ht; 
	}
		
	public static final Class getClass(int id) {
		if ( id == -1 ) return null;
		//Class temp = identifiersIDclassHT.get(id);
		//if ( temp == null ) throw new Exception("Feature class id not found");
		return identifiersIDclass[id];
	}
	
	public static final int getClassID(Class featureClass) {
		if ( featureClass == null ) return -1;
		Integer id = idclassIentifiersHT.get(featureClass);
		//if ( id == null ) throw new Exception("Feature class not found");
		return id;
	}
	
	public static final IID readData(DataInput in ) throws IOException {
		int idInt = in.readByte();
		
		if ( idInt == -1 ) return null;
		
		try {
			// work around for old Cophir
//			if ( idInt == 1001 ) return (IID) constructors[0].newInstance(in);
			return (IID) constructors[idInt].newInstance(in);//idClass.getConstructor(DataInput.class).newInstance(in);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static final IID readData(ByteBuffer in ) throws IOException {
		int idInt = in.get();
		
		if ( idInt == -1 ) return null;
		
		try {
			// work around for old Cophir
//			if ( idInt == 1001 ) return (IID) constructors[0].newInstance(in);
			return (IID) constructors_NIO[idInt].newInstance(in);//idClass.getConstructor(DataInput.class).newInstance(in);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static final void writeData(IID id, DataOutput out) throws IOException {
		out.writeByte( getClassID(id.getClass()) );		
		id.writeData(out);
	}
	
	public static Class readClass(DataInput in) throws IOException {
		return getClass(in.readInt());
	}

	public static void writeClass(Class c, DataOutput out) throws IOException {
		System.out.println("class id= " + getClassID(c));
		out.writeInt(getClassID(c));
	}
	
	
	public static final IID[] readArray(DataInput in, int n, Class idClass) throws IOException {
		if ( idClass.equals(IDString.class) ) {
			return IDString.readArray(in, n);
		}
		if ( idClass.equals(IDInteger.class) ) {
			return IDInteger.readArray(in, n);
		}
		if ( idClass.equals(IDLong.class) ) {
			return IDLong.readArray(in, n);
		}
		throw new IOException("idClass not found");
	}
}
