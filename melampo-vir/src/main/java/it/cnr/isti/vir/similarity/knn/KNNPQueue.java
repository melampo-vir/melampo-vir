package it.cnr.isti.vir.similarity.knn;

import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.id.IID;
import it.cnr.isti.vir.similarity.Similarity;
import it.cnr.isti.vir.similarity.ISimilarityResults;
import it.cnr.isti.vir.similarity.pqueues.SimilarityPQueue;
import it.cnr.isti.vir.util.ParallelOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import com.davidsoergel.conja.Function;
import com.davidsoergel.conja.Parallel;

public class KNNPQueue<F> {

	protected final SimilarityPQueue pQueue;
	protected final Similarity sim;
	public final F query;
//	public final F queryFeature;
	protected final boolean storeID;
	public double excDistance = Double.MAX_VALUE;
	
//	public KNNPQueue(KNNPQueue<F> given, HashSet<ID> excHashSet) {
//		this.sim = given.sim;
//		this.query = given.query;
//		this.storeID = given.storeID;
//		this.excDistance = given.excDistance;
//		this.pQueue = given.pQueue.getExcluding(excHashSet);
//	}
	
	public KNNPQueue(SimilarityPQueue pQueue, Similarity sim, F query) {
		this(pQueue,sim,query,false);
	}
	
	public KNNPQueue(SimilarityPQueue pQueue, Similarity sim, F query, boolean storeID) {
		this.pQueue = pQueue;
		this.sim = sim;
		this.query = query;
		this.storeID = storeID;
//		if ( sim.getRequestedFeaturesClasses().size() == 1 ) {
//			queryFeature = ((IFeature)  query).getFeature(sim.getRequestedFeaturesClasses().iterator().next());
//		} else {
//			queryFeature = (F) query;
//		}
	}
	
//	public final boolean isFull() {
//		return pQueue.isFull();
//	}
	
	public final boolean offer(F obj) {
		double tDistance = sim.distance((IFeaturesCollector) query, (IFeaturesCollector)  obj, pQueue.excDistance);
		if ( tDistance >=0 )	{
			offer(obj, tDistance);
		}
		return false;
	}
	
	//da aggiungere synchronized
	public final synchronized void offer(F obj, double distance) {
		if ( distance < pQueue.excDistance  ) {
			if ( storeID ) pQueue.offer(	((IHasID) obj).getID(),distance);
			else pQueue.offer(obj, distance);
			excDistance = pQueue.excDistance;
		}
		
	}
	
	public final void offerAll(Collection<F> coll) {
		if ( ArrayList.class.isInstance(coll)) {
			offerAll( (ArrayList) coll);
			return;
		}
		
		final ArrayList<F> arrColl = new ArrayList(coll.size());
		for (Iterator<F> it = coll.iterator(); it.hasNext(); ) {
			arrColl.add(it.next());
		}
		
		offerAll(arrColl);
	}
	
	public final void offerAll_seq(ArrayList<F> coll) {

		for (int iO = 0; iO<coll.size(); iO++) {
			offer(coll.get(iO));
		}

	}
	
	public final void offerAll(ArrayList<F> coll) {
		final ArrayList<F> arrColl = coll;
		
		// For parallel
		final int size = coll.size();
		final int nObjPerThread = (int) Math.ceil( size / ParallelOptions.nThreads);
		ArrayList<Integer> arrList = new ArrayList(size);
		for (int iO = 0; iO<size; iO+=nObjPerThread) {
			arrList.add(iO);
		}

		Parallel.forEach(arrList, new Function<Integer, Void>() {
			public Void apply(Integer i) {
					int max = i+nObjPerThread;
					if ( max > size )
						max = size;
					for (int iO = i; iO<max; iO++) {
						offer(arrColl.get(iO));
					}
					return null;
				}
		});
	}
	
	public final F getFirstObject() {
		return (F) pQueue.getFirstObject();
	}
	
	public final ISimilarityResults getResults() {
		ISimilarityResults res = pQueue.getResults();
		res.setQuery(query);
		return res;
	}
	
	public final ISimilarityResults getResultsIDs() {
		return getResults().getResultsIDs();
	}
	
	public String toString() {
		return getResults().toString();
	}
	
	public boolean equals(Object obj) {
		if ( this == obj) return true;
		KNNPQueue that = (KNNPQueue) obj;
		if ( this.query != that.query ) return false;
		if ( ! this.getResults().equalResults(that.getResults()) ) return false;
		return true;
	}
	
//	public double getExcDistance() {
//		return pQueue.excDistance;
//	}
	
//	public final Object getQuery() {
//		return query;
//	}
	
	public double getLastDist() {
		return pQueue.getLastDist();
	}
	
	public int hashCode() {
		  assert false : "hashCode not designed";
		  return 42; // any arbitrary constant will do 
		  }

}
