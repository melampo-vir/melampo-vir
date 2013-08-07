package it.cnr.isti.vir.util;

public class MutableInt implements Comparable {
	int value = 0;
	
	public MutableInt(int init) {
		value = init;
	}
	  
	public final void inc(int inc) { value+=inc; };
	public final void inc () { ++value; }
	public final int get () { return value; }
	public final void set(int v) { value=v; };
	@Override
	public final int compareTo(Object arg0) {
		return value-((MutableInt)arg0).value;
	}
	
	public boolean equals(Object obj) {
		if ( compareTo(obj) == 0 ) return true;
		return false;
	}
	  
	  
}
