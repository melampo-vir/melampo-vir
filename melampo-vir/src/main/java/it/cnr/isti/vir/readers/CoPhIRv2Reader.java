package it.cnr.isti.vir.readers;

import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.FeaturesCollectorArr;
import it.cnr.isti.vir.features.FeaturesCollectorException;
import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.features.mpeg7.vd.MPEG7VDFormatException;
import it.cnr.isti.vir.id.IDString;
import it.cnr.isti.vir.id.IID;
import it.cnr.isti.vir.util.FileNames;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;


public class CoPhIRv2Reader implements ObjectsReader<FeaturesCollectorArr> {
	
	//File currDirectory = null;
	File[] currFiles = null;
	int currFileIndex = 0;
	BufferedReader br = null;
	static Class idClass = IDString.class;
	
	public static final Class getIdClass() {
		return idClass;
	}

	public static final void setIdClass(Class idClass) {
		CoPhIRv2Reader.idClass = idClass;
	}

	static double maxColorSIFTscale = 0;
	static double maxSIFTscale = 0;
	static double maxSURFscale = 0;
	


	static int maxSIFTn = -1;
	static int maxSURFn = -1;
	static int maxColorSIFTn = -1;
	
	public static FeatureClassCollector fCC = null;
//            new FeatureClassCollector(
//			SIFTGroup.class,
//			SURFGroup.class,
//			ColorSIFTGroup.class,
//			ColorLayout.class,
//			ColorStructure.class,
//			ScalableColor.class,
//			EdgeHistogram.class,
//			HomogeneousTexture.class );

	final static private Pattern p = Pattern.compile("<photo id=\"([-\\d]*)\"");
	
	public static void setFeatures(FeatureClassCollector fcc) {
		fCC = fcc;
	}
	
	public CoPhIRv2Reader() {};
	
	public void openDirectory(File dir) throws IOException {
		
		currFiles = getFilesInDirectory(dir);

	    currFileIndex = 0;
	    if ( br != null ) {
	    	br.close();
	    }
	    br = null;
	}
	
	protected static final File[] getFilesInDirectory(File dir) {
	    FilenameFilter filter = new FilenameFilter() {
	        public boolean accept(File dir, String name) {
	            return name.endsWith(".xml") || name.endsWith(".xml") || name.endsWith(".gz");
	        }
	    };		
	    return dir.listFiles(filter);
	}
	
	public static final LinkedList<FeaturesCollectorArr> getAllObjsInDir(File dir) throws IOException, FactoryConfigurationError, MPEG7VDFormatException, XMLStreamException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException, FeaturesCollectorException {
		return getObjsInDir(dir, (Integer) null);
	}
	
	public static final LinkedList<FeaturesCollectorArr> getObjsInDir(File dir, Collection ids, String extension) throws IOException, FactoryConfigurationError, MPEG7VDFormatException, XMLStreamException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException, FeaturesCollectorException {
		File[] files = new File[ids.size()];
		int i=0;
		for ( Iterator it=ids.iterator(); it.hasNext(); ) {
			files[i++] = new File(dir.getAbsolutePath()+File.separator+it.next().toString()+extension); 
		}
		return getObjs(files, null);
	}
	
	public static final LinkedList<FeaturesCollectorArr> getObjsInDir(File dir, Integer maxN) throws IOException, FactoryConfigurationError, MPEG7VDFormatException, XMLStreamException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException, FeaturesCollectorException {
		File[] files = getFilesInDirectory(dir);
		return getObjs(files, maxN);
	}
	
/* OLD	
	public static final LinkedList<FeaturesCollWithID> getObjs(File[] files, Integer maxN) throws IOException, FactoryConfigurationError, MPEG7VDFormatException, XMLStreamException, InstantiationException, IllegalAccessException {
		LinkedList<FeaturesCollWithID> list = new LinkedList<FeaturesCollWithID>();
		
		int globalCount = 0;
		for (int i=0; i<files.length && (maxN==null || globalCount<maxN); i++) {
			//System.out.print("Reading " + files[i].getAbsolutePath() + " ... ");
			BufferedReader br = new BufferedReader( new FileReader( files[i] ) );
			FeaturesCollWithID currObj = null;
			int count = 0;
			while ( (currObj = getObj(br)) != null ) {
				list.add(currObj);
				count++;
			}
			globalCount += count;
			System.out.println( list.size() + ": " +  count + " obj(s) found in " +  files[i].getAbsolutePath());
			br.close();
		}
		//System.out.println(list.size()+ " object(s) found in " + files.length + " file(s)");
		
		return list;
	}
	*/
	
