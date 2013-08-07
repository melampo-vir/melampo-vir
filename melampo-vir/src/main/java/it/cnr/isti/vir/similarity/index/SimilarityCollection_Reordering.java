package it.cnr.isti.vir.similarity.index;

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.ILabeled;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.IFeaturesCollector_Labeled_HasID;
import it.cnr.isti.vir.file.ArchiveException;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.id.IID;
import it.cnr.isti.vir.similarity.ISimilarityResults;
import it.cnr.isti.vir.similarity.ObjectWithDistance;
import it.cnr.isti.vir.similarity.Similarity;
import it.cnr.isti.vir.similarity.knn.KNNPQueue;
import it.cnr.isti.vir.similarity.pqueues.SimPQueueArr;
import it.cnr.isti.vir.similarity.pqueues.SimPQueueDMax;
import it.cnr.isti.vir.util.MutableInt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class SimilarityCollection_Reordering extends SimilarityCollection {

	FeaturesCollectorsArchive archive;
	Similarity reOrdSim;
	int internalK = 50;
	int heterogeneity = 10;
	public SimilarityCollection_Reordering(
			Similarity sim,
			Similarity reOrdSim,
			int internalK,
			int heterogeneity,
			FeaturesCollectorsArchive reOrdData,
			Collection objCollection) {
		
		super(sim);
		this.archive = archive;
		this.reOrdSim = reOrdSim;
		this.archive = reOrdData;
		this.internalK = internalK;
		this.heterogeneity = heterogeneity;
		coll = objCollection;		
	}
	
	public SimilarityCollection_Reordering(
			Similarity sim,
			Similarity reOrdSim,
			int internalK,
			int heterogeneity,
			FeaturesCollectorsArchive reOrdData) {
		this(sim, reOrdSim, internalK, heterogeneity, reOrdData, null);
	}

	@Override
	public synchronized ISimilarityResults getKNNResults(IFeaturesCollector qObj, int k) {
		//KNNObjects knn = new KNNObjects(qObj, k, sim);
		KNNPQueue knn = 	new KNNPQueue(	new SimPQueueDMax(internalK),sim, qObj );
		knn.offerAll(coll);
		ISimilarityResults res = knn.getResults();
		
		HashMap<AbstractLabel, MutableInt> labelHMap = new HashMap(100);

		IID qObjID = null;
		if ( IHasID.class.isInstance(qObj) ) 
			qObjID = ((IHasID) qObj).getID();
		
		AbstractLabel qLabel = ((ILabeled) qObj).getLabel() ;
		int corrLabel = -1;
		ArrayList<IFeaturesCollector_Labeled_HasID> arrList = new ArrayList( internalK );
		for ( 	Iterator<ObjectWithDistance> it = res.iterator();
				it.hasNext() && arrList.size() < internalK; ) {
			try {
				IFeaturesCollector_Labeled_HasID curr = (IFeaturesCollector_Labeled_HasID) it.next().getObj();
				AbstractLabel currLabel = curr.getLabel();
				IID currID = curr.getID();
				
				if ( qObjID != null && qObjID.equals(currID)) continue;
				
				if (  heterogeneity > 0 ) {
					MutableInt labelCount = labelHMap.get(currLabel);
					if ( labelCount == null ) {
						labelHMap.put(currLabel, new MutableInt(1));						
					} else if ( labelCount.get() < heterogeneity) {
						labelCount.inc();
					} else {
						continue;
					}
				}
				
				if ( archive != null ) {
					IFeaturesCollector_Labeled_HasID obj = (IFeaturesCollector_Labeled_HasID) archive.get( curr.getID());
					obj.setLabel(currLabel);
					arrList.add( obj );
				
				} else {
					arrList.add( curr );
				}
				
				if ( corrLabel < 0 && currLabel.equals(qLabel)) {
					corrLabel = arrList.size();
				}
				
			} catch (ArchiveException e) {
				e.printStackTrace();
			} 
		}
		
		
		/// TO DO !!!!
		IFeaturesCollector_Labeled_HasID qObjComplete = (IFeaturesCollector_Labeled_HasID) qObj;
		if ( archive != null && qObjID != null ) {
			try {
				qObjComplete = (IFeaturesCollector_Labeled_HasID) archive.get( ((IHasID) qObj).getID());
			} catch (ArchiveException e) {
				e.printStackTrace();
			}
		}
		
//		if ( corrLabel == -1)
//			System.out.println("BOH!");
		
		SimPQueueArr pQueue = new SimPQueueArr(k);
		for ( int i=0; i<arrList.size(); i++ ) {
			IFeaturesCollector_Labeled_HasID curr = arrList.get(i);
			double distance = reOrdSim.distance(qObjComplete, curr, pQueue.excDistance);
			if ( distance >=0 && distance < pQueue.excDistance )
				pQueue.offer(curr, distance);
		}
		
//		System.out.println(	"---> " + 
//							"\t" + pQueue.size() +
//							"\t" + corrLabel + "/" + arrList.size() +
//							"\t" + res.size() +
//							"\thet=" + heterogeneity );
		return pQueue.getResults();
		
	}
}
