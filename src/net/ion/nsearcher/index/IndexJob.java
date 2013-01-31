package net.ion.nsearcher.index;



public interface IndexJob<T> {
	public T handle(IndexSession session) throws Exception ;
}
