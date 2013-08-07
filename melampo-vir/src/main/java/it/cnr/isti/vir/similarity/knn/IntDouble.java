package it.cnr.isti.vir.similarity.knn;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntDouble implements Comparable {

	public final int id;
	public final double dist;
	
	public IntDouble(int id, double dist) {
		this.id = id;
		this.dist = dist;
	}
	
	public IntDouble(DataInput  in) throws IOException {
		id = in.readInt();
		dist = in.readDouble();
	}
	
	public final void writeData(DataOutput output) throws IOException {
		output.writeInt(id);
		output.writeDouble(dist);
	}
	
	public String toString() {
		return " " + id + " " + dist;
	}

	@Override
	public int compareTo(Object arg0) {
		IntDouble that = (IntDouble) arg0;
		int comp = Double.compare(this.dist, that.dist); 
		if ( comp != 0 ) return comp;
		comp = this.id - that.id;
		if ( comp != 0 ) return comp;
		return 0;
	}
	
	public boolean equals(Object obj) {
		if ( compareTo(obj) == 0 ) return true;
		return false;
	}

}
