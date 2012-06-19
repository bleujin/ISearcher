package net.ion.isearcher.indexer.channel;

import net.ion.framework.db.DBController;
import net.ion.framework.db.IDBController;
import net.ion.framework.db.manager.DBManager;
import net.ion.framework.db.manager.OracleDBManager;
import net.ion.framework.db.procedure.IUserCommand;
import net.ion.isearcher.ISTestCase;
import net.ion.isearcher.crawler.listener.CountListener;
import net.ion.isearcher.events.DataRowEvent;
import net.ion.isearcher.events.ICollectorEvent;
import net.ion.isearcher.events.KeyValues;
import net.ion.isearcher.indexer.collect.DatabaseCollector;
import net.ion.isearcher.indexer.collect.FileCollector;
import net.ion.isearcher.indexer.report.DefaultReporter;

import org.apache.lucene.document.Document;

public class TestCollectorExecutor extends ISTestCase{

	private IDBController dc ;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		// DBManager dbm = new OracleDBManager("jdbc:oracle:thin:@dev-sql.i-on.net:1521:devSql", "bleu", "redf") ;
		DBManager dbm = new OracleDBManager("jdbc:oracle:thin:@dev-oracle.i-on.net:1521:dev10g", "bleu", "redf");
		dc = new DBController(dbm) ;
		dc.initSelf() ;
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		dc.destroySelf() ;
	}
	
	public void testFile() throws Exception {
		FileCollector col = new FileCollector(getTestDirFile(), true) ;
		DefaultReporter reportor = new DefaultReporter(false) ;
		col.addListener(reportor) ;
		
		col.collect() ;

	}
	
	
	
	public void testDatabase() throws Exception {
		
		IUserCommand cmd = dc.createUserCommand("select * from emp_sample where rownum <= 10") ;
		DatabaseCollector col = new DatabaseCollector(cmd, "empNo") ;

		CountListener counter = new CountListener();
		col.addListener(counter) ;
		col.collect() ;
		
		assertEquals(10, counter.getCount()) ;

		dc.destroySelf() ;
	}
	
	public void testJoindQuery() throws Exception {

		
		IUserCommand cmd = dc.createUserCommand("select x1.deptNo, dname, loc, empNo, ename, job, sal from dept_sample x1, emp_sample x2 where x1.deptNo = x2.deptNo order by x1.deptNo") ;
		DatabaseCollector col = new DatabaseCollector(cmd, new String[]{"deptNo"}, new String[]{"deptNo", "dname", "loc"}) ;

		CountListener counter = new CountListener();
		col.addListener(counter) ;
		col.collect() ;

		dc.destroySelf() ;
		assertEquals(3, counter.getCount()) ;
	}

	
	public void testJoindQueryEvent() throws Exception {

		IUserCommand cmd = dc.createUserCommand("select x1.DeptNo, dname, LOC, empNo, ename, job, sal, 'abc' Tag from dept_sample x1, emp_sample x2 where x1.deptNo = x2.deptNo order by x1.deptNo") ;
		DatabaseCollector col = new DatabaseCollector(cmd, new String[]{"deptNo"}, new String[]{"deptNo", "dname", "loc"}) ;

		DebugQueueListener queue = new DebugQueueListener() ;
		col.addListener(queue) ;
		col.collect() ;

		dc.destroySelf() ;
		
		ICollectorEvent[] events = queue.getCollectorEvents() ;
		assertEquals(3, events.length) ;
		for (ICollectorEvent event : events) {
			KeyValues dk = ((DataRowEvent)event).getKeyValues() ;
			assertEquals(8, dk.getKeySet().size()) ;
			assertEquals("abc", dk.get("tag")) ;
		}
	}

	
	
	
}
