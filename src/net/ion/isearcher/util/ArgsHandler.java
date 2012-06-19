package net.ion.isearcher.util;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class ArgsHandler {

	private Map map;
	private Set keys;
	private boolean validate;

	public ArgsHandler(String[] args) {
		this(args, null, false);
	}

	public ArgsHandler(String[] args, String[] knownKeys) {
		this(args, knownKeys, true);
	}

	public ArgsHandler(String[] args, String[] knownKeys, boolean validate) {
		String arg;
		int i, index;

		map = new HashMap();
		setupKnownKeys(knownKeys);
		this.validate = validate;
		for (i = 0; i < args.length; i++) {
			arg = args[i];
			if (!arg.startsWith("-"))
				System.err.println("Warning: unknown option " + arg + " ignored");
			index = arg.indexOf("=");
			if (index == -1)
				collectBooleanFlags(arg.substring(1));
			else
				setKeyValueArg(arg.substring(1, index), arg.substring(index + 1));
		}
	}

	protected void setupKnownKeys(String[] knownKeys) {
		int i;

		if (knownKeys == null)
			return;
		keys = new HashSet();
		for (i = 0; i < knownKeys.length; i++)
			keys.add(knownKeys[i]);
	}

	protected boolean checkKey(String key) {
		if (keys == null)
			return true;
		else
			return keys.contains(key);
	}

	protected void collectBooleanFlags(String arg) {
		String key;
		int i;

		for (i = 0; i < arg.length(); i++) {
			key = arg.substring(i, i + 1);
			if (checkKey(key))
				map.put(key, Boolean.TRUE);
			else
				System.err.println("Warning: collectBooleanFlags: Unknown option " + key + " ignored");
		}
	}

	protected void setKeyValueArg(String key, String value) {
		if (checkKey(key))
			map.put(key, value);
		else
			System.err.println("Warning: setKeyValueArg: Unknown option " + key + " ignored");
	}

	public boolean contains(String key) {
		if (!checkKey(key) && validate)
			System.err.println("Internal error: Option " + key + " is unknown!");
		return map.containsKey(key);
	}

	public String getStringValue(String key) {
		if (!checkKey(key) && validate)
			System.err.println("Internal error: Option " + key + " is unknown!");
		return (String) map.get(key);
	}

	public String getStringValue(String key, String defaultValue) {
		if (!checkKey(key) && validate)
			System.err.println("Internal error: Option " + key + " is unknown!");
		if (!contains(key))
			return defaultValue;
		else
			return (String) map.get(key);
	}

	public int getIntValue(String key) {
		if (!checkKey(key) && validate)
			System.err.println("Internal error: Option " + key + " is unknown!");
		return Integer.parseInt(getStringValue(key));
	}

	public int getIntValue(String key, int defaultValue) {
		if (!checkKey(key) && validate)
			System.err.println("Internal error: Option " + key + " is unknown!");
		if (!contains(key))
			return defaultValue;
		else
			return getIntValue(key);
	}

	public long getLongValue(String key) {
		if (!checkKey(key) && validate)
			System.err.println("Internal error: Option " + key + " is unknown!");
		return Long.parseLong(getStringValue(key));
	}

	public long getLongValue(String key, long defaultValue) {
		if (!checkKey(key) && validate)
			System.err.println("Internal error: Option " + key + " is unknown!");
		if (!contains(key))
			return defaultValue;
		else
			return getLongValue(key);
	}

	public void dump(PrintStream stream) {
		Iterator it;
		Object key;

		it = map.keySet().iterator();
		while (it.hasNext()) {
			key = it.next();
			stream.println("key=" + key + ",value=" + map.get(key) + " (" + map.get(key).getClass().getName() + ")");
		}
	}

}