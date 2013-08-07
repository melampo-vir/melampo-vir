package it.cnr.isti.vir.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map.Entry;


public class EntryValueReverseComparator implements Comparator {

	public final int compare(Object o1, Object o2) {
		return -((Comparable) ((Entry) o1).getValue()).compareTo((Comparable) ((Entry) o2).getValue());
	}
	
}
