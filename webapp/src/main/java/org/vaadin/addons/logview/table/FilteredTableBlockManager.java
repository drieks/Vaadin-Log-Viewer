package org.vaadin.addons.logview.table;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.github.logview.api.DetailLogEntry;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class FilteredTableBlockManager {
	private final TableBlockManager manager;
	private final PreparedStatement size;
	private final PreparedStatement index;
	private final Set<String> levels;

	public FilteredTableBlockManager(TableBlockManager manager, Iterable<String> levels) {
		this.manager = manager;
		this.levels = Sets.newHashSet();
		StringBuilder sql = new StringBuilder();
		boolean first = false;
		for(String level : levels) {
			if(!first) {
				first = true;
			} else {
				sql.append(" + ");
			}
			String l = level.toLowerCase();
			this.levels.add(l);
			sql.append("level_");
			sql.append(l);
		}
		String l = sql.toString();
		size = manager.prepareStatement("SELECT SUM(" + l + ") AS sum FROM log");
		index = manager.prepareStatement("SELECT id, " + l + " AS sum FROM log WHERE (" + l + ") <> 0;");
	}

	private final LoadingCache<Integer, TableBlock> filteredBlockCache = CacheBuilder.newBuilder() //
			.maximumSize(1000) //
			.build(new CacheLoader<Integer, TableBlock>() {
				@Override
				public TableBlock load(Integer index) throws Exception {
					// System.err.printf("filter load block %d\n", index);
					return filter(manager.getRawBlock(index));
				}
			});

	public int size() {
		try {
			ResultSet rs;
			synchronized(size) {
				if(size.execute()) {
					rs = size.getResultSet();
				} else {
					throw new RuntimeException();
				}
			}
			rs.first();
			return rs.getInt(1);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public LinkedHashMap<Integer, Integer> getIndex() {
		try {
			ResultSet rs;
			synchronized(index) {
				if(index.execute()) {
					rs = index.getResultSet();
				} else {
					throw new RuntimeException();
				}
			}
			LinkedHashMap<Integer, Integer> ret = Maps.newLinkedHashMap();
			while(rs.next()) {
				int id = rs.getInt(1);
				int sum = rs.getInt(2);
				ret.put(id, sum);
			}
			return ret;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private TableBlock filter(TableBlock block) {
		List<DetailLogEntry> ret = Lists.newLinkedList();
		for(int i = 0; i < block.size(); i++) {
			DetailLogEntry entry = block.getEntry(i);
			if(filter(entry)) {
				ret.add(entry);
			}
		}
		return new TableBlock(ret);
	}

	private boolean filter(DetailLogEntry entry) {
		return levels.contains(entry.getLevel().toLowerCase());
	}

	public DetailLogEntry getEntry(Integer index) {
		TableBlock block = filteredBlockCache.getUnchecked(index / TableBlockManager.BLOCK_SIZE);
		return block.getEntry(index);
	}
}
