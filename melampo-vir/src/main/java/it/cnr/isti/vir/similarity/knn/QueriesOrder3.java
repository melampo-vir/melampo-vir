package it.cnr.isti.vir.similarity.knn;

import it.cnr.isti.vir.util.Pivots;
import it.cnr.isti.vir.util.RandomOperations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class QueriesOrder3 implements IQueriesOrdering{

	private final Integer triesMax;
	private final Integer nObjects ;
	
	public QueriesOrder3(int triesMax, int nObjects) {
		this.triesMax = triesMax;
		this.nObjects = nObjects;
	}
	
	public QueriesOrder3() {
		this.triesMax = null;
		this.nObjects = null;		
	}

	
	public final ArrayList<Integer> getOrder(double[][] intDist) {
		
		System.out.println(this.getClass() + " ordering (triesMax="+triesMax+" nObjects4Tries=" + nObjects +")");
		System.out.println("Avg inter-dist before ordering: " + Pivots.getTrMatrixAvg(intDist));
		System.out.println("Avg inter-dist before ordering(topK): " + Pivots.getTrMatrixAvg(intDist, nObjects));
		long startTime = System.currentTimeMillis();

		
		int size=intDist.length;
		
		boolean[] ordered = new boolean[size];
		java.util.Arrays.fill(ordered,false);
//		if ( nObjects != null ) {
//			System.out.print("Random reordering");			
//			LinkedList<Integer> ordList = RandomOperations.getRandomOrderedIntegers(size);
//			System.out.print(" ... done.\n");
//			return ordList;
//		}
		
		// initialize list of indexes with a random ordered list of objects
		LinkedList<Integer> list = RandomOperations.getRandomOrderedLinkedListIntegers(size);
		
		ArrayList<Integer> orderedList = new ArrayList<Integer>(size);
		
		double[][] last = new double[size][];
		for ( int i=0; i<size; i++ ) {
			last[i] = new double[i];
			// initializing (probably not necessary)
			for ( int j=0; j<i; j++) {
				last[i][j] = 0;
			}
		}
		
		// at each step in finding new order
		while ( orderedList.size() < size ) {
			Integer best = null;
			double bestSum = -1;
			
			if ( triesMax != null && list.size() > triesMax ) RandomOperations.reorderLinkedList(list);
			
			// for each not ordered image (not more then triesMax)
			int triesCount = 0;
			for (	Iterator<Integer> it = list.iterator();
					it.hasNext() && ( triesMax == null || triesCount < triesMax);
					triesCount ++) {
				int curr = it.next();
				double currSum = 0;
				
				// consider only first nObjects
				for ( int i=0; i<size && ((nObjects==null) || i<nObjects); i++ ) {
					if ( ordered[i] ) continue;
					for ( int j=0; j<i; j++) {
						if ( ordered[j] ) continue;
						
						double d1 = 0;
						if ( i > curr ) d1 = intDist[i][curr];
						else if ( i < curr ) d1 = intDist[curr][i];
						//else d1 = 0;
						
						double d2 = 0;
						if ( j > curr ) d2 = intDist[j][curr];
						else if ( j < curr ) d2 = intDist[curr][j];
						//else d2 = 0;
						
						double abs = Math.abs( d1 - d2 );
						double temp = last[i][j];
						if ( abs > temp ) currSum += abs-temp;
						//currSum += Math.max(last[i][j], Math.abs( d1 - d2));
					}
				}			
				
				if ( currSum > bestSum ) {
					best = curr;
					bestSum = currSum;
				}
				
				System.out.println("QueriesOrder3 Try: " + triesCount + " Best Sum: " + bestSum);
			} // next best found
			
			ordered[best]=true;
			orderedList.add( best ); //add to orderedList
			list.remove(best); // removes from to do list
			
			// last updating using new 
			for ( int i=0; i<last.length; i++ ) {
				for ( int j=0; j<i; j++) {
					double d1 = 0;
					if ( i > best ) d1 = intDist[i][best];
					else if ( i < best ) d1 = intDist[best][i];
					//else d1 = 0;
					
					double d2 = 0;
					if ( j > best ) d2 = intDist[j][best];
					else if ( j < best ) d2 = intDist[best][j];
					//else d2 = 0;
					
					last[i][j] = Math.max(last[i][j], Math.abs( d1 - d2 ));
				}
			}	
			
			
			System.out.println(orderedList.size() + ":\t" +best);
			//System.out.print(".");
			System.out.println(Arrays.toString(orderedList.toArray()));
		}
		
		return orderedList;
		
	}
	
}
