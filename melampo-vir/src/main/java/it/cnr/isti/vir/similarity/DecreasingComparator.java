package it.cnr.isti.vir.similarity;

import java.util.Comparator;

public class DecreasingComparator<E> implements Comparator<ObjectWithDistance<E> >{

	@Override
	public final int compare(ObjectWithDistance<E> arg0, ObjectWithDistance<E> arg1) {
		return -arg0.compareTo(arg1);
	}

}
