package it.cnr.isti.vir.similarity.pqueues;

import it.cnr.isti.vir.similarity.ObjectWithDistance;
import it.cnr.isti.vir.similarity.SimilarityResults;
import it.cnr.isti.vir.similarity.ISimilarityResults;

import java.util.Arrays;

public class SimPQueueArr<O> extends SimilarityPQueue<O> {

	int objCount = 0;
	final ObjectWithDistance<O> arr[];
	private final int size;
	
	public SimPQueueArr(int k) {
		size = k;
		arr = new ObjectWithDistance[size];
	}
	
	public void reset() {
		for (int i=0; i<objCount; i++) {
			arr[i]= null;
		}
		objCount = 0;
	}
	
//	@Override
//	public double getExcDistance() {
//		if ( objCount<arr.length) return -Double.MAX_VALUE;
//		return arr[arr.length-1].dist;
//	}

	@Override
	public Integer getK() {
		return size;
	}

	@Override
	public Double getRange() {
		return Double.MAX_VALUE;
	}

	@Override
	public ISimilarityResults getResults() {
		Arrays.sort(arr);
		return new SimilarityResults(arr);
	}

	@Override
	public void offer(O obj, double distance) {
		
		if ( objCount < arr.length) {
			arr[objCount++] = new ObjectWithDistance(obj,distance);
			if ( objCount == size ) {
				Arrays.sort(arr);
				excDistance = arr[size-1].getDist();
			}
		} else if ( distance<arr[size-1].getDist() ) {
			arr[arr.length-1].reset(obj,distance);
			Arrays.sort(arr);
			excDistance = arr[size-1].getDist();
		}
		
	}

	@Override
	public int size() {
		return objCount;
	}


	@Override
	public boolean isFull() {
		return objCount >= arr.length;
	}


	@Override
	public Double getLastDist() {
		return arr[objCount-1].dist;
	}


	@Override
	public ISimilarityResults getResultsAndEmpty() {
		// TODO Auto-generated method stub
		return getResults();
	}


	@Override
	public O getFirstObject() {
		return arr[0].obj;
	}

	
}
