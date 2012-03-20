package org.vaadin.addons.logview.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Scanner;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

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
		InputStream format = Importer.class.getResourceAsStream("../settings/format.properties");
		Properties props = new Properties();
		props.load(format);
		Importer.loadPath(prefix, path, props, loader);
	}

	public static void loadPath(String prefix, String path, Properties props, Loader loader) throws Exception {
		File dir = new File(path);
		for(File file : dir.listFiles()) {
			if(!file.isFile()) {
				continue;
			}
			LogFile logfile = loader.load(file);
			loadLog(prefix, file, props, loader, logfile);
			loader.save(logfile);
		}
	}

	public static void loadLog(String prefix, File file, Properties props, Appender appender, LogFile logfile)
			throws Exception {
		LogEntry entry = null;
		Pattern pattern = Pattern.compile((String)props.get(prefix));
		Scanner scanner;
		if(file.getName().endsWith(".gz")) {
			scanner = new Scanner(new GZIPInputStream(new FileInputStream(file)));
		} else {
			scanner = new Scanner(new FileReader(file));
		}
		int max;
		for(max = 0;; max++) {
			if(props.getProperty(prefix + "." + max) == null) {
				break;
			}
		}

		String[] field = new String[max];
		String[] type = new String[max];
		SimpleDateFormat[] sdf = new SimpleDateFormat[max];
		for(int i = 0; i < max; i++) {
			field[i] = props.getProperty(prefix + "." + i);
			type[i] = props.getProperty(prefix + "." + i + ".type");
			if("date".equals(type[i])) {
				String format = props.getProperty(prefix + "." + i + ".format");
				sdf[i] = new SimpleDateFormat(format);
			}
		}
		try {
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				Matcher matcher = pattern.matcher(line);
				if(matcher.find()) {
					if(entry != null) {
						appender.append(logfile, entry);
					}
					entry = new LogEntry();
					if(matcher.groupCount() != max) {
						throw new RuntimeException(String.format("wrong entry count on line (is:%d should:%d):\n%s",
							matcher.groupCount(), max, line));
					}
					for(int i = 0; i < max; i++) {
						String group = matcher.group(i + 1);
						if(type[i] == null) {
						} else if("date".equals(type[i])) {
							entry.put(field[i], sdf[i].parse(group));
							continue;
						} else {
							throw new RuntimeException("wrong type " + type[i]);
						}
						entry.put(field[i], group);
					}
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
