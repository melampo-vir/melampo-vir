package it.cnr.isti.vir.similarity.knn;

import it.cnr.isti.vir.util.RandomOperations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

public class QueriesOrder2 implements IQueriesOrdering{

	private final Integer nObjects ;
	
	public QueriesOrder2(int nObjects) {
		this.nObjects = nObjects;
	}
	
	public QueriesOrder2() {
		this.nObjects = null;		
	}

	protected static class Candidate implements Comparable{
		int index;
		double value;

		public Candidate(int index, double value) {
			this.index = index;
			this.value = value;
		}
		
		public int compareTo(Object o) {
			return Double.compare(this.value, ((Candidate)o).value);
		}
		
		public boolean equals(Object obj) {
			if ( compareTo(obj) == 0 ) return true;
			return false;
		}
		
		 
	}
	
	public String toString() {
		return this.getClass() + " ( nObjects4Tries=" + nObjects +")";
	}
	
	public final ArrayList<Integer> getOrder(double[][] intDist) {
	
		int size=intDist.length;
		
//		if ( nObjects != null ) {
//			System.out.print("Random reordering");			
//			LinkedList<Integer> ordList = RandomOperations.getRandomOrderedIntegers(size);
//			System.out.print(" ... done.\n");
//			return ordList;
//		}
		
		// initialize list of indexes with a random ordered list of objects
		LinkedList<Integer> list = RandomOperations.getRandomOrderedLinkedListIntegers(size);
				
		
		TreeSet<Candidate> orderSet = new TreeSet();

		//Reordering candidates
		RandomOperations.reorderLinkedList(list);
		
		// for each not ordered image (not more then triesMax)
		int triesCount = 0;
		for (	Iterator<Integer> it = list.iterator();
				//it.hasNext() && ( triesMax == null || triesCount < triesMax);
				it.hasNext();
				triesCount ++) {
			int curr = it.next();
			double currSum = 0;
			
			// consider only first nObjects
			for ( int i=0; i<size && ((nObjects==null) || i<nObjects); i++ ) {
				for ( int j=0; j<i; j++) {
					double d1 = 0;
					if ( i > curr ) d1 = intDist[i][curr];
					else if ( i < curr ) d1 = intDist[curr][i];
					//else d1 = 0;
					
					double d2 = 0;
					if ( j > curr ) d2 = intDist[j][curr];
					else if ( j < curr ) d2 = intDist[curr][j];
					//else d2 = 0;
					
					double abs = Math.abs( d1 - d2 );
					//double temp = last[i][j];
					//if ( abs > temp ) currSum += abs-temp;
					
					currSum += abs;
										
				}
			}			
			
			orderSet.add( new Candidate(curr, currSum) );
		
		}
		
		//System.out.println(orderedList.size() + ":\t" +best);
		//System.out.print(".");
		
		
		ArrayList<Integer> ordered = new ArrayList<Integer>(size);
		for (Iterator<Candidate> it=orderSet.descendingIterator(); it.hasNext(); )
			ordered.add(it.next().index);
		
		return ordered;
	}
	
}
