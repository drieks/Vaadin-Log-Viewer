package org.vaadin.addons.logview.table;

import java.io.Serializable;
import java.util.Date;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.github.logview.api.LogEntry;

public class DetailLogEntry implements Serializable {
	public final static String ID = "id";
	public final static String DATE = "date";
	public final static String LEVEL = "level";
	public final static String NDC = "NDC";
	public final static String CLASS = "className";
	public final static String MESSAGE = "message";

	private transient final DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm:ss.SSS");
	private transient final LogEntry entry;

	public DetailLogEntry(LogEntry entry) {
		this.entry = entry;
	}

	public long getId() {
		return entry.getId();
	}

	public String getDate() {
		return dtf.print(((Date)entry.getLine().getValue(0)).getTime());
	}

	public String getLevel() {
		return entry.getLine().toString(1).trim();
	}

	public String getNDC() {
		return entry.getLine().toString(2).trim();
	}

	public String getClassName() {
		return entry.getLine().toString(3).trim();
		/*
		return "" + (getId() / TableBlockCache.BLOCK_SIZE) + "/" + (getId() % TableBlockCache.BLOCK_SIZE) + "="
		+ getId() + entry.getLine().toString(3).trim();
		*/
	}

	public String getMessage() {
		return entry.getLine().toString(4).trim();
	}
}
