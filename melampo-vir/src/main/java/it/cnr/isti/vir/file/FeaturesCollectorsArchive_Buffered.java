/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.cnr.isti.vir.file;

import gnu.trove.TLongArrayList;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.FeaturesCollectors;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.id.IDClasses;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.id.IID;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Fabrizio
 */
public class FeaturesCollectorsArchive_Buffered {
	public static final long fileID = 0x5a25287d3aL;

	public static final int version = 2;

	private final RandomAccessFile rndFile;
	private final File f;
	private final FeatureClassCollector featuresClasses;
	private final Constructor fcClassConstructor;

        private final Class idClass;

        private boolean changed = false;

        private final Class fcClass;

        public FeaturesCollectorsArchive_Buffered ( File file, FeatureClassCollector featuresClasses, Class idClass, Class fcClass  ) throws Exception {
		file.delete();
		rndFile = new RandomAccessFile( file, "rw" );
		this.f = file;
		this.featuresClasses = featuresClasses;
		this.idClass = idClass;
		this.fcClass = fcClass;
		this.fcClassConstructor = FeaturesCollectorsArchive.getFCConstructor( fcClass );

                FeaturesCollectorsArchive.writeIntro(rndFile, featuresClasses, idClass, fcClass );
	}

        public void add( IFeaturesCollector fc ) throws ArchiveException, IOException {

		if ( idClass != null ) {
			IID id = ((IHasID) fc).getID();
			if ( !idClass.isInstance(id)) {
				throw new ArchiveException(	"Objecct has a wrong ID class: "+
											idClass + " requeste, " +
											id.getClass() + " found." );
			}
		}

                if ( fcClass == null ) {
			FeaturesCollectors.writeData(rndFile, fc);
		} else {
			if ( fcClass.isInstance(fc) ) {
				fc.writeData(rndFile);
			} else {
				throw new  ArchiveException(
						"FeaturesCollector class inserted (" +
						fc.getClass() + ") diffear from expected (" +
						fcClass + ")");
			}
		}

		changed = true;
	}


	public void close() throws IOException {
		rndFile.close();
	}
}
