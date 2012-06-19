package net.ion.isearcher.lucene.function;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.RandomUtil;

import org.apache.http.impl.cookie.DateUtils;

public class FunctionTest extends TestCase {

	public void testRandomCalendar() throws Exception {
		for (int i = 0; i < 10; i++) {
			Debug.debug(DateUtils.formatDate(RandomUtil.nextCalendar(10).getTime(), "yyyyMMdd-HH24mmss")) ;
		}
	}


}
