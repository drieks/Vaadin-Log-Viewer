package org.vaadin.addons.logview.api;

public interface Appender {
	void append(LogFile logfile, LogEntry entry) throws Exception;

	void save(LogFile logfile) throws Exception;
}
