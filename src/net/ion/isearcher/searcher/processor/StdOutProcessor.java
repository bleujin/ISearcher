package net.ion.isearcher.searcher.processor;


// test 
public class StdOutProcessor implements PostProcessor{

	private SearchTask task ;
	
	public void postNotify(SearchTask task) {
		this.task = task ;
	}
	
	public int getTotalCount() {
		return task.getResult().getTotalCount() ;
	}
}