	public static final LinkedList<FeaturesCollectorArr> getObjs(File[] files, Integer maxN) throws IOException, FactoryConfigurationError, MPEG7VDFormatException, XMLStreamException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException, FeaturesCollectorException {
		LinkedList<FeaturesCollectorArr> list = new LinkedList<FeaturesCollectorArr>();
		
		int globalCount = 0;
		for (int i=0; i<files.length && (maxN==null || globalCount<maxN); i++) {
			FeaturesCollectorArr curr = getObj(files[i]);
			if ( curr != null ){ 
				list.add(getObj(files[i]));
			}			
			globalCount += list.size();
			System.out.println( list.size() + ": " +  list.size() + " obj(s) found in " +  files[i].getAbsolutePath());
		}
		//System.out.println(list.size()+ " object(s) found in " + files.length + " file(s)");
		
		return list;
	}

	public final FeaturesCollectorArr getObj() throws IOException, FactoryConfigurationError, MPEG7VDFormatException, XMLStreamException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException, FeaturesCollectorException {
		FeaturesCollectorArr fc = null;
		
		while (fc == null ) {
			if ( br != null ) {
				try {
					fc = getObj(br);
					if ( fc != null ) return fc;
					br.close();
					br = null;
				} catch ( Exception e) {
					e.printStackTrace();
					br = null;
					continue;
				}
			}
			
			if ( currFiles.length == 0 || currFileIndex == currFiles.length) return null;
			if (currFiles != null && currFileIndex<currFiles.length) {
				File currFile = currFiles[currFileIndex++];
				if ( br != null) br.close();
				String filename = currFile.getName();
				//System.out.println(filename);
				String ext = (filename.lastIndexOf(".")==-1)?"":filename.substring(filename.lastIndexOf(".")+1,filename.length());
				if ( ext.equals("gz")) {
					//System.out.println("zipped file");
					br = new BufferedReader(
							new InputStreamReader(
									new GZIPInputStream(
											new FileInputStream(currFile)) ));
				}
				else br = new BufferedReader( new FileReader(currFile));
				
				fc = getObj(br);
				
				if ( fc != null && fc.id == null ) {					
					//if ( idClass.equals(IDLong.class)) fc.id = new IDLong(Long.parseLong(filename.substring(0, filename.indexOf('.'))));
					fc.id = (IID) idClass.getConstructor(String.class).newInstance(filename.substring(0, filename.indexOf('.')));
				}
				if ( fc != null ) return fc;
				System.err.println("Null objects in " + currFiles[currFileIndex].getAbsolutePath());
			}
		}	
		
		return null;
		
	}
	
	public final FeaturesCollectorArr getObj_OnePerFile() throws IOException, FactoryConfigurationError, MPEG7VDFormatException, XMLStreamException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException, FeaturesCollectorException {
		
		FeaturesCollectorArr fc = null;
		while (true) {
			if ( currFiles.length == 0 || currFileIndex == currFiles.length) return null;
			if (currFiles != null && currFileIndex<currFiles.length) {
				File currFile = currFiles[currFileIndex++];
				if ( br != null) br.close();
				String filename = currFile.getName();
				//System.out.println(filename);
				String ext = (filename.lastIndexOf(".")==-1)?"":filename.substring(filename.lastIndexOf(".")+1,filename.length());
				if ( ext.equals("gz")) {
					//System.out.println("zipped file");
					br = new BufferedReader(
							new InputStreamReader(
									new GZIPInputStream(
											new FileInputStream(currFile)) ));
				}
				else br = new BufferedReader( new FileReader(currFile), 131072);
				
				fc = getObj(br);
				
				if ( fc != null && fc.id == null ) {					
					//if ( idClass.equals(IDLong.class)) fc.id = new IDLong(Long.parseLong(filename.substring(0, filename.indexOf('.'))));
					fc.id = (IID) idClass.getConstructor(String.class).newInstance(filename.substring(0, filename.indexOf('.')));
				}
				if ( fc != null ) return fc;
				System.err.println("No objects were found."); // in " + currFiles[currFileIndex].getAbsolutePath());
			}
		} 
		
	}
	
	public final static FeaturesCollectorArr getObj(File  f) throws IOException, FactoryConfigurationError, MPEG7VDFormatException,
	XMLStreamException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException, FeaturesCollectorException {
		BufferedReader br = new BufferedReader(new FileReader(f));
		FeaturesCollectorArr obj =  getObj(br);
		if ( obj == null) {
			System.out.println("Error reading " + f.getAbsolutePath());
			return null;
		}
		if ( obj.id == null ) {
//			String fName = f.getName().split("\\.")[0];
			String fName = FileNames.getFileNameWithoutExtension(f.getName());
			//System.out.println(fName);
			obj.id = (IID) idClass.getConstructor(String.class).newInstance(fName);
		}
		br.close();
		return obj;
	}
	
	
	public final static FeaturesCollectorArr getObjFromGZ(File  f) throws IOException, FactoryConfigurationError, MPEG7VDFormatException,
	XMLStreamException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException, FeaturesCollectorException {
		FeaturesCollectorArr obj =  getObj(new BufferedReader(
													new InputStreamReader(
															new GZIPInputStream(
															new FileInputStream(f)) )));
		if ( obj == null ) return null;
		if ( obj.id == null ) {
			String fName = f.getName().split("\\.")[0];
			obj.setID( (IID) idClass.getConstructor(String.class).newInstance(fName) );
		}
		return obj;
	}
	
