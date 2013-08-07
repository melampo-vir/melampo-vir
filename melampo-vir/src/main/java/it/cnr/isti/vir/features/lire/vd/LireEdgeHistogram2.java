package it.cnr.isti.vir.features.lire.vd;

import it.cnr.isti.vir.features.mpeg7.vd.MPEG7VDFormatException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class LireEdgeHistogram2 extends LireFeatures {

	private static final long serialVersionUID = 1L;

	static final byte version = 0;

	public LireEdgeHistogram2(ByteBuffer src) throws IOException {
		super(src, "EdgeHistogram");
	}

	public LireEdgeHistogram2(DataInput str) throws IOException {
		super(str, "EdgeHistogram");
	}

	public void writeData(DataOutput str) throws IOException {
		// TODO
	}

	public LireEdgeHistogram2(XMLStreamReader xmlr) throws XMLStreamException,
			MPEG7VDFormatException {
		super(xmlr, "EdgeHistogram");
	}

//	public int hashCode() {
//		return lireFeature.hashCode();
//	}

//	@Override
//	public String toString() {
//		return lireFeature.toString();
//	}

}
