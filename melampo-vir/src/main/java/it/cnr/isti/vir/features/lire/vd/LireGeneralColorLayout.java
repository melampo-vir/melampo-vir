package it.cnr.isti.vir.features.lire.vd;

import it.cnr.isti.vir.features.mpeg7.vd.MPEG7VDFormatException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class LireGeneralColorLayout extends LireFeatures {

	private static final long serialVersionUID = 1L;

	static final byte version = 0;

	public LireGeneralColorLayout(ByteBuffer src) throws IOException {
		super(src, "GeneralColorLayout");
	}

	public LireGeneralColorLayout(DataInput str) throws IOException {
		super(str, "GeneralColorLayout");
	}

	public void writeData(DataOutput str) throws IOException {
		// TODO
	}

	public LireGeneralColorLayout(XMLStreamReader xmlr) throws XMLStreamException,
			MPEG7VDFormatException {
		super(xmlr, "GeneralColorLayout");
	}

//	public int hashCode() {
//		return lireFeature.hashCode();
//	}

//	@Override
//	public String toString() {
//		return lireFeature.toString();
//	}

}
