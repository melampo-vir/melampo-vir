package it.cnr.isti.vir.util;

import java.io.File;

public class FileNames {

	public static String getFileNameWithoutExtension(String fileName) {
        File tmpFile = new File(fileName);
        tmpFile.getName();
        int whereDot = tmpFile.getName().lastIndexOf('.');
        if (0 < whereDot && whereDot <= tmpFile.getName().length() - 2 ) {
            return tmpFile.getName().substring(0, whereDot);
            //extension = filename.substring(whereDot+1);
        }    
        return "";
    }
}
