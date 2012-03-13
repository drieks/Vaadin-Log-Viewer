package org.vaadin.addons.logview.data;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class LogEntry {
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
}
