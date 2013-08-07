package it.cnr.isti.vir.features.lire.vd;

import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.features.mpeg7.vd.MPEG7VDFormatException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.semanticmetadata.lire.imageanalysis.LireFeature;

public abstract class LireFeatures implements IFeature, java.io.Serializable {

	private static final long serialVersionUID = 1L;

	static final byte version = 0;

	//public static final HashSet<String> VISUAL_FEATURES = new HashSet<String>();
	public static final String VISUAL_FEATURES_PACKAGE = "net.semanticmetadata.lire.imageanalysis";

	public static enum FEATURES {
		BasicFeatures, CEDD, FCTH, FuzzyColorHistogram, Gabor, 
		GeneralColorLayout, HSVColorHistogram, JCD, JpegCoefficientHistogram, 
		SimpleColorHistogram, Tamura,
		EdgeHistogram, ScalableColor, ColorLayout
	}

//	static {
//		// VISUAL_FEATURES.add("BasicFeatures");
//		VISUAL_FEATURES.add("CEDD");
//		VISUAL_FEATURES.add("FCTH");
//		VISUAL_FEATURES.add("FuzzyColorHistogram");
//		// VISUAL_FEATURES.add("Gabor");
//		// VISUAL_FEATURES.add("GeneralColorLayout");
//		// VISUAL_FEATURES.add("HSVColorHistogram");
//		// VISUAL_FEATURES.add("JCD");
//		// VISUAL_FEATURES.add("JpegCoefficientHistogram");
//		// VISUAL_FEATURES.add("SimpleColorHistogram");
//		// VISUAL_FEATURES.add("Tamura");
//	}

	LireFeature lireFeature;

	public LireFeatures(ByteBuffer str, String visualFeature) throws IOException {
		System.out.println("creating ByteBuffer" + visualFeature);
		byte version = str.get();
		int arrayLength = str.getInt();
		if ( arrayLength == -1 ) {
			lireFeature = null;
			return;
		}
		byte[] byteRepresentation = new byte[arrayLength];
		for (int i = 0; i < arrayLength; i++) {
			byteRepresentation[i] = str.get();
		}
		try {
			lireFeature = (LireFeature) Class.forName(
					VISUAL_FEATURES_PACKAGE + "."
							+ visualFeature).newInstance();
			lireFeature.setByteArrayRepresentation(byteRepresentation);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(lireFeature.getStringRepresentation());
	}

	public LireFeatures(DataInput str, String visualFeature) throws IOException {
		System.out.println("creating DataInput " + visualFeature);
		byte version = str.readByte();
		int arrayLength = str.readInt();
		if ( arrayLength == -1 ) {
			lireFeature = null;
			return;
		}
		byte[] byteRepresentation = new byte[arrayLength];
		for (int i = 0; i < arrayLength; i++) {
			byteRepresentation[i] = str.readByte();
		}
		try {
			lireFeature = (LireFeature) Class.forName(
					VISUAL_FEATURES_PACKAGE + "."
							+ visualFeature).newInstance();
			lireFeature.setByteArrayRepresentation(byteRepresentation);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(lireFeature.getStringRepresentation());
	}

	public boolean equals(Object obj) {
		return lireFeature.equals(obj);
	}

	public void writeData(DataOutput str) throws IOException {
		byte[] byteRepresentation = lireFeature.getByteArrayRepresentation();
		str.writeByte(version);
		if ( byteRepresentation != null ) {
			str.writeInt(byteRepresentation.length);
			System.out.println(lireFeature.getClass().getName() + " - " + byteRepresentation.length);
			for (int i=0; i<byteRepresentation.length; i++) {
				str.writeByte(byteRepresentation[i]);
			}
		} else {
			str.writeInt(-1);
		}
	}

	public LireFeatures(XMLStreamReader xmlr, String visualFeature)
			throws XMLStreamException, MPEG7VDFormatException {
		//System.out.println("creating XMLStreamReader " + visualFeature);
		for (int event = xmlr.getEventType(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr
				.next()) {
			switch (event) {
			case XMLStreamConstants.START_ELEMENT:
				if (xmlr.getLocalName().equals(visualFeature)) {
					// System.out.println(this.toString() + " " +
					// xmlr.getLocalName());
					try {
						lireFeature = (LireFeature) Class.forName(
								VISUAL_FEATURES_PACKAGE + "."
										+ xmlr.getLocalName()).newInstance();
						lireFeature.setStringRepresentation(xmlr
								.getElementText());
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//System.out.println(lireFeature.getStringRepresentation());
					return;
				}

				break;
			case XMLStreamConstants.END_ELEMENT:
				break;
			} // end switch
		} // end while
	}

	// public int hashCode() {
	// return lireFeature.hashCode();
	// }

	public final static double mpeg7XMDistance(LireFeatures d1, LireFeatures d2) {
		return d1.lireFeature.getDistance(d2.lireFeature);
	}

	// @Override
	// public String toString() {
	// return lireFeature.toString();
	// }

	public boolean equals(IFeature givenVD) {
		return lireFeature.equals(givenVD);
	}

	public double getMaxDistance() {
		// TODO Auto-generated method stub
		return 3000;
	}
}
