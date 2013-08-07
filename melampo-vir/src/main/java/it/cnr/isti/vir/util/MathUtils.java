package it.cnr.isti.vir.util;

public class MathUtils {
	
	public static final double[][] getSqrtAB(int n) {
		double[][] res = new double[n][n];
		for ( int i1=0; i1<n; i1++) {
			for ( int i2=0; i2<n; i2++) {
				res[i1][i2]=Math.sqrt(i1*i2);
			}
		}
		return res;
	}
}
