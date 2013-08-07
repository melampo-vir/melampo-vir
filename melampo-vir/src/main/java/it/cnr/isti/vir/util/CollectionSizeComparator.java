package it.cnr.isti.vir.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map.Entry;


public class CollectionSizeComparator implements Comparator {

	public final int compare(Object o1, Object o2) {
		return ((Collection) o2).size() - ((Collection) o1).size();
	}
	
}
