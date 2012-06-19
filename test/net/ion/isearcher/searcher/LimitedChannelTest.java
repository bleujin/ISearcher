package net.ion.isearcher.searcher;

import junit.framework.TestCase;
import net.ion.isearcher.searcher.processor.LimitedChannel;

public class LimitedChannelTest extends TestCase{

	private LimitedChannel<Object> channels = null;
	int MaxSize = 100;
	public void setUp() throws Exception {
		channels = new LimitedChannel<Object>(MaxSize) ;
	}
	public void testMax() throws Exception {
		
		for (int i = 0; i < 200; i++) {
			channels.add(new Integer(i)) ;
		}
		
		assertEquals(100, channels.size()) ;
		assertEquals(100, channels.poll()) ;
		assertEquals(99, channels.size()) ;
		
	}
}
