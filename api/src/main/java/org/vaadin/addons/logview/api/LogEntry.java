package org.vaadin.addons.logview.api;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class LogEntry {
	private int line;
	private final LinkedHashMap<String, Object> fields = new LinkedHashMap<String, Object>();
	private final LinkedList<String> lines = new LinkedList<String>();

	public void put(String field, Object value) {
		fields.put(field, value);
	}

	public void add(String line) {
		lines.add(line);
	}

	public Set<String> keySet() {
		return fields.keySet();
	}

	public List<String> lines() {
		return lines;
	}

	public Object get(String key) {
		return fields.get(key);
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}
}
