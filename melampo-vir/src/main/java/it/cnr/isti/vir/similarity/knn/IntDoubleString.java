package it.cnr.isti.vir.similarity.knn;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntDoubleString extends IntDouble {

	public final String stringId;
	
	
	public IntDoubleString(int id, double dist, String stringId) {
		super(id, dist);
		this.stringId = stringId;
	}
	
	public IntDoubleString(DataInput  in) throws IOException {
		super(in);
		this.stringId = in.readUTF();
	}
	
	public final void writeData(DataOutput output) throws IOException {
		output.writeInt(id);
		output.writeDouble(dist);
		output.writeUTF(stringId);
	}
	
	public String toString() {
		return stringId + " " + String.format("%.4f", dist) + id + " ";
	}

	@Override
	public int compareTo(Object arg0) {
		int res = super.compareTo(arg0);
		if(res == 0)
			res = this.stringId.compareTo(((IntDoubleString) arg0).stringId);
		
		return res;
	}
	
	public boolean equals(Object obj) {
		if ( compareTo(obj) == 0 ) return true;
		return false;
	}

	public String getStringId() {
		return stringId;
	}

	public Double getDistance() {
		return dist;
	}

}
