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

public final class VirLireFeatures implements IFeature, java.io.Serializable {

	private static final long serialVersionUID = 1L;

	static final byte version = 0;

	public static final HashSet<String> VISUAL_FEATURES = new HashSet<String>();
	public static final String VISUAL_FEATURES_PACKAGE = "net.semanticmetadata.lire.imageanalysis";
	
	static{ 
		//VISUAL_FEATURES.add("BasicFeatures");
		VISUAL_FEATURES.add("CEDD");
		VISUAL_FEATURES.add("FCTH");
		VISUAL_FEATURES.add("FuzzyColorHistogram");
//		VISUAL_FEATURES.add("Gabor");
//		VISUAL_FEATURES.add("GeneralColorLayout");
//		VISUAL_FEATURES.add("HSVColorHistogram");
//		VISUAL_FEATURES.add("JCD");
//		VISUAL_FEATURES.add("JpegCoefficientHistogram");
//		VISUAL_FEATURES.add("SimpleColorHistogram");
//		VISUAL_FEATURES.add("Tamura");
	}

	LireFeature lireFeature;

	public VirLireFeatures(ByteBuffer src) throws IOException {
		// TODO
	}

	public VirLireFeatures(DataInput str) throws IOException {
		// TODO
	}

	public boolean equals(Object obj) {
		return lireFeature.equals(obj);
	}

	public void writeData(DataOutput str) throws IOException {
		// TODO
	}

	public VirLireFeatures(XMLStreamReader xmlr) throws XMLStreamException,
			MPEG7VDFormatException {
		for (int event = xmlr.getEventType(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr
				.next()) {
			switch (event) {
			case XMLStreamConstants.START_ELEMENT:
				if (VISUAL_FEATURES.contains(xmlr.getLocalName())) {
					System.out.println(this.toString() + " " + xmlr.getLocalName());
					try {
						lireFeature = (LireFeature) Class.forName(VISUAL_FEATURES_PACKAGE + "." + xmlr.getLocalName()).newInstance();
						lireFeature.setStringRepresentation(xmlr.getElementText());
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
					return;
				}

				break;
			case XMLStreamConstants.END_ELEMENT:
				break;
			} // end switch
		} // end while
	}

//	public int hashCode() {
//		return lireFeature.hashCode();
//	}

	public final static double mpeg7XMDistance(VirLireFeatures d1, VirLireFeatures d2) {
		return d1.lireFeature.getDistance(d2.lireFeature);
	}

//	@Override
//	public String toString() {
//		return lireFeature.toString();
//	}

	public boolean equals(IFeature givenVD) {
		return lireFeature.equals(givenVD);
	}

	public double getMaxDistance() {
		// TODO Auto-generated method stub
		return 3000;
	}
}
