package it.cnr.isti.vir.readers;

import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.FeaturesCollectorException;
import it.cnr.isti.vir.features.FeaturesCollectorArr;
import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.features.lire.vd.CcDominantColor;
import it.cnr.isti.vir.features.lire.vd.LireBasicFeatures;
import it.cnr.isti.vir.features.lire.vd.LireCEDD;
import it.cnr.isti.vir.features.lire.vd.LireColorLayout;
import it.cnr.isti.vir.features.lire.vd.LireColorLayout2;
import it.cnr.isti.vir.features.lire.vd.LireEdgeHistogram;
import it.cnr.isti.vir.features.lire.vd.LireEdgeHistogram2;
import it.cnr.isti.vir.features.lire.vd.LireFCTH;
import it.cnr.isti.vir.features.lire.vd.LireFuzzyColorHistogram;
import it.cnr.isti.vir.features.lire.vd.LireGabor;
import it.cnr.isti.vir.features.lire.vd.LireGeneralColorLayout;
import it.cnr.isti.vir.features.lire.vd.LireHSVColorHistogram;
import it.cnr.isti.vir.features.lire.vd.LireJpegCoefficientHistogram;
import it.cnr.isti.vir.features.lire.vd.LireScalableColor;
import it.cnr.isti.vir.features.lire.vd.LireScalableColor2;
import it.cnr.isti.vir.features.lire.vd.LireSimpleColorHistogram;
import it.cnr.isti.vir.features.lire.vd.LireTamura;
import it.cnr.isti.vir.features.mpeg7.vd.MPEG7VDFormatException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Constructor;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class LireMPEG7VDs {
//	static final FeatureClassCollector lireMpeg7FCC = new FeatureClassCollector(	LireColorLayout.class,
//																					LireScalableColor.class,
//																					LireEdgeHistogram.class);	
//	
	public static FeaturesCollectorArr getFeaturesCollection(File file) throws XMLStreamException, MPEG7VDFormatException, FactoryConfigurationError, FileNotFoundException, FeaturesCollectorException {
		BufferedReader br = new BufferedReader( new FileReader(file) );
		return getFeaturesCollection( XMLInputFactory.newInstance().createXMLStreamReader(br));
	}

	
	public static FeaturesCollectorArr getFeaturesCollection(File file, FeatureClassCollector fClasses, FeatureClassCollector regionFClasses  ) throws XMLStreamException, MPEG7VDFormatException, FactoryConfigurationError, FileNotFoundException, FeaturesCollectorException {
		BufferedReader br = new BufferedReader( new FileReader(file) );
		return getFeaturesCollection( XMLInputFactory.newInstance().createXMLStreamReader(br), fClasses, regionFClasses);
	}
	
	
	public static FeaturesCollectorArr getFeaturesCollection( StringBuffer sb, FeatureClassCollector fClasses, FeatureClassCollector regionFClasses )  throws XMLStreamException, FactoryConfigurationError, MPEG7VDFormatException, FeaturesCollectorException {
		return  getFeaturesCollection( 	XMLInputFactory.newInstance().createXMLStreamReader( new ByteArrayInputStream( sb.toString().getBytes() )),
										fClasses,
										regionFClasses );
	}
	
	public static FeaturesCollectorArr getFeaturesCollection( StringBuffer sb, FeatureClassCollector fClasses )  throws XMLStreamException, FactoryConfigurationError, MPEG7VDFormatException, FeaturesCollectorException {
		return  getFeaturesCollection( 	XMLInputFactory.newInstance().createXMLStreamReader( new ByteArrayInputStream( sb.toString().getBytes() )),
										fClasses,
										null );
	}
	
	public static FeaturesCollectorArr getFeaturesCollection( XMLStreamReader xmlr  ) throws XMLStreamException, MPEG7VDFormatException, FeaturesCollectorException {
		return getFeaturesCollection( xmlr, null, null );
	}
	
	static private boolean toRead( Class c, FeatureClassCollector fClasses) {
		//all features are read if not fClasses are specified
		if ( fClasses == null ) return true;
		
		return  fClasses == null || fClasses.contains(c);
	}
	
	protected static FeaturesCollectorArr getFeaturesCollection( XMLStreamReader xmlr, FeatureClassCollector fClasses, FeatureClassCollector regionFClasses ) throws XMLStreamException, MPEG7VDFormatException, FeaturesCollectorException {
		
		FeaturesCollectorArr mainFC = new FeaturesCollectorArr();
		FeaturesCollectorArr currFC = mainFC; 
		
		for (int event = xmlr.next();  
	    	event != XMLStreamConstants.END_DOCUMENT;
	    	event = xmlr.next()) {
	    	switch (event) {
	        	case XMLStreamConstants.START_ELEMENT:

//				try {
//					if (VirLireFeatures.VISUAL_FEATURES.contains(xmlr.getLocalName())  && toRead(Class.forName(VirLireFeatures.VISUAL_FEATURES_PACKAGE + "." + xmlr.getLocalName()), fClasses ) ) {
//	        			currFC.add( new VirLireFeatures( xmlr ) );
//	        		}
//				} catch (ClassNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
	        		
	        		if (xmlr.getLocalName().equals("FCTH")  && toRead(LireFCTH.class, fClasses ) ) {
	        			currFC.add( new LireFCTH( xmlr ) );
	        		} 
	        		else if (xmlr.getLocalName().equals("CEDD")  && toRead(LireCEDD.class, fClasses) ) {
		        			currFC.add( new LireCEDD( xmlr ) );
		        		} 
	        		else if (xmlr.getLocalName().equals("FuzzyColorHistogram")  && toRead(LireFuzzyColorHistogram.class, fClasses) ) {
	        			currFC.add( new LireFuzzyColorHistogram( xmlr ) );
	        		}
	        		else if (xmlr.getLocalName().equals("EdgeHistogram")  && toRead(LireEdgeHistogram.class, fClasses) ) {
	        			currFC.add( new LireEdgeHistogram( xmlr ) );
	        		}  
	        		else if (xmlr.getLocalName().equals("ScalableColor")  && toRead(LireScalableColor.class, fClasses) ) {
	        			currFC.add( new LireScalableColor( xmlr ) );
	        		}  
	        		else if (xmlr.getLocalName().equals("ColorLayout")  && toRead(LireColorLayout.class, fClasses) ) {
	        			currFC.add( new LireColorLayout( xmlr ) );
	        		}
	        		else if (xmlr.getLocalName().equals("BasicFeatures")  && toRead(LireBasicFeatures.class, fClasses) ) {
	        			currFC.add( new LireBasicFeatures( xmlr ) );
	        		}
	        		else if (xmlr.getLocalName().equals("Gabor")  && toRead(LireGabor.class, fClasses) ) {
	        			currFC.add( new LireGabor( xmlr ) );
	        		} 
	        		else if (xmlr.getLocalName().equals("GeneralColorLayout")  && toRead(LireGeneralColorLayout.class, fClasses) ) {
	        			currFC.add( new LireGeneralColorLayout( xmlr ) );
	        		} 
	        		else if (xmlr.getLocalName().equals("HSVColorHistogram")  && toRead(LireHSVColorHistogram.class, fClasses) ) {
	        			currFC.add( new LireHSVColorHistogram( xmlr ) );
	        		} 
	        		else if (xmlr.getLocalName().equals("JpegCoefficientHistogram")  && toRead(LireJpegCoefficientHistogram.class, fClasses) ) {
	        			currFC.add( new LireJpegCoefficientHistogram( xmlr ) );
	        		} 
	        		else if (xmlr.getLocalName().equals("SimpleColorHistogram")  && toRead(LireSimpleColorHistogram.class, fClasses) ) {
	        			currFC.add( new LireSimpleColorHistogram( xmlr ) );
	        		} 
	        		else if (xmlr.getLocalName().equals("Tamura")  && toRead(LireTamura.class, fClasses) ) {
	        			currFC.add( new LireTamura( xmlr ) );
	        		} 	
	        		else if (xmlr.getLocalName().equals("CcDominantColor")){
	        			//TODO: update the implementation of the other classes to parse the input correcty if the feature is to be ignored
	        			IFeature dominantColor = new CcDominantColor( xmlr );
	        			if(toRead(CcDominantColor.class, fClasses))
	        				currFC.add(dominantColor);
	        			
//	        			Constructor<?> constructor;
//	        			IFeature ccDominantColor;
//						try {
//							constructor = Class.forName("it.cnr.isti.vir.features.lire.vd.CcDominantColor").getConstructor(XMLStreamReader.class);
//							ccDominantColor = (IFeature) constructor.newInstance(xmlr);
//							currFC.add( ccDominantColor);
//						} catch (Exception e) {
//							//throw new RuntimeException("Cannot instantionate CcDominantColor for xml stream!", e);
//							System.err.println("Cannot instantiate CcDominantColor for xml stream!");
//							e.printStackTrace();
//						}
					} 	
	        		break;
       			
	            case XMLStreamConstants.END_ELEMENT:
	            	if (xmlr.getLocalName().equals("lire") ) { 
	            		//System.out.print(".");
//	            		System.out.println(toString());

	            				
	            		// checking features in mainFC
//	            		if ( fClasses != null &&  !FeatureClassCollector.getIntersection(fClasses,lireMpeg7FCC).areIn(mainFC) ) {
//	            			System.err.println("Not all the features were found.");
//	            			return null;
//	            		}
	            		
	            		
	            		
	            		return currFC;
	            	} 
	            	break;
	            case XMLStreamConstants.CHARACTERS:
	
	            	break;
	            case XMLStreamConstants.CDATA:
	
	            	break;
	    	} // end switch
	    	
	    } // end for
		
		System.err.println("lire tag not found!!");
		return null;
	}
	
}
