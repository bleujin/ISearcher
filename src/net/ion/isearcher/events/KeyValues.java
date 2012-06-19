package net.ion.isearcher.events;

import java.util.Map;
import java.util.Set;

import net.ion.framework.util.CaseInsensitiveHashMap;

public class KeyValues {

	private Map<String, Object> store = new CaseInsensitiveHashMap<Object>();
	public static KeyValues create() {
		return new KeyValues();
	}

	public void add(String key, Object value) {
		store.put(key, value) ;
	}

	public Set<String> getKeySet() {
		return store.keySet() ;
	}

	public Object get(String key) {
		return store.get(key);
	}
	
	public String getString(String key){
		return (get(key) == null) ? "" : get(key).toString() ;
	}

	
}
