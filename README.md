ISearcher
=========

Search Framework

public class TestFirstAPI extends TestCase {

	private Central cen ;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.cen = CentralConfig.newRam().build() ;
	}
	
	@Override
	protected void tearDown() throws Exception {
		cen.close() ;
		super.tearDown();
	}
	
	public void testCreateSearcher() throws Exception {
		Searcher searcher = cen.newSearcher();
		assertEquals(0, searcher.search("").totalCount()) ; 
	}

	public void testCreateIndexer() throws Exception {
		Indexer indexer = cen.newIndexer() ;
		indexer.index(new StandardAnalyzer(SearchConstant.LuceneVersion), new IndexJob<Void>(){
			public Void handle(IndexSession session) throws IOException {
				for (int i : ListUtil.rangeNum(10)) {
					MyDocument doc = MyDocument.newDocument(new ObjectId().toString()).add(JsonObject.create().put("name", "bleujin").put("age", i));
					session.insertDocument(doc) ;
				}
				return null;
			}
		}) ;
		
		Searcher searcher = cen.newSearcher();
		assertEquals(10, searcher.search("").totalCount()) ;
	}
	
	
}
