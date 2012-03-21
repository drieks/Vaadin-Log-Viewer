package org.vaadin.addons.logview.api;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vaadin.addons.logview.api.RegexParser.MatchResult;

public class LogEntry {
	private int line;
	private final Map<String, MatchResult> fields;
	private final LinkedList<String> lines = new LinkedList<String>();

	public LogEntry() {
		this(new LinkedHashMap<String, MatchResult>());
	}

	public LogEntry(Map<String, MatchResult> matches) {
		this.fields = matches;
	}

	public void put(String field, MatchResult value) {
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

	public MatchResult get(String key) {
		return fields.get(key);
	}

	public String getString(String key) {
		MatchResult r = fields.get(key);
		if(r == null) {
			return null;
		}
		return (String)r.value;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}
}
