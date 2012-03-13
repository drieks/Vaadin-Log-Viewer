package org.vaadin.addons.logview.importer;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Properties;
import java.util.prefs.Preferences;

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
		Preferences prefs = Preferences.userNodeForPackage(Importer.class);
		String path = prefs.get("path", null);
		if(path == null) {
			throw new RuntimeException("please set path pref!");
		}
		InputStream format = Importer.class.getResourceAsStream("../settings/format.properties");
		Properties props = new Properties();
		props.load(format);
		Importer.loadPath("default", path, props, new ImporterDemo());
	}
}
