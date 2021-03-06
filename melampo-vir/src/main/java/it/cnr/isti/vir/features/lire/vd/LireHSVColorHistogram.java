package it.cnr.isti.vir.features.lire.vd;

import it.cnr.isti.vir.features.mpeg7.vd.MPEG7VDFormatException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class LireHSVColorHistogram extends LireFeatures {

	private static final long serialVersionUID = 1L;

	static final byte version = 0;

	public LireHSVColorHistogram(ByteBuffer src) throws IOException {
		super(src, "HSVColorHistogram");
	}

	public LireHSVColorHistogram(DataInput str) throws IOException {
		super(str, "HSVColorHistogram");
	}

	public void writeData(DataOutput str) throws IOException {
		// TODO
	}

	public LireHSVColorHistogram(XMLStreamReader xmlr) throws XMLStreamException,
			MPEG7VDFormatException {
		super(xmlr, "HSVColorHistogram");
	}

//	public int hashCode() {
//		return lireFeature.hashCode();
//	}

//	@Override
//	public String toString() {
//		return lireFeature.toString();
//	}

}
