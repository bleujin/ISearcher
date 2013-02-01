package net.ion.nsearcher.config;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

public class SearchConfig {

	private final Version version;
	private final Analyzer queryAnalyzer ;
	private final String defaultSearchFieldName ;
	
	SearchConfig(Version version, Analyzer queryAnalyzer, String defaultSearchFieldName) {
		this.version = version ;
		this.queryAnalyzer = queryAnalyzer ;
		this.defaultSearchFieldName = defaultSearchFieldName ;
	}

	public Analyzer queryAnalyzer() {
		return queryAnalyzer;
	}

	public String defaultSearchFieldName() {
		return defaultSearchFieldName;
	}

	public Query parseQuery(Analyzer analyzer, String query) throws ParseException {
		QueryParser parser = new QueryParser(version, defaultSearchFieldName(), analyzer) ;
		return parser.parse(query) ;
	}

	
	
}
