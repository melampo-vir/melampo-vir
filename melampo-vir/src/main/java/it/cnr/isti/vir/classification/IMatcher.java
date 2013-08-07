package it.cnr.isti.vir.classification;

import it.cnr.isti.vir.similarity.pqueues.SimilarityPQueue;

public interface IMatcher {
	
	public boolean match(SimilarityPQueue pQ);
	
}
