package it.cnr.isti.vir.classification;

import it.cnr.isti.vir.id.IDClasses;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.id.IID;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LabeledID<IDC, LABELC> implements ILabeled, IHasID {

	public final IID id;
	public final AbstractLabel label;
	
	public LabeledID (IDC id, LABELC label ) {
		this.id = (IID) id;
		this.label = (AbstractLabel) label;
	}

	@Override
	public AbstractLabel getLabel() {
		return label;
	}

	@Override
	public int compareTo(Object arg0) {
		return id.compareTo( ((LabeledID)arg0).id);
	}
	
	public boolean equals(Object arg0) {
		return id.equals( ((LabeledID)arg0).id);
	}

	@Override
	public IID getID() {
		return id;
	}
	
	public void writeData(DataOutput out) throws IOException {
		IDClasses.writeData(id, out);
		LabelClasses.writeData(label, out);
	}
	
	public LabeledID(DataInput in) throws IOException {
		id = IDClasses.readData(in);
		label = LabelClasses.readData(in);
	}
	
	
}
