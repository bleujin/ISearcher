package net.ion.crawler.core;

import net.ion.crawler.filter.ILinkFilter;
import net.ion.crawler.model.ICrawlerModel;
import net.ion.crawler.parser.IParser;
import net.ion.nsearcher.index.event.ILoadingEventListener;
import net.ion.nsearcher.index.event.IParserEventListener;


public interface ICrawler {

    IParser getParser();

    void setParser(IParser parser);

    ICrawlerModel getModel();

    void setModel(ICrawlerModel model);

    ILinkFilter getLinkFilter();

    void setLinkFilter(ILinkFilter linkFilter);

    void addLoadingListener(ILoadingEventListener l);

    void removeLoadingListener(ILoadingEventListener l);

    void addParserListener(IParserEventListener l);

    void removeParserListener(IParserEventListener l);
    
    void collect() ;

	String getCollectName();

}
