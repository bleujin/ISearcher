package net.ion.nsearcher.search;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.map.LRUMap;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;

public class CachedFilter {

	private Map<Filter, Filter> filters = new LRUMap(128);

	public Filter getFilter(Filter filter) {
		synchronized (filters) {
			if (filters.containsKey(filter)) {
				return filters.get(filter);
			} else {
				CachingWrapperFilter value = null;
				if (filter instanceof CachingWrapperFilter) {
					value = (CachingWrapperFilter) filter;
				} else {
					value = new CachingWrapperFilter(filter);
				}
				filters.put(filter, value);
				return value;
			}
		}
	}

	public Filter getKeyFilter(Filter find) {
		for (Entry<Filter, Filter> entry : filters.entrySet()) {
			if (find.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		throw new IllegalArgumentException("NOT FOUND : " + find);
	}

	protected boolean existFilter(Filter filter) {
		return filters.containsKey(filter);
	}

	public void clear() {
		filters.clear();
	}
}
