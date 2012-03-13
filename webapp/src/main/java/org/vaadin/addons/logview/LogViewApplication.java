package org.vaadin.addons.logview;

import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedList;

import org.vaadin.addons.logview.data.Appender;
import org.vaadin.addons.logview.data.LogEntry;
import org.vaadin.addons.logview.importer.Importer;

import com.vaadin.Application;
import com.vaadin.ui.Window;

public class LogViewApplication extends Application {
	private final static LinkedList<LogEntry> entrys = new LinkedList<LogEntry>();

	static {
		try {
			Importer.load("default", new Appender() {
				@Override
				public void append(LogEntry entry) {
					entrys.add(entry);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init() {
		setMainWindow(new Window("LogViewer", new LogViewComponent()));
	}
}
