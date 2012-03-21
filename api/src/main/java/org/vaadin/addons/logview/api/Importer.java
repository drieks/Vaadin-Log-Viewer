package org.vaadin.addons.logview.api;

import java.io.File;
import java.util.Map;
import java.util.Scanner;
import java.util.prefs.Preferences;

import org.vaadin.addons.logview.api.RegexParser.MatchResult;

public class Importer {
	public static void load(String prefix, Loader loader) throws Exception {
		Preferences prefs = Preferences.userNodeForPackage(Importer.class);
		String path = prefs.get("path", null);
		if(path == null) {
			throw new RuntimeException("please set path pref!");
		}
		load(prefix, path, loader);
	}

	public static void load(String prefix, String path, Loader loader) throws Exception {
		Importer.loadPath(new RegexParser(prefix, Importer.class, "../settings/format.properties"), path, loader);
	}

	public static void loadPath(RegexParser parser, String path, Loader loader) throws Exception {
		File dir = new File(path);
		for(File file : dir.listFiles()) {
			if(!file.isFile()) {
				continue;
			}
			LogFile logfile = loader.load(file);
			loadLog(parser, file, loader, logfile);
			loader.save(logfile);
		}
	}

	public static void loadLog(RegexParser parser, File file, Appender appender, LogFile logfile) throws Exception {
		LogEntry entry = null;
		Scanner scanner = AutomaticScanner.create(file);

		try {
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				Map<String, MatchResult> matches = parser.matchLine(line);
				if(matches != null) {
					if(entry != null) {
						appender.append(logfile, entry);
					}
					entry = new LogEntry(matches);
				} else if(entry != null) {
					entry.add(line);
				}
			}
			if(entry != null) {
				appender.append(logfile, entry);
			}
		} finally {
			scanner.close();
		}
	}
}
