package it.cnr.isti.vir.features;

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.LabelClasses;
import it.cnr.isti.vir.id.IDClasses;
import it.cnr.isti.vir.id.IID;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class FeaturesCollectorArr implements IFeaturesCollector_Labeled_HasID {

	public IID id;
	
	public AbstractLabel cl;
	
	private IFeature[] feature;

	public FeaturesCollectorArr() {
		id = null;
		cl = null;
		feature = null;
	}
	
	public FeaturesCollectorArr(Collection<IFeature> coll, IID id, AbstractLabel cl) {
		this.id=id;
		this.cl=cl;
		int size = coll.size();
		feature = new IFeature[size];
		int i = 0;
		for ( Iterator<IFeature> it=coll.iterator(); it.hasNext(); i++) {
			IFeature curr = it.next();
			feature[i]=curr;
		}
		
	}
	
	public FeaturesCollectorArr(ByteBuffer src) throws IOException  {
		id = IDClasses.readData(src);
		cl = LabelClasses.readData(src);
		int size = src.get();
		feature = new IFeature[size];
		for ( int i=0; i<feature.length; i++) {
			feature[i] = FeatureClasses.readData(src, this);
		}
	}
	
	public FeaturesCollectorArr(DataInput in) throws IOException {
		id = IDClasses.readData(in);
		cl = LabelClasses.readData(in);
		int size = in.readByte();
		feature = new IFeature[size];
		for ( int i=0; i<feature.length; i++) {
			feature[i] = FeatureClasses.readData(in, this);
		}
	}
		
	@Override
	public void writeData(DataOutput out) throws IOException {
		IDClasses.writeData(id, out);
		LabelClasses.writeData(cl, out);
		out.writeByte( (byte) feature.length);
		for ( int i=0; i<feature.length; i++) {
			FeatureClasses.writeData(out, feature[i]);
		}
	}

	@Override
	public int compareTo(Object obj) {
		return hashCode()-((FeaturesCollectorArr) obj).hashCode();
	}

	@Override
	public final AbstractLabel getLabel() {
		return cl;
	}

	@Override
	public final IID getID() {
		return id;
	}

	@Override
	public IFeature getFeature(Class featureClass) {
		for ( int i=0; i<feature.length; i++) {
			if ( feature[i] == null ) {
				System.err.println("Feature null!!!!");
				continue;
			}
			if ( featureClass.equals(feature[i].getClass())) {
				return feature[i];
			}
		}
		return null;
	}

	@Override
	public void add(IFeature f) {
			
		if ( feature == null ) {
			feature = new IFeature[1];
			feature[0] = f;
		} else {
			
			int size = feature.length+1;
			IFeature[] temp = new IFeature[size];
			for (int i=0; i<feature.length; i++) {
				temp[i]=feature[i];
			}
			temp[size-1]=f;
			
			feature = temp;
		}
	}

	@Override
	public void discardAllBut( 	FeatureClassCollector featuresClasses )
								throws FeaturesCollectorException {
		if ( featuresClasses == null ) {
			feature = null;
			return;
		}
		int size = featuresClasses.size();
		IFeature[] temp = new IFeature[size];
		int count = 0;
		for ( int i=0; i<feature.length; i++) {
			if ( featuresClasses.contains(feature[i].getClass())) {
				temp[count++]=feature[i];
			}
		}
		if ( count != size ) {
			throw new FeaturesCollectorException("Not all the requested features were found");
		}
		feature = temp;
	}

	@Override
	public Collection<IFeature> getFeatures() {
		return Arrays.asList(feature);
	}

	@Override
	public boolean contains(Class c) {
		for ( int i=0; i<feature.length; i++) {
			if ( feature[i].getClass().equals(c)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean equals(Object obj) {
		if ( obj == null ) return false;
		if ( this == obj ) return true;
		FeaturesCollectorArr that = (FeaturesCollectorArr) obj;
		if ( this.id != that.id && this.id != null && !this.id.equals(that.id)) return false;
		if ( this.cl != that.cl && this.cl != null && !this.cl.equals(that.cl)) return false;
		if ( this.feature.length != that.feature.length )return false;
		for ( int i=0; i<feature.length; i++) {
			if ( !feature[i].equals(that.feature[i])) return false;
		}
		return true;
	}
	
	public int hashCode() {
		int hashCode = 1;
		for ( int i=0; i<feature.length; i++) {
			hashCode = 31*hashCode + feature[i].hashCode();
		}
		return hashCode;
	}

	public final void setID(IID newInstance) {
		this.id = newInstance;		
	}

	@Override
	public void setLabel(AbstractLabel label) {
		cl = label;
	}
	
	public String toString() {
		String tStr = "";
		for ( int i=0; i<feature.length; i++) {
			tStr += feature[i].getClass() + "\n";
			tStr += feature[i] + "\n"; 
		}
		return tStr;
	}


	
	
}
