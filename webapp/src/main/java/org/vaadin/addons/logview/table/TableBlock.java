package org.vaadin.addons.logview.table;

import java.util.ArrayList;
import java.util.List;

import com.github.logview.api.DetailLogEntry;
import com.google.common.collect.Lists;

public class TableBlock {
	private final ArrayList<DetailLogEntry> entrys;

	public TableBlock(List<DetailLogEntry> entrys) {
		if(entrys.size() == 0) {
			throw new RuntimeException("empty table block!");
		}
		this.entrys = Lists.newArrayList(entrys);
	}

	public DetailLogEntry getEntry(int i) {
		// System.err.println(i + " " + (i % TableBlockCache.BLOCK_SIZE) + " " + entrys.size());
		return entrys.get((i % TableBlockManager.BLOCK_SIZE) % entrys.size());
	}

	public int size() {
		return entrys.size();
	}
}
