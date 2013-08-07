package it.cnr.isti.vir.features;

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.id.IID;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


public class FeaturesCollector_SingleWithIDClassified implements IFeaturesCollector_Labeled_HasID {

	protected final IFeature f;
	protected AbstractLabel l;
	protected final IID id;
	
	public FeaturesCollector_SingleWithIDClassified(IFeature f ) {
		this(f, null);
	}
	
	public FeaturesCollector_SingleWithIDClassified(IFeature f, IID id) {
		this(f, id, null);
	}
	
	public FeaturesCollector_SingleWithIDClassified(IFeature f, IID id, AbstractLabel l) {
		this.f = f;
		this.l = l;
		this.id = id;
	}
	
	@Override
	public IFeature getFeature(Class featureClass) {
		return f;
	}

	@Override
	public void add(IFeature f) {
		
	}

	@Override
	public void discardAllBut(FeatureClassCollector featuresClasses) throws FeaturesCollectorException {
		throw new FeaturesCollectorException("Method not implemented");
	}

	@Override
	public void writeData(DataOutput out) throws IOException {
		FeatureClasses.writeData(out, f);
	}

	@Override
	public AbstractLabel getLabel() {
		return l;
	}

	@Override
	public IID getID() {
		return id;
	}

	@Override
	public int compareTo(Object obj) {
		if ( id != null ) return  id.compareTo( ((FeaturesCollector_SingleWithIDClassified) obj).id);
		else return hashCode()-((FeaturesCollector_SingleWithIDClassified) obj).hashCode();
	}
	
	public boolean equals(Object obj) {
		if ( compareTo(obj) == 0 ) return true;
		return false;
	}
	
	public int hashCode() {
		
		int hashCode = f.hashCode();
		
		return hashCode;
	}

	@Override
	public Collection<IFeature> getFeatures() {
		ArrayList t =  new ArrayList(1);
		t.add(f);
		return t;
	}

	@Override
	public boolean contains(Class c) {
		if ( f.getClass().equals(c) ) return true;
		return false;
	}

	@Override
	public void setLabel(AbstractLabel label) {
		l = label;
	}

}
