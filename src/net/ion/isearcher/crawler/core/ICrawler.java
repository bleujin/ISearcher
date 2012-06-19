package net.ion.isearcher.crawler.core;

import net.ion.isearcher.crawler.filter.ILinkFilter;
import net.ion.isearcher.crawler.model.ICrawlerModel;
import net.ion.isearcher.crawler.parser.IParser;
import net.ion.isearcher.events.ILoadingEventListener;
import net.ion.isearcher.events.IParserEventListener;


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
