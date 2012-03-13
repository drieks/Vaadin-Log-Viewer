package org.vaadin.addons.logview.importer;

import java.io.IOException;
import java.text.ParseException;

import org.vaadin.addons.logview.data.Appender;
import org.vaadin.addons.logview.data.LogEntry;

public class ImporterDemo implements Appender {
	@Override
	public void append(LogEntry entry) {
		for(String key : entry.keySet()) {
			System.err.printf("%s(%s) ", key, entry.get(key));
		}
		System.err.println();
		for(String line : entry.lines()) {
			System.err.printf(" * %s\n", line);
		}
	}

	public static void main(String[] args) throws IOException, ParseException {
		Importer.load("default", new ImporterDemo());
	}
}
