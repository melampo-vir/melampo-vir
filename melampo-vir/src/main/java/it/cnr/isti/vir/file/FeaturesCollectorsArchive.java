package it.cnr.isti.vir.file;

import gnu.trove.TLongArrayList;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.FeaturesCollectors;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.id.IDClasses;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.id.IID;
import it.cnr.isti.vir.similarity.ISimilarityResults;
import it.cnr.isti.vir.similarity.Similarity;
import it.cnr.isti.vir.similarity.pqueues.SimPQueueArr;
import it.cnr.isti.vir.util.ParallelOptions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.davidsoergel.conja.Function;
import com.davidsoergel.conja.Parallel;

public class FeaturesCollectorsArchive implements Iterable<IFeaturesCollector> {

	public static final long fileID = 0x5a25287d3aL;

	public static final int version = 2;

	private final TLongArrayList positions;
	private final ArrayList<IID> ids;
	// private final HashMap<IID,Long> idOffsetMap;
	private final HashMap<IID, Integer> idPosMap;

	private final RandomAccessFile rndFile;
	private final File f;
	private final File offsetFile;
	private final File idFile;
	private final FeatureClassCollector featuresClasses;
	private final Constructor fcClassConstructor;
	private final Constructor fcClassConstructor_NIO;
	
	private final Class idClass;

	private boolean changed = false;
	private boolean forceOverwrite = false;

	public boolean isForceOverwrite() {
		return forceOverwrite;
	}

	public void setForceOverwrite(boolean overwrite) {
		this.forceOverwrite = overwrite;
	}

	private final Class fcClass;

	public FeatureClassCollector getFeaturesClasses() {
		return featuresClasses;
	}

	public Class getIdClass() {
		return idClass;
	}

	public Class getFcClass() {
		return fcClass;
	}

	public int size() {
		return positions.size();
	}

	public Class getIDClass() {
		return idClass;
	}

	public final IID getID(int i) {
		return ids.get(i);
	}

	public FeaturesCollectorsArchive(File file,
			FeatureClassCollector featuresClasses, Class idClass, Class fcClass)
			throws Exception {
		
		if(isForceOverwrite())
			file.delete();
		
		rndFile = new RandomAccessFile(file, "rw");
		this.f = file;
		this.featuresClasses = featuresClasses;
		this.idClass = idClass;
		this.fcClass = fcClass;
		this.fcClassConstructor = getFCConstructor(fcClass);
		this.fcClassConstructor_NIO = getFCConstructor_NIO(fcClass);
		this.positions = new TLongArrayList();
		this.ids = new ArrayList();
		// this.idOffsetMap = new HashMap();
		this.idPosMap = new HashMap();
		offsetFile = new File(getOffsetFileName(file));
		idFile = new File(getIDFileName(file));

		writeIntro(rndFile, featuresClasses, idClass, fcClass);
	}

	public static final void writeIntro(DataOutput out,
			FeatureClassCollector featuresClasses, Class idClass, Class fcClass)
			throws Exception {
		out.writeLong(fileID);
		out.writeInt(version);
		FeaturesCollectors.writeClass(fcClass, out);
		featuresClasses.writeData(out);
		IDClasses.writeClass(idClass, out);
	}

	protected static final Constructor getFCConstructor(Class c)
			throws SecurityException, NoSuchMethodException {
		if (c == null)
			return null;
		return c.getConstructor(DataInput.class);
	}

	protected static final Constructor getFCConstructor_NIO(Class c)
			throws SecurityException, NoSuchMethodException {
		if (c == null)
			return null;
		return c.getConstructor(ByteBuffer.class);
	}

