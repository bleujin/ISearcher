package net.ion.nsearcher.index;



public interface IndexJob<T> {
	
	public final static IndexJob<Void> SAMPLE = new IndexJob<Void>() {
		@Override
		public Void handle(IndexSession isession) throws Exception {
			isession.newDocument("bleujin").keyword("name", "bleujin").number("age", 20).text("intro", "Hello Bleujin") ;
			return null;
		}
	};
	
	public T handle(IndexSession isession) throws Exception ;
}
