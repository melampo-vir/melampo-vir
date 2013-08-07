package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.similarity.knn.KNNSearchException;


public interface KNNExecuter {
	
	public ISimilarityResults getKNNResults(IFeaturesCollector qObj, int k) throws KNNSearchException;
	
	//public SimilarityResultsInterface<ID> getKNNResultsIDs(FeaturesCollectionInterface qObj, int k);
	
	//public KNNIDs<ID> getKNNIDs(FeaturesCollectionWithID qObj, int k);
	
}
