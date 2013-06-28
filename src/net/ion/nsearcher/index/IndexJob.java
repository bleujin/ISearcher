package net.ion.nsearcher.index;



public interface IndexJob<T> {
	public T handle(IndexSession isession) throws Exception ;
}
