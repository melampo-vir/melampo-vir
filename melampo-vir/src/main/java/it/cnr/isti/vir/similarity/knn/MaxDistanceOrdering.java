package it.cnr.isti.vir.similarity.knn;

import it.cnr.isti.vir.util.Pivots;
import it.cnr.isti.vir.util.RandomOperations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class MaxDistanceOrdering implements IQueriesOrdering{

	int topK;
	
	public MaxDistanceOrdering(int topK) {
		this.topK = topK;
	}
	
	public final ArrayList<Integer> getOrder(double[][] intDist) {
		
		//System.out.println(this.getClass() + " ordering (triesMax="+triesMax+" nObjects4Tries=" + nObjects +")");
		System.out.println("Avg inter-dist before ordering: " + Pivots.getTrMatrixAvg(intDist));
		System.out.println("Avg inter-dist before ordering(topK): " + Pivots.getTrMatrixAvg(intDist, topK));
		long startTime = System.currentTimeMillis();
		
		double[] distanceSum = new double[intDist.length];
		
		for (int i = 0; i < distanceSum.length; i++) {
			distanceSum[i] = computeSum(intDist[i]);
		}


			
//			System.out.println(orderedList.size() + ":\t" +best);
//			//System.out.print(".");
//			System.out.println(Arrays.toString(orderedList.toArray()));
//		}
//		
//		return orderedList;
		return null;
		
	}
	
private double computeSum(double[] ds) {
	double sum = 0;
	for (int i = 0; i < ds.length; i++) {
		sum += ds[i];
	}
	
	return sum;
}
}
