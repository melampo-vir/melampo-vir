package it.cnr.isti.vir.features.lire.vd;

import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.features.mpeg7.vd.MPEG7VDFormatException;
import it.cnr.isti.vir.util.Convertions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class CcDominantColor implements IFeature, java.io.Serializable {

	private static final long serialVersionUID = 1L;
	byte version = 0;
	byte length = 10;

	short[] h;
	short[] s;
	short[] v;
	short[] score;
	List<String> centroids;

	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeByte(version);
		out.writeByte(length);
		for (int i = 0; i < length; i++) {
			out.writeShort(h[i]);
			out.writeShort(s[i]);
			out.writeShort(v[i]);
			out.writeShort(score[i]);
		}
	}

	public CcDominantColor(DataInput str) throws IOException {
		version = str.readByte();
		length = str.readByte();
		initArrays();
		
		for (int i = 0; i < length; i++) {
			h[i] = str.readShort();
			s[i] = str.readShort();
			v[i] = str.readShort();
			score[i] = str.readShort();
			centroids.add(toHexString(h[i], s[i], v[i]));
		}
	}

	protected void initArrays() {
		h = new short[length];
		s = new short[length];
		v = new short[length];
		score = new short[length];
		centroids = new ArrayList<String>(length);
	}
	
	public CcDominantColor(ByteBuffer src) throws IOException {
		version = src.get();
		length = src.get();
		for (int i = 0; i < length; i++) {
			h[i] = src.get();
			s[i] = src.get();
			v[i] = src.get();
			score[i] = src.get();
			centroids.add(toHexString(h[i], s[i], v[i]));
		}
	}
	
	public CcDominantColor(XMLStreamReader xmlr) throws XMLStreamException, MPEG7VDFormatException {
		//protected void parse( XMLStreamReader xmlr ) throws XMLStreamException, MPEG7VDFormatException  {
		
		//byte[] temp = null;
		for (int event = xmlr.getEventType();
	    	event != XMLStreamConstants.END_DOCUMENT;
	    	event = xmlr.next()) {
	    	switch (event) {
	        	case XMLStreamConstants.START_ELEMENT:
	            	if (xmlr.getLocalName().equals("CcDominantColor") ) { 
	            		String[] values = Convertions.getNumbers(xmlr.getElementText());
	            		int offset = 0;
	            		
	            		assert values.length == 4*length : values.length;
	            		initArrays();
	            		
	            		for(int i = 0; i < length; i++){
	            			offset = 4*i;
	            			
							h[i] = (short) (Integer.parseInt(values[offset]));//first value
	            			s[i] = (short) (Integer.parseInt(values[offset+1]));//next
	            			v[i] = (short) (Integer.parseInt(values[offset+2]));//next
	            			score[i] = (short) Integer.parseInt(values[offset+3]);//next
	            			centroids.add(toHexString(h[i], s[i], v[i]));	
	            		}
	            		//do not parse the rest of the stream here
	            		return;
	            	}
	            	break;
	            case XMLStreamConstants.END_ELEMENT:
	            	break;
	    	} // end switch
	    } // end for/while
	}

	public CcDominantColor(short[] h, short[] s, short[] v, short[] score) {
		this.h = h;
		this.s = s;
		this.v = v;
		this.score = score;
	}

	public String toString() {
		StringBuilder str = new StringBuilder("CcDominantColor: ");
		for (int i = 0; i < length; i++) {
			str.append("\n");
			str.append(toHexString(h[i], s[i], v[i]));
			str.append(" ");
			str.append(score[i]);
		}
		return str.toString();
	}

	
	private String toHexString(int i) {
		String ret = Integer.toHexString(i);
		if(ret.length() == 1)
			ret = "0"+ret;
		return ret;
	}
	
	private String toHexString(int h, int s, int v) {
		return toHexString(h) + toHexString(s) + toHexString(v);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		return this.getClass().equals(obj.getClass()) && this.toString().equals(obj.toString());
		
	}

	public short[] getScore() {
		return score;
	}

	public List<String> getCentroids() {
		return centroids;
	}
	
//	protected byte toByte(String val){
//		//(byte) (Integer.parseInt("0") -128);
//		return (byte) (Integer.parseInt(val));
//	}
	
}
