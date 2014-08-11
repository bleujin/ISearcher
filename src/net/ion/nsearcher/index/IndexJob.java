package net.ion.nsearcher.index;

import net.ion.nsearcher.common.WriteDocument;



public interface IndexJob<T> {
	
	public final static IndexJob<Void> SAMPLE = new IndexJob<Void>() {
		@Override
		public Void handle(IndexSession isession) throws Exception {
			WriteDocument wdoc = isession.newDocument("bleujin").keyword("name", "bleujin").number("age", 20).text("intro", "Hello Bleujin") ;
			isession.updateDocument(wdoc) ;
			return null;
		}
	};
	
	public T handle(IndexSession isession) throws Exception ;
}
