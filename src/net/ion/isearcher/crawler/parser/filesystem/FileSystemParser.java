package net.ion.isearcher.crawler.parser.filesystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.ion.isearcher.crawler.link.Link;
import net.ion.isearcher.crawler.parser.IParser;
import net.ion.isearcher.crawler.parser.PageData;
import net.ion.isearcher.crawler.util.ILinkExtractor;
import net.ion.isearcher.crawler.util.LinksUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileSystemParser implements IParser {

    private static final transient Log LOG = LogFactory.getLog(FileSystemParser.class);

    private boolean warnOfMissingMappings;

    private final Map mapping = new HashMap();

    // set the default link extractor of LinksUtil.
    private ILinkExtractor linkExtractor = LinksUtil.DEFAULT_LINK_EXTRACTOR;

    public FileSystemParser() {
        super();
    }

    public FileSystemParser(String domain, File file) {
        this();
        addMapping(domain, file);
    }

    public void addMapping(String domain, File file) {
        // remove all / from end of the domain
        while (domain.endsWith("/")) {
            domain = domain.substring(0, domain.length() - 1);
        }
        mapping.put(domain, file);
    }

    public boolean isWarnOfMissingMappings() {
        return warnOfMissingMappings;
    }

    public void setWarnOfMissingMappings(boolean warnOfMissingMappings) {
        this.warnOfMissingMappings = warnOfMissingMappings;
    }

    // FIXME performance extract the server of the uri and use domain
    private String getDomain(String uri) {
        Iterator itr = mapping.keySet().iterator();
        while (itr.hasNext()) {
            String domain = (String) itr.next();
            if (uri.startsWith(domain)) {
                return domain;
            }
        }
        return null;
    }
    public PageData load(Link link) {
        final String uri = link.getURI();

        String domain = getDomain(uri);
        if (domain != null) {

            File file = new File((File) mapping.get(domain), uri.substring(domain.length()));
            if (file.exists()) {
                LOG.debug("Loading file=" + file);

                StringBuffer content = new StringBuffer(8192);
                char[] buffer = new char[8192];
                int read = 0;

                BufferedReader in = null;
                try {
                    in = new BufferedReader(new FileReader(file));
                    while ((read = in.read(buffer)) > 0) {
                        content.append(buffer, 0, read);
                    }
                } catch (Exception e) {
                    LOG.warn("Can't read file=" + file, e);
                    return new PageDataFileSystem(link, PageData.ERROR);
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (Exception e) {
                            LOG.warn("Can't close input stream of file, link=" + link.getURI() + ", file=" + file);
                        }
                    }
                }
                return new PageDataFileSystem(link, content.toString());
            } else {
                LOG.warn("File doesn't exists, link=" + link.getURI() + ", file=" + file.getAbsolutePath());
            }
        } else {
            if (warnOfMissingMappings) {
                LOG.warn("Missing domain file path mapping for " + uri);
            } else {
                LOG.info("Missing domain file path mapping for " + uri);
            }
        }

        return new PageDataFileSystem(link, PageData.ERROR);
    }

	public void shutdown() {
		
	}

    public Collection<Link> parse(PageData pageData) {
        if (!(pageData instanceof PageDataFileSystem)) {
            LOG.warn("Type mismatch in " + this.getClass().getName());
            return Collections.EMPTY_LIST;
        }
        return linkExtractor.retrieveURIs(pageData.getLink(), (CharSequence) pageData.getData());
    }

    public ILinkExtractor getLinkExtractor() {
        return linkExtractor;
    }

    public void setLinkExtractor(ILinkExtractor linkExtractor) {
        if (linkExtractor == null) {
            throw new IllegalArgumentException("Parameter linkExtractor is null.");
        }
        this.linkExtractor = linkExtractor;
    }

    private static class PageDataFileSystem extends PageData {
        private CharSequence data;
        public PageDataFileSystem(Link link, String data) {
            super(link, PageData.OK);
            this.data = data;
        }

        public PageDataFileSystem(Link link, int status) {
            super(link, status);
        }
        public Object getData() {
            return data;
        }
        public void setData(Object data) {
            this.data = (CharSequence) data;
        }

    }

    
}
