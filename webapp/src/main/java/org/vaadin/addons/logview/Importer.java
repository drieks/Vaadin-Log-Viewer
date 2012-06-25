package org.vaadin.addons.logview;

import com.github.logview.importer.Importers;
import com.github.logview.importer.MultiThreadLoader;
import com.github.logview.matcher.PatternMatcher;
import com.github.logview.util.Util;
import com.github.logview.value.api.ValueFactory;

public class Importer {
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		String mach = Util.loadString(Importers.class, "../settings/format.properties", "default");
		PatternMatcher matcher = new PatternMatcher(ValueFactory.createDefault(), mach, false);
		MultiThreadLoader loader = new MultiThreadLoader(matcher, new Bz2LogEntryTask());
		Importers.loadPath(loader, loader, ".");
		System.err.printf("%d ms\n", System.currentTimeMillis() - start);
	}
}
