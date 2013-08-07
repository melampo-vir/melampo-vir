package it.cnr.isti.vir.classification;

public class LabeledObject implements ILabeled {

	private final AbstractLabel label;
	private final Object obj;
	
	public LabeledObject(Object obj, AbstractLabel label) {
		super();
		this.obj = obj;
		this.label = label;
	}

	@Override
	public AbstractLabel getLabel() {
		return label;
	}

}
