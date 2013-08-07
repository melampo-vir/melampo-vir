package it.cnr.isti.vir.features;

import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

public interface IFeature extends Serializable {

	public void writeData(DataOutput out) throws IOException;

//	public byte[] getBytes() throws IOException;
	
//	public IFeature getFeature( Class featureClass );	
}
