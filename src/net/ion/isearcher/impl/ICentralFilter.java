package net.ion.isearcher.impl;

import org.apache.lucene.search.Filter;

public abstract class ICentralFilter {

	public abstract Filter getFilter(Filter filter);

	public abstract Filter getKeyFilter(Filter find);

	protected abstract boolean existFilter(Filter filter) ;
	
	public abstract void clear() ;
}
