package it.cnr.isti.vir.id;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public interface IID extends Comparable, IHasID, Serializable {

	public void writeData(DataOutput out) throws IOException;
	
//	public IID[] readArray(DataInput in, int n) throws IOException;
}
