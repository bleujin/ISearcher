package net.ion.isearcher.rest.formater;

import junit.framework.TestCase;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;

public class JSONFormaterTest extends TestCase{
	
	public void testTransform() throws Exception {

		JsonObject root = new JsonObject() ;
		
		JsonObject obj1 = new JsonObject() ;
		obj1.accumulate("abc", "value1") ;

		JsonObject obj2 = new JsonObject() ;
		obj2.accumulate("abc", "value2") ;

		JsonObject obj3 = new JsonObject() ;
		obj3.accumulate("abc", "value3") ;
		
		root.add("property", obj1) ;
		root.add("property", obj2) ;
		root.add("property", obj3) ;
		
		Debug.debug(root.toString()) ;
	}

}
