package net.ion.bleujin;

import junit.framework.TestCase;

public class TestParameter extends TestCase {

	public void testCall() throws Exception {
		Typetester t = new Typetester();
		Object v = new Integer(3);

		t.printType(v);
	}
}

class Typetester {

	void printType(int x) {
		System.out.println(x + " is an int");
	}

	void printType(Object x) {
		System.out.println(x + " is an object");
	}

	void printType(String x) {
		System.out.println(x + " is an string");
	}

	void printType(Integer x) {
		System.out.println(x + " is an integer");
	}
}