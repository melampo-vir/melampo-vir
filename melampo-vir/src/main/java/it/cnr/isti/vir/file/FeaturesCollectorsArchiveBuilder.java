package it.cnr.isti.vir.file;

import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.FeaturesCollectorException;
import it.cnr.isti.vir.features.FeaturesCollectors;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.id.IDClasses;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.id.IID;
import it.cnr.isti.vir.readers.CoPhIRv2Reader;
import it.cnr.isti.vir.readers.ObjectsReader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;

public class FeaturesCollectorsArchiveBuilder {
	
	private final FeatureClassCollector featuresClasses;
	private ArrayList<Long> tempOffsetList = new ArrayList();
	private ArrayList<IID> tempIDList = new ArrayList();
	private Class idClass = null;
	private final RandomAccessFile out;
	private long lastObjOffset = 0;
	
	public FeaturesCollectorsArchiveBuilder( 	File outFile,
												FeatureClassCollector featuresClasses
											) throws IOException, SecurityException, NoSuchMethodException, FeaturesCollectorException {
		this(outFile, featuresClasses, null);
	}
	
	
	public FeaturesCollectorsArchiveBuilder( 	File outFile,
												FeatureClassCollector featuresClasses,
												Class idClass) throws IOException, SecurityException, NoSuchMethodException, FeaturesCollectorException {
		
		
		
		this.featuresClasses = featuresClasses;
		this.idClass = idClass;
		
		if ( outFile.exists() ) {
			outFile.delete();
		}
		
		out = new RandomAccessFile(outFile, "rw");
		//System.out.println("StartOffset: " + out.getFilePointer() + " - fileLength: " + out.length());
		
		out.writeLong(FeaturesCollectorsArchive.fileID);
		out.writeInt(1);
		
		out.writeLong(0); // will be used to store index offset;
		//		out.writeInt(FeaturesClassCollector.getClassID(featuresCollectorClass));
		
		// writes the Feature Classes
		this.featuresClasses.writeData(out);
		IDClasses.writeClass( idClass, out );
		
	}
	
	public void addAll( ObjectsReader reader ) throws IOException, FeaturesCollectorException, VIRFileException {
		IFeaturesCollector fc;
		
		//LinkedList<FeaturesCollectorHTwithID> list = new LinkedList<FeaturesCollectorHTwithID>();
		int count = 0;
		while ( true ) {
			try {
				fc = (IFeaturesCollector) reader.getObj();
				
			} catch ( Exception e) {
				e.printStackTrace();
				continue;
			}						
			if (fc == null) {
				break;
			}
			count++;
			
			this.add(fc);
//			FeaturesCollectors.writeData(out, tr);
						
			//list.add(tr);
								
			System.out.println(count + "-th object ["+ ((IHasID) fc).getID() +"] inserted (archive size: "+ tempIDList.size() + ")");
		
		}
	}
	
	public void addAll_OnePerFile( ObjectsReader reader ) throws IOException, FeaturesCollectorException, VIRFileException {
		IFeaturesCollector fc;
		
		int count = 0;
		while ( true ) {
			try {
				fc = (IFeaturesCollector) ((CoPhIRv2Reader) reader).getObj_OnePerFile();
				
			} catch ( Exception e) {
				e.printStackTrace();
				continue;
			}						
			if (fc == null) {
				break;
			}
			count++;
			
			this.add(fc);
								
			System.out.println(count + "-th object ["+ ((IHasID) fc).getID() +"] inserted (archive size: "+ tempIDList.size() + ")");
		
		}
	}
	
	public synchronized void add( IFeaturesCollector fc	) throws IOException, FeaturesCollectorException, VIRFileException {
			
			IID id = null;	
			if ( idClass != null ) {
				id = ((IHasID) fc).getID();
				if ( !idClass.isInstance(id) ) {
					throw new VIRFileException("ID classes not consistent. Requested " + idClass.getName() + ", found: " + id.getClass().getName());
				}
			}
			
			fc.discardAllBut(featuresClasses);
			lastObjOffset = out.getFilePointer();
			tempOffsetList.add(lastObjOffset);
			tempIDList.add(id);
			FeaturesCollectors.writeData(out, fc);
			
// DEBUG !!!!!!!!!!!			
//			try {
//				if ( !fc.equals(readLast()) ) {
//					throw new VIRFileException("Saved object and read object diffear.");
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				throw new VIRFileException();
//			}
	}
	
	public IFeaturesCollector readLast() throws IOException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		out.seek(lastObjOffset);
		return FeaturesCollectors.readData(out);
	}
		
		
	public void close() throws IOException {
		System.out.println(tempOffsetList.size() + " objects were found.");
		long[] positions = new long[tempOffsetList.size()];
		for (int i=0; i<tempOffsetList.size(); i++) {
			positions[i] = tempOffsetList.get(i);
		}
		tempOffsetList = null;
		
		// writing index
		System.out.print("Writing index... ");
		long indexOffset = out.getFilePointer();
		
		out.writeInt(positions.length);
		byte[] byteArray = new byte[positions.length*8];
		LongBuffer outLongBuffer = ByteBuffer.wrap(byteArray).asLongBuffer();
		outLongBuffer.put(positions, 0, positions.length);
		out.write(byteArray);
		System.out.println("done.");
		
		if ( idClass != null ) {
			// writing IDs
			System.out.print("Writing IDs... ");
			long idOffset = out.getFilePointer();
			for ( int i=0; i<positions.length; i++) {
				tempIDList.get(i).writeData(out);
			}
			System.out.println("done.");
			
//			System.out.println("idOffset: " + idOffset);
			out.writeLong(idOffset);
		}
		
//		System.out.println("indexOffset: " + indexOffset);
		
		out.seek(12);
		out.writeLong(indexOffset);
		out.close();
	}
	
	public int size() {
		return tempOffsetList.size();
	}
	
}