	public final static FeaturesCollectorArr getObjFromGZ(File  f, IID id ) throws IOException, FactoryConfigurationError, MPEG7VDFormatException,
	XMLStreamException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException, FeaturesCollectorException {
		FeaturesCollectorArr obj =  getObj(new BufferedReader(
													new InputStreamReader(
															new GZIPInputStream(
																	new FileInputStream(f)) )));
		obj.setID( id );
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public final static FeaturesCollectorArr getObj(BufferedReader br) throws IOException,
		FactoryConfigurationError, MPEG7VDFormatException,
		XMLStreamException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException, FeaturesCollectorException {
		IID id = null;
		String line = null;
		
//		FeaturesCollectorArr fc = new FeaturesCollectorArr();
		LinkedList<IFeature> fList = new LinkedList();

                while ( (line = br.readLine()) != null ) {
	    		    	
                    if ( line.matches("^<CoPhIR>.*") || line.matches("^<VISITO>.*") || line.matches("^<IRImage>.*") || line.matches("^<ASSETS>.*")) {
                            //sb = new StringBuffer ();
                            //sb.delete(0, sb.length());
                            //sb.append("<SapirMMObject>");

                            while ( (line = br.readLine()) != null ) { //&& fCC.size() != fList.size() ) {

                            // This removes <photo> content keeping the ID
                            if ( line.matches("^<photo.*") ) {
                                    // Getting the integer ID

                                    Matcher m = p.matcher(line);
                                    m.find();
                                    if (id != null) {
                                            System.err.println("Error reading: " + id + "\n");
                                    }
                                    id = (IID) idClass.getConstructor(String.class).newInstance(m.group(1));
                                    //id = m.group(1);
                                    /*
                                    //sb.append(line);
                                    while ( !line.matches("^</photo>.*")) {
                                            line = br.readLine();
                                    }
                                    */
                                    //sb.append(line);
                                    continue;
                            }

                            
                            if ( line.contains("<lire")) {
                                StringBuffer sb = new StringBuffer ();

                                //System.out.print(".");
                                sb.append(line);
                                while ( !line.contains("</lire>")) {
                                        line = br.readLine();
                                        sb.append(line);
                                }
                                try {
                                        //System.out.println(sb);

                                        // Internally will insert only used features
                                        fList.addAll(LireMPEG7VDs.getFeaturesCollection(sb, fCC).getFeatures());

                                } catch (XMLStreamException e) {
                                        e.printStackTrace();
                                        System.err.println("Error parsing:" + sb.toString() + "\n" + sb);
                                        throw e;
                                }
                            }
                            
                        }
                    }
	    }
		
	    if ( id == null && fList.size() == 0 ) {
	    	return null;
	    }
	    
	    FeaturesCollectorArr fc = new FeaturesCollectorArr(fList, id, null);
	    
	    if ( fCC != null && !fCC.areIn(fc) ) {
	    	//System.err.println("Error parsing: not all features were found.");
	    	System.err.println("Error: " + fCC.missingIn(fc) + " were not found");
	    }
	    
	    
	    return fc;
		
	}
	
	
	
	public static void setColorSIFTscaleFilter(double scale) {
		maxColorSIFTscale = scale;		
	}
	
	public static void setSIFTscaleFilter(double scale) {
		maxSIFTscale = scale;		
	}
	
	public static void setSURFscaleFilter(double scale) {
		maxSURFscale = scale;		
	}

	public void open(BufferedReader br) {
		this.br = br;		
	}
	public static final int getMaxSIFTn() {
		return maxSIFTn;
	}

	public static final void setMaxSIFTn(int maxSIFTn) {
		CoPhIRv2Reader.maxSIFTn = maxSIFTn;
	}

	public static final int getMaxSURFn() {
		return maxSURFn;
	}

	public static final void setMaxSURFn(int maxSURFn) {
		CoPhIRv2Reader.maxSURFn = maxSURFn;
	}

	public static final int getMaxColorSIFTn() {
		return maxColorSIFTn;
	}

	public static final void setMaxColorSIFTn(int maxColorSIFTn) {
		CoPhIRv2Reader.maxColorSIFTn = maxColorSIFTn;
	}
	
	

	
}
