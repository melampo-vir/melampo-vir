package it.cnr.isti.vir.similarity.knn;

import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.id.IID;
import it.cnr.isti.vir.similarity.ISimilarityResults;
import it.cnr.isti.vir.similarity.metric.Metric;
import it.cnr.isti.vir.similarity.pqueues.SimPQueueDMax;
import it.cnr.isti.vir.similarity.pqueues.SimilarityPQueue;
import it.cnr.isti.vir.util.Pivots;
import it.cnr.isti.vir.util.Reordering;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class MultipleKNNPQueueID<F>  {
	
	private KNNPQueue<F>[] knn;
	private IFeature[] qObj;
	private double[][] intDist;
	private final Metric<F> comp;
	private final Integer k;
	private final boolean storeID;
	private final boolean distET;
		
	// not usable for multithread
	final double[] tempDs;
	final int[] tempI;
	
	private boolean silent = false;
	
	public ISimilarityResults getResults(int i) {
		return knn[i].getResults();
	}
	

	public ISimilarityResults[] getResults() {
		ISimilarityResults[] res = new ISimilarityResults[knn.length];
		for (int i=0; i<res.length; i++) {
			res[i] = knn[i].getResults();
		}
		return res;
	}
	
//	public MultipleKNNPQueueID(MultipleKNNPQueueID<F> given, HashSet<ID> exclusionHashSet) {
//		this.qObj = given.qObj;
//		this.comp = given.comp;
//		this.k = given.k;
//		this.storeID = given.storeID;
//		this.distET = given.distET;
//		
//		this.tempDs = null;
//		this.tempI = null;
//		
//		this.knn = new KNNPQueue[given.knn.length];
//		for ( int i=0; i<knn.length; i++ ) {
//			this.knn[i] = new KNNPQueue(given.knn[i], exclusionHashSet);
//		}
//	}
	
	public final void setSilent(boolean silent) {
		this.silent = silent;
	}

	//private final RecentsAsPivots recentsFilter;
	@SuppressWarnings("unchecked")
	
	public MultipleKNNPQueueID(	Collection queryColl,
			Integer k,
								Metric comp,
								boolean useInterDistances,
								IQueriesOrdering ordering,
								Integer nRecents
								) {
		this(queryColl, k, comp, useInterDistances, ordering, nRecents, false, true, SimPQueueDMax.class, false );
	
	}
	
	
	public MultipleKNNPQueueID(	Collection queryColl,
								Integer k,
								Metric comp,
								Class pQueueClass
								) {
		
		this (			queryColl,
						k,
						comp,
						false,
						null,
						null,
						true,
						false,
						pQueueClass,
						true );
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MultipleKNNPQueueID(	Collection queryColl,
								Integer k,
								Metric comp,
								boolean useInterDistances,
								IQueriesOrdering ordering,
								Integer nRecents,
								boolean distET,
								boolean storeID,
								Class pQueueClass,
								boolean silent) {
		
		this.storeID = storeID;
		int size = queryColl.size();
		qObj = new IFeature[size];
		this.comp = comp;
		this.k = k;
		this.silent = silent;
		
		this.distET = distET;

		//temporary
		tempDs = new double[qObj.length];
		tempI = new int[qObj.length];
		
		int count=0;
		for (Iterator<F> it = queryColl.iterator(); it.hasNext();) {
			qObj[count++] = (IFeature) it.next();
		}
		
		if ( useInterDistances == true ) {
			intDist = evalInterDistances();
		} else {
			intDist = null;
		}
		
		
		//init reordering
		if ( intDist!=null && ordering != null ) {
			long startTime = System.currentTimeMillis();;
			if ( !silent ) {
				System.out.print(ordering.getClass());
				System.out.println("Avg inter-dist before ordering: " + Pivots.getTrMatrixAvg(intDist));
				System.out.println("Avg inter-dist before ordering (topK): " + Pivots.getTrMatrixAvg(intDist, k));
				startTime = System.currentTimeMillis();
			}
			//TODO: reorder top N only? 
			reorder( ordering.getOrder(intDist));
			if ( !silent ) {
				System.out.println("Avg inter-dist after ordering: " + Pivots.getTrMatrixAvg(intDist));	
				System.out.println("Avg inter-dist before ordering (topK): " + Pivots.getTrMatrixAvg(intDist, k));
				System.out.print(" done in " + (System.currentTimeMillis()-startTime) + " milliSec\n");
			}
		}
		
		//			
				
		knn = new KNNPQueue[qObj.length];
		for ( int i=0; i<knn.length; i++ ) {
			@SuppressWarnings("rawtypes")
			SimilarityPQueue queue = null;
			try {
				// for SimPQueue_kNN
				queue = (SimilarityPQueue) pQueueClass.getConstructor(int.class).newInstance((int) k);
			} catch (Exception e1) {
				try {	
					// basically for SimPQueueLowe and SimPQueueLowe2NN
					queue = (SimilarityPQueue) pQueueClass.getConstructor().newInstance();
				} catch (Exception e2) {
					e1.printStackTrace();
					e2.printStackTrace();
				}
			}
			knn[i] = new KNNPQueue<F>(queue, comp, (F) qObj[i], storeID);
		}
		
//		if ( nRecents != null && nRecents > 0 ) {
//			recentsFilter = new RecentsAsPivots(knn, nRecents, comp);
//		} else {
//			recentsFilter = null;
//		}
		
	}
	
	public int size() {
		return knn.length;
	}

//	public final void offer(Collection<F> coll ) {
//		((SURFMetric) comp).offer((Collection<SURF>) coll, (KNNPQueue<SURF>[]) knn);
//	}
	
	public final void offer(F obj) {
		offer(obj, 0, knn.length);
	}
	
	public final void offer(F obj, int min, int max) {
		
		
		if ( intDist != null ) {
			int distCount = 0;
			for ( int i=min; i<max; i++ ) {
				
				if ( intDist != null && intDistFiltered(i, tempDs, tempI, distCount) ) continue;
				
				// using max
				double dist = -1;
				if ( distET ) dist = comp.distance( knn[i].query, obj, knn[i].excDistance );
				else dist = comp.distance( knn[i].query, obj);
				
				if ( dist >= 0 ) {
					tempDs[distCount] = dist;			
					tempI[distCount] = i;
				
					if ( dist < knn[i].excDistance ) knn[i].offer(obj, dist);
					distCount++;
				}
					
			}
		} else if ( distET ) {
			for ( int i=min; i<max; i++ ) {
				// using max
				double dist = comp.distance( knn[i].query, obj, knn[i].excDistance );
				if ( dist >= 0 && dist < knn[i].excDistance ) knn[i].offer(obj, dist);
			}
		} else {
			for ( int i=min; i<max; i++ ) {
				double dist = comp.distance( knn[i].query, obj);
				if ( dist < knn[i].excDistance ) knn[i].offer(obj, dist);
			}
		}
		
//		if ( recentsFilter != null ) {
//			recentsFilter.add(obj, tempDs);
//			//recentsFilter.add(obj, tempIDDs);
//		}
	}
	
	protected final double[][] evalInterDistances() {
		double temp[][] = new double[qObj.length][];
		for ( int i=0; i<temp.length; i++ ) {
			temp[i] = new double[i];
		}
		for ( int i=0; i<temp.length; i++ ) {
			for ( int j=0; j<i; j++ ) {
				temp[i][j] = comp.distance( (F) qObj[i], (F) qObj[j]);
			}
		}
		return temp;
	}
	
	
	public final void reorder(Collection<Integer> ordList) {

		Reordering.reorder(ordList, qObj);
		Reordering.reorder(ordList, knn);
		intDist = Reordering.reorderTrMatrix(ordList, intDist);
		//System.out.println("intDist");
		
	}
	
	public final double getAvgLastDist() {
		double sum = 0;
		for ( int i=0; i<knn.length; i++ ) {
			sum += knn[i].getLastDist();
		}
		return sum /(double) knn.length;
	}
	
	//TODO: remove duplication to Pivots.getAvg..
	public final double getAvgIntDist() {
		double avg = 0;
		// i=0 is not useful
		
		if ( intDist == null ) return -1;
		int count = 0;
		for ( int i=1; i<intDist.length; i++ ) {
			double temp = 0;
			for ( int j=0; j<intDist[i].length; j++ ) {
				temp = intDist[i][j];
				avg += temp;
			}
			count += intDist[i].length;
		}
		return avg / (double) count;
	}
	
	//TODO: remove duplication to Pivots.getAvg..
		public final double getAvgIntDist(int topK) {
			return Pivots.getTrMatrixAvg(intDist, topK);
		}
	
	
	private final boolean intDistFiltered(int id, double[] tempDs, int[] tempIDs, int set) {
		
		for ( int j=0; j<set; j++) if ( Math.abs( intDist[id][tempIDs[j]] - tempDs[j] ) > knn[id].excDistance) return true;
//			double diff =  intDist[id][tempIDs[j]] - tempDs[j];
//			if ( diff > knn[id].excDistance) return true;
//			if ( -diff > knn[id].excDistance) return true;

		return false;
	}
	
	
	public final KNNPQueue<F> getKNN( Object givenObj ) {
		
		for ( int i=0; i<knn.length; i++ ) {
			if ( knn[i].query == givenObj ) return knn[i];
		}		
		
		return null;
	}

	public void writeResultsIDs(DataOutputStream out) throws IOException {
		for ( int i=0; i<knn.length; i++ ) {
			knn[i].getResults().writeIDData(out);
		}		
	}
	
	public final KNNPQueue<F> getKNN( int i ) {
		return knn[i];
	}
	
	public KNNPQueue<F> get(int index) {
		return knn[index];
	}



	
}
