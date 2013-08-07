package it.cnr.isti.vir.features.lire.vd;

import it.cnr.isti.vir.features.mpeg7.vd.MPEG7VDFormatException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class LireFuzzyColorHistogram extends LireFeatures {

	private static final long serialVersionUID = 1L;

	static final byte version = 0;

	public LireFuzzyColorHistogram(ByteBuffer src) throws IOException {
		super(src, "FuzzyColorHistogram");
	}

	public LireFuzzyColorHistogram(DataInput str) throws IOException {
		super(str, "FuzzyColorHistogram");
	}

	public void writeData(DataOutput str) throws IOException {
		// TODO
	}

	public LireFuzzyColorHistogram(XMLStreamReader xmlr) throws XMLStreamException,
			MPEG7VDFormatException {
		super(xmlr, "FuzzyColorHistogram");
	}

//	public int hashCode() {
//		return lireFeature.hashCode();
//	}

//	@Override
//	public String toString() {
//		return lireFeature.toString();
//	}

}
