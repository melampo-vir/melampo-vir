package it.cnr.isti.vir.similarity.index;

import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.similarity.ISimilarityResults;
import it.cnr.isti.vir.similarity.KNNExecuter;
import it.cnr.isti.vir.similarity.Similarity;
import it.cnr.isti.vir.similarity.knn.KNNPQueue;
import it.cnr.isti.vir.similarity.pqueues.SimPQueueDMax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class SimilarityCollection implements KNNExecuter {

	protected Similarity sim;
	protected Collection coll = null;
	
	public SimilarityCollection(Similarity sim) {
		this.sim = sim;
		coll = new LinkedList();
	}
	
	
	public SimilarityCollection(Similarity sim, Collection objCollection) {
		this.sim = sim;
		coll = objCollection;	
	}
	
	public void add(IFeaturesCollector obj) {
		if ( coll == null ) coll = new ArrayList();
		coll.add(obj);
	}

	@Override
	public synchronized ISimilarityResults getKNNResults(IFeaturesCollector qObj, int k) {
		//KNNObjects knn = new KNNObjects(qObj, k, sim);
		KNNPQueue knn = 	new KNNPQueue(	new SimPQueueDMax(k),sim, qObj );
		knn.offerAll(coll);
		return knn.getResults();
	}
	
	
	public String toString() {
		return this.getClass() + "\n   similarity: " + sim;
	}
}
