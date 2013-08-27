package net.ion.nsearcher.common;

import net.ion.nsearcher.index.IndexSession;

public interface Applier<T> {
	public T apply(IndexSession isession) ;
}
