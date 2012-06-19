package net.ion.isearcher.crawler.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import net.ion.framework.rope.Rope;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides common file data handling methods.
 *
 * @author bleujin
 * @version $Revision: 1.3 $
 */
public final class FileUtil {

    private static final transient Log LOG = LogFactory.getLog(FileUtil.class);
 
    private FileUtil() {
    }

    public static boolean save(File file, CharSequence data, String charSet, long timestamp) {
        LOG.info("Saving data (" + data.length() + " bytes) to file=" + file + ", " + charSet);

        createDirectoryForFile(file);

        try {
            OutputStreamWriter writer;
            if (Charset.isSupported(charSet)) {
                writer = new OutputStreamWriter(new FileOutputStream(file), charSet);
            } else {
                writer = new OutputStreamWriter(new FileOutputStream(file));
            }
            try {
            	if (data instanceof Rope){
            		((Rope) data).write(writer) ;
            	} else {
            		writer.write(data.toString());
            	}
            } finally {
                writer.close();
            }
            if (timestamp > 0) {
                if (!file.setLastModified(timestamp)) {
                    LOG.info("Can't set last modified timestamp by file=" + file);
                }
            }
        } catch (IOException e) {
            LOG.error("Failed to write file=" + file, e);
            return false;
        }

        return true;
    }

    /** lock for workaround the Sun Bug ID 4742723. */ 
    private static final Object MKDIRS_LOCK = new Object();

    public static void createDirectoryForFile(File file) {
        // check if file exists before testing if parent exists
        if (!file.exists()) {
            // check if the directory structure required exists and create it if it doesn't
            final File filepath = new File(file.getParent());
            if (!filepath.exists()) {
                synchronized (MKDIRS_LOCK) {
                    if (!filepath.mkdirs()) {
                        LOG.warn("Can't create directory=" + filepath);
                    }
                }
            }
        }
    }

}