	public void add(IFeaturesCollector fc) throws ArchiveException, IOException {

		if (idClass != null) {
			IID id = ((IHasID) fc).getID();
			if (!idClass.isInstance(id)) {
				throw new ArchiveException("Objecct has a wrong ID class: "
						+ idClass + " requeste, " + id.getClass() + " found.");
			}
			ids.add(id);
		}

		positions.add(rndFile.length());

		rndFile.seek(rndFile.length());

		if (fcClass == null) {
			FeaturesCollectors.writeData(rndFile, fc);
		} else {
			if (fcClass.isInstance(fc)) {
				fc.writeData(rndFile);
			} else {
				throw new ArchiveException("FeaturesCollector class inserted ("
						+ fc.getClass() + ") diffear from expected (" + fcClass
						+ ")");
			}
		}

		changed = true;
	}

	public final File getfile() {
		return f;
	}

	public final ArrayList<IFeaturesCollector> getAll() throws IOException,
			SecurityException, NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(f)));
		ArrayList<IFeaturesCollector> arr = new ArrayList(ids.size());

		if (in.readLong() != fileID) {
			System.err
					.println("The file does not appear to be a FeatureArchive");
		}

		int fileVersion = in.readInt();

		if (fileVersion > 1) {
			Class fcClass = FeaturesCollectors.readClass(in);
			Constructor fcClassConstructor = getFCConstructor(fcClass);
			new FeatureClassCollector(in);
			IDClasses.readClass(in);

			arr = new ArrayList();
			while (in.available() != 0) {
				if (fcClassConstructor == null) {
					arr.add(FeaturesCollectors.readData(in));
				} else {
					arr.add((IFeaturesCollector) fcClassConstructor
							.newInstance(in));
				}

			}
		} else {
			// old IO
			long indexOffSet = in.readLong();

			FeatureClassCollector featuresClasses = new FeatureClassCollector(
					in); // FeaturesCollectors.getClass( file.readInt() );
			Class idClass = IDClasses.readClass(in);

			for (int i = 0; i < ids.size(); i++) {
				arr.add(FeaturesCollectors.readData(in));
			}
		}

		return arr;
	}
	
	public synchronized ISimilarityResults[] getKNN_IDs(IFeaturesCollector[] qObj, int k, final Similarity sim ) throws SecurityException, IllegalArgumentException, IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		return getKNN(qObj, k, sim, true);
	}

	public synchronized ISimilarityResults[] getKNN(IFeaturesCollector[] qObj, int k, final Similarity sim ) throws SecurityException, IllegalArgumentException, IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		return getKNN(qObj, k, sim, false);
	}
	
	public synchronized ISimilarityResults[] getKNN(IFeaturesCollector[] qObj, int k,
			final Similarity sim, final boolean onlyID) throws IOException, SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(f)));
		
		final int nObj = positions.size();

		if (in.readLong() != fileID) {
			// System.err.println("The file does not appear to be a FeatureArchive");
		}

		int fileVersion = in.readInt();

		SimPQueueArr[] kNNQueue = new SimPQueueArr[qObj.length];
		for (int i = 0; i < kNNQueue.length; i++) {
			kNNQueue[i] = new SimPQueueArr(k);
		}

		if (fileVersion > 1) {
			Class fcClass = FeaturesCollectors.readClass(in);
			Constructor fcClassConstructor = getFCConstructor(fcClass);
			new FeatureClassCollector(in);
			IDClasses.readClass(in);

			// to DO !
			boolean parallel = true;
			if (!parallel) {
				
				for (int iObj = 0; iObj < nObj; iObj++) {
					// while( in.available() != 0) {IFeaturesCollector fc =
					IFeaturesCollector fc = null;
					if (fcClassConstructor == null) {
						fc = FeaturesCollectors.readData(in);
					} else {
						fc = (IFeaturesCollector) fcClassConstructor.newInstance(in);
					}

					for (int i = 0; i < kNNQueue.length; i++) {
						if ( onlyID)
							kNNQueue[i].offer(((IHasID) fc).getID(), sim.distance(qObj[i], fc));
						else 
							kNNQueue[i].offer(fc, sim.distance(qObj[i], fc));
					}

					if ( (iObj+1) % 100 == 0 ) {
						System.out.println( (iObj+1) + " of " + nObj + " processed.");
					}
				}				
			
			} else {

				final int parallelBatchSize = 1000;
				final IFeaturesCollector[] obj = new IFeaturesCollector[parallelBatchSize];
							
				// For parallel
				final IFeaturesCollector[] q = qObj;
				final SimPQueueArr[] kNNs = kNNQueue;
				
				final int nObjPerThread = (int) Math.ceil(kNNQueue.length / ParallelOptions.nThreads);
				// final int nObjPerThread = kNNQueue.length;
				ArrayList<Integer> arrList = new ArrayList(kNNQueue.length);
				for (int iO = 0; iO < kNNQueue.length; iO += nObjPerThread) {
					arrList.add(iO);
				}
				
				for (int iObj = 0; iObj < nObj; iObj+=parallelBatchSize ) {
					System.out.println( iObj + " of " + nObj +	 " processed.");
					int tObj = 0;
					for ( int i=0; i<parallelBatchSize && iObj+i<nObj; i++ ) {
						IFeaturesCollector fc = null;
						if (fcClassConstructor == null) {
							fc = FeaturesCollectors.readData(in);
						} else {
							fc = (IFeaturesCollector) fcClassConstructor
									.newInstance(in);
						}
						obj[i] = fc;
					}				
					
					final int fiObj = iObj;
					Parallel.forEach(arrList, new Function<Integer, Void>() {
						public Void apply(Integer i) {
							int max = i + nObjPerThread;
							if (max > kNNs.length)
								max = kNNs.length;
							
							for (int iQ = i; iQ<max; iQ++) {
								for ( int iO=0; iO<parallelBatchSize && fiObj+iO<nObj; iO++ ) {
									if ( onlyID)
										kNNs[iQ].offer(((IHasID) obj[iO]).getID(), sim.distance(q[iQ], obj[iO]));
									else 
										kNNs[iQ].offer(obj[iO], sim.distance(q[iQ], obj[iO]));
									
								}
							}

							return null;
						}
					});
				
					

				}
			}
		} else {
			// old IO
			// long indexOffSet = in.readLong();
			//
			// FeatureClassCollector featuresClasses = new
			// FeatureClassCollector(in); //FeaturesCollectors.getClass(
			// file.readInt() );
			// Class idClass = IDClasses.readClass(in);
			//
			// RandomAccessFile rndFile = new RandomAccessFile( file, "rw" );
			// rndFile.seek(indexOffSet);
			// int size = rndFile.readInt();
			// arr = new ArrayList(size);
			//
			// System.out.println("--> The archive contains " + size);
			//
			//
			// for ( int i=0; i<size; i++) {
			// arr.add( FeaturesCollectors.readData(in));
			// }
		}

		ISimilarityResults[] res = new ISimilarityResults[kNNQueue.length];
		for (int i = 0; i < kNNQueue.length; i++) {
			res[i] = kNNQueue[i].getResults();
		}

		return res;
	}

	public static final ArrayList<IFeaturesCollector> getAll(File file)
			throws IOException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(file)));
		ArrayList<IFeaturesCollector> arr = null;

		if (in.readLong() != fileID) {
			System.err
					.println("The file does not appear to be a FeatureArchive");
		}

		int fileVersion = in.readInt();

		if (fileVersion > 1) {
			Class fcClass = FeaturesCollectors.readClass(in);
			Constructor fcClassConstructor = getFCConstructor(fcClass);
			new FeatureClassCollector(in);
			IDClasses.readClass(in);

			arr = new ArrayList();
			while (in.available() != 0) {
				if (fcClassConstructor == null) {
					arr.add(FeaturesCollectors.readData(in));
				} else {
					arr.add((IFeaturesCollector) fcClassConstructor.newInstance(in));
				}

			}
		} else {
			// old IO
			long indexOffSet = in.readLong();

			FeatureClassCollector featuresClasses = new FeatureClassCollector(
					in); // FeaturesCollectors.getClass( file.readInt() );
			Class idClass = IDClasses.readClass(in);

			RandomAccessFile rndFile = new RandomAccessFile(file, "rw");
			rndFile.seek(indexOffSet);
			int size = rndFile.readInt();
			arr = new ArrayList(size);

			System.out.println("--> The archive contains " + size);

			for (int i = 0; i < size; i++) {
				arr.add(FeaturesCollectors.readData(in));
			}
		}

		return arr;
	}

	public final IID getIdAt(int i) {
		return ids.get(i);
	}
	
	static final public void readHeader(DataInputStream in) throws IOException {
		
		in.readLong();
		in.readInt();
		// Reading classes
		FeaturesCollectors.readClass(in);
		new FeatureClassCollector(in);
		IDClasses.readClass(in);
	}

	public FeaturesCollectorsArchive(File file) throws IOException,
			SecurityException, NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {

		System.out.println("Opening FeaturesCollectorsArchive: " + file.getAbsolutePath());
		offsetFile = new File(getIDFileName(file));
		idFile = new File(getOffsetFileName(file));
		this.f = file;
		rndFile = new RandomAccessFile(file, "rw");

		if (rndFile.readLong() != fileID) {
			System.err
					.println("The file does not appear to be a FeatureArchive");
		}

		int fileVersion = rndFile.readInt();

		if (fileVersion > 1) {

			// Reading classes
			fcClass = FeaturesCollectors.readClass(rndFile);
			fcClassConstructor = getFCConstructor(fcClass);
			fcClassConstructor_NIO = getFCConstructor_NIO(fcClass);
			featuresClasses = new FeatureClassCollector(rndFile);
			idClass = IDClasses.readClass(rndFile);

			if (!offsetFile.exists() || !idFile.exists()
					|| file.lastModified() > offsetFile.lastModified()
					|| file.lastModified() > idFile.lastModified()) {

				if (!offsetFile.exists())
					System.out.println("Offsets file not found.");
				else if (file.lastModified() > offsetFile.lastModified())
					System.out.println("offsetFile file out of date will be delete and rebuilt");
				if (!idFile.exists())
					System.out.println("IDs file not found.");
				else if (file.lastModified() > idFile.lastModified())
					System.out.println("IDs file file out of date will be delete and rebuilt");
				
				System.out.print("Analysing binary file... ");
				positions = new TLongArrayList();
				ids = new ArrayList();
				long offset = 0;
				// reading
				while ((offset = rndFile.getFilePointer()) < rndFile.length()) {

					positions.add(offset);

					IFeaturesCollector fc = null;
					if (fcClassConstructor == null) {
						fc = FeaturesCollectors.readData(rndFile);
					} else {
						fc = (IFeaturesCollector) fcClassConstructor.newInstance(rndFile);
					}

					ids.add(((IHasID) fc).getID());
					
					System.out.println(positions.size());
				}
				System.out.println("done");

				System.out.println("--> The archive contains " + positions.size());
				System.out.println("--> Features Collector Class: " + fcClass);
				System.out.println("--> Features to consider are: "
						+ featuresClasses);

				System.out.print("Creating IDs HashTable... ");
				// idOffsetMap = new HashMap(2*positions.size());
				// for ( int i=0; i<positions.size(); i++ ) {
				// idOffsetMap.put(ids.get(i), positions.get(i));
				// }
				idPosMap = new HashMap(2 * positions.size());
				for (int i = 0; i < positions.size(); i++) {
					idPosMap.put(ids.get(i), i);
				}
				System.out.println("done");

				createIndexFiles();
			} else {

				// READING INDEX FILE
				RandomAccessFile inOffset = new RandomAccessFile(offsetFile,
						"r");
				// RandomAccessFile inId = new RandomAccessFile( idFile, "r" );

				int size = (int) (inOffset.length() / 8);
				System.out.println("Archive " + file.getAbsolutePath()
						+ " contains " + size + " objects.");

				// Reading offsets
				long[] tempPositions = new long[size];
				byte[] byteArray = new byte[size * 8];
				LongBuffer inLongBuffer = ByteBuffer.wrap(byteArray)
						.asLongBuffer();
				inOffset.readFully(byteArray);
				inLongBuffer.get(tempPositions, 0, size);
				positions = new TLongArrayList(tempPositions);

				DataInputStream idInput = new DataInputStream(
						new BufferedInputStream(new FileInputStream(idFile)));

				// Reading ids
				if (idClass != null) {
					// idOffsetMap = new HashMap(2*size);
					idPosMap = new HashMap(2 * size);

					System.out.print("Reading IDs... ");
					ids = new ArrayList(Arrays.asList(IDClasses.readArray(
							idInput, size, idClass)));
					System.out.println("done");

					System.out.print("Creating IDs HashTable... ");
					size = positions.size();
					for (int i = 0; i < size; i++) {
						// idOffsetMap.put(ids.get(i), positions.get(i));
						idPosMap.put(ids.get(i), i);
					}
					System.out.println("done");
				} else {
					System.out.println("--> The archive does not contains IDs");
					// no IDs
					ids = null;
					// idOffsetMap = null;
					idPosMap = null;
				}

			}

		} else {

			// VERSION 1

			fcClass = null;
			fcClassConstructor = null;
			fcClassConstructor_NIO = null;
			// old IO
			long indexOffSet = rndFile.readLong();

			featuresClasses = new FeatureClassCollector(rndFile); // FeaturesCollectors.getClass(
																	// file.readInt()
																	// );
			// featureCollectorConstructor =
			// featuresCollectorClass.getConstructor(DataInput.class);

			idClass = IDClasses.readClass(rndFile);

			// file.seek(file.length()-8);
			// long indexOffSet = file.readLong();
			rndFile.seek(indexOffSet);
			int size = rndFile.readInt();
			System.out.println("--> The archive contains " + size);
			System.out.println("--> Features Collector Class: " + fcClass);
			System.out.println("--> Features to consider are: "
					+ featuresClasses);

			// Reading offsets
			long[] tempPositions = new long[size];
			byte[] byteArray = new byte[size * 8];
			LongBuffer inLongBuffer = ByteBuffer.wrap(byteArray).asLongBuffer();
			rndFile.readFully(byteArray);
			inLongBuffer.get(tempPositions, 0, size);
			positions = new TLongArrayList(tempPositions);

			// Reading ids
			if (idClass != null) {
				// idOffsetMap = new HashMap(2*size);
				idPosMap = new HashMap(2 * size);

				System.out.print("Reading IDs... ");
				ids = new ArrayList(Arrays.asList(IDClasses.readArray(rndFile, size, idClass)));
				System.out.println("done");

				System.out.print("Creating IDs HashTable... ");
				size = positions.size();
				for (int i = 0; i < size; i++) {
					// idOffsetMap.put(ids.get(i), positions.get(i));
					idPosMap.put(ids.get(i), i);
				}
				System.out.println("done");
			} else {
				System.out.println("--> The archive does not contains IDs");
				// no IDs
				ids = null;
				// idOffsetMap = null;
				idPosMap = null;
			}
		}
	}

	public static final String getIDFileName(File file) {
		return file.getAbsolutePath() + ".ids";
	}

	public static final String getOffsetFileName(File file) {
		return file.getAbsolutePath() + ".offs";
	}

	public void createIndexFiles() throws IOException {

		offsetFile.delete();
		idFile.delete();

		DataOutputStream outOffset = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(offsetFile)));
		DataOutputStream outIDs = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(idFile)));

		for (int i = 0; i < positions.size(); i++) {
			outOffset.writeLong(positions.get(i));
			ids.get(i).writeData(outIDs);
		}

		outOffset.close();
		outIDs.close();

	}

	public static final FeaturesCollectorsArchive create(File inFile,
			File outFile, FeatureClassCollector featuresClasses)
			throws Exception {
		return create(inFile, outFile, featuresClasses, null, null);
	}

	public static final FeaturesCollectorsArchive create(File inFile,
			File outFile, FeatureClassCollector featuresClasses, Class idClass)
			throws Exception {
		return create(inFile, outFile, featuresClasses, idClass, null);
	}

	public static final FeaturesCollectorsArchive create(File inFile,
			File outFile, FeatureClassCollector featuresClasses, Class idClass,
			HashSet<IID> ids) throws Exception {
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(inFile)));
		FeaturesCollectorsArchiveBuilder builder = new FeaturesCollectorsArchiveBuilder(
				outFile, featuresClasses, idClass);

		while (in.available() != 0) {
			IFeaturesCollector fc = FeaturesCollectors.readData(in);

			if (ids == null || ids.contains(((IHasID) fc).getID()))
				builder.add(fc);

			if (builder.size() % 1000 == 0)
				System.out.println(builder.size());
		}

		builder.close();

		return new FeaturesCollectorsArchive(outFile);
	}

	public synchronized IFeaturesCollector get(int i) throws ArchiveException {
		try {
			// System.out.println("i: " + i + ", offset: " + positions.get(i));
			rndFile.seek(positions.get(i));

			byte[] arr = null;
			if (i < positions.size() - 1)
				arr = new byte[(int) (positions.get(i + 1) - positions.get(i))];
			else
				arr = new byte[(int) (rndFile.length() - positions.get(i))];
			ByteBuffer buf = ByteBuffer.wrap(arr);
			rndFile.read(arr);

			if (fcClassConstructor_NIO == null) {
				return FeaturesCollectors.readData(buf);

				// return FeaturesCollectors.readData(rndFile);

			} else {

				return (IFeaturesCollector) fcClassConstructor_NIO.newInstance(buf);

				// return (IFeaturesCollector)
				// fcClassConstructor.newInstance(rndFile);
			}
		} catch (Exception e) {
			throw new ArchiveException(e);
		}
		// return featureConstructor.newInstance(file);

	}

	public final Long getOffset(int i) {
		return positions.get(i);
	}

	public final Long getOffset(IID id) {
		// return idOffsetMap.get(id);
		return getOffset(idPosMap.get(id));
	}

	public synchronized final boolean contains(IID id) throws ArchiveException {
		// return idOffsetMap.get(id) != null;
		return idPosMap.get(id) != null;
	}

	public synchronized final IFeaturesCollector get(IID id)
			throws ArchiveException {
		Integer i = idPosMap.get(id);
		if (i == null)
			return null;
		return get(i);
		// if ( idClass != null ) {
		// Long tempOffset = idOffsetMap.get(id);
		// if ( tempOffset == null ) {
		// return null;
		// }
		// try {
		// rndFile.seek(tempOffset);
		// if ( fcClassConstructor == null ) {
		// return FeaturesCollectors.readData(rndFile);
		// } else {
		// return (IFeaturesCollector) fcClassConstructor.newInstance(rndFile);
		// }
		// } catch (Exception e) {
		// throw new ArchiveException(e);
		// }
		//
		// } else {
		// throw new
		// ArchiveException("The archive does not contain any ID information");
		// }
	}

	public void close() throws IOException {
		if (changed) {
			createIndexFiles();
		}
		rndFile.close();
	}

	@Override
	public Iterator<IFeaturesCollector> iterator() {
		try {
			return new FeaturesCollectorsArchiveIterator(f, fcClassConstructor );
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
