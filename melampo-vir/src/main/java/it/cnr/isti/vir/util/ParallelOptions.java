package it.cnr.isti.vir.util;

public class ParallelOptions {
	public static int nThreads = 6;;
	
	public static final void set( java.util.Properties properties) {
		String coreStr  = properties.getProperty("core");
		if ( coreStr != null ) {
			nThreads =  Integer.parseInt(coreStr);
			System.out.println("Using " + nThreads + " nThreads");
		}
	}
}
