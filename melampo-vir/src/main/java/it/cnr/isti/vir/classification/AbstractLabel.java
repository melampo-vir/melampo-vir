package it.cnr.isti.vir.classification;

import java.io.DataOutput;
import java.io.IOException;

public abstract class AbstractLabel implements Comparable {
	
	
	public int hashCode() {
		return getLabel().hashCode();
	}
	
	public abstract void writeData(DataOutput out) throws IOException;
	
	@SuppressWarnings("unchecked")
	protected abstract Comparable getLabel();
	
	public String toString() {
		return getLabel().toString();
	}
	
	public boolean equals(Object that) {
		if ( this == that ) return true;
		if ( that == null ) return false;
		if ( getClass() != that.getClass()) return false;
		final AbstractLabel obj = (AbstractLabel) that;
		return this.getLabel().equals(obj.getLabel());
		
//		boolean temp = this.getLabel().equals(obj.getLabel());
//		//System.out.println(this.getLabel() + "\t" + obj.getLabel() + "\t" + temp);
//		return temp;
	}
}

