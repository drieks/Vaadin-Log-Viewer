package org.vaadin.addons.logview;

import java.util.List;

import com.github.logview.api.LogEntry;
import com.github.logview.importer.Importers;
import com.github.logview.importer.LogEntryTask;
import com.github.logview.importer.MultiThreadLoader;
import com.github.logview.matcher.PatternMatcher;
import com.github.logview.util.Util;
import com.github.logview.value.api.ValueFactory;
import com.google.common.collect.Lists;

public class Loader {
	public final static List<LogEntry> entrys = loadEntrys();

	private static List<LogEntry> loadEntrys() {
		long start = System.currentTimeMillis();
		try {
			String mach = Util.loadString(Importers.class, "../settings/format.properties", "default");
			PatternMatcher matcher = new PatternMatcher(ValueFactory.createDefault(), mach, false);
			LogEntryTask entrys = new LogEntryTask();
			MultiThreadLoader loader = new MultiThreadLoader(matcher, entrys);
			Importers.loadPath(loader, loader);
			return entrys.getEntrys();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.err.printf("%d ms\n", System.currentTimeMillis() - start);
		}
		return Lists.newLinkedList();
	}
}
