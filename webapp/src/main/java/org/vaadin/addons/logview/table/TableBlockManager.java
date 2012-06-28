package org.vaadin.addons.logview.table;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import org.apache.tools.bzip2.CBZip2InputStream;

import com.github.logview.api.DetailLogEntry;
import com.github.logview.api.LogEntry;
import com.github.logview.importer.Importers;
import com.github.logview.matcher.Match;
import com.github.logview.matcher.PatternMatcher;
import com.github.logview.util.Util;
import com.github.logview.value.api.ValueFactory;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.Lists;

public class TableBlockManager {
	public static final int BLOCK_SIZE = 2500;

	private Connection connection;

	private PreparedStatement get;

	public TableBlockManager() {
		open();
	}

	private final PatternMatcher matcher = getMatcher();

	private PatternMatcher getMatcher() {
		try {
			String mach = Util.loadString(Importers.class, "../settings/format.properties", "default");
			return new PatternMatcher(ValueFactory.createDefault(), mach, false);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static TableBlock parseTableBlock(PatternMatcher matcher, int id, byte[] bytes) {
		try {
			ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
			try {
				CBZip2InputStream zi = new CBZip2InputStream(bi);
				try {
					Scanner scanner = new Scanner(zi);
					int i = 0;
					try {
						final List<String> lines = Lists.newLinkedList();
						final List<DetailLogEntry> entrys = Lists.newLinkedList();
						Match last = null;
						while(scanner.hasNextLine()) {
							final String line = scanner.nextLine();
							Match m = matcher.match(line);
							if(m == null) {
								lines.add(line);
								continue;
							}
							if(last != null) {
								entrys.add(new DetailLogEntry(new LogEntry(id + i, last, lines)));
								lines.clear();
								i++;
							}
							last = m;
						}
						entrys.add(new DetailLogEntry(new LogEntry(i, last, lines)));
						return new TableBlock(entrys);
					} finally {
						scanner.close();
					}
				} finally {
					zi.close();
				}
			} finally {
				bi.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private final LoadingCache<Integer, TableBlock> rawBlockCache = CacheBuilder.newBuilder() //
			.maximumSize(1000) //
			.removalListener(new RemovalListener<Integer, TableBlock>() {
				@Override
				public void onRemoval(RemovalNotification<Integer, TableBlock> notification) {
					// System.err.printf("unload block %d\n", notification.getKey());
				}
			}) //
			.build(new CacheLoader<Integer, TableBlock>() {
				@Override
				public TableBlock load(Integer index) throws Exception {
					// System.err.printf("load block %d\n", index);
					try {
						ResultSet rs;
						synchronized(get) {
							get.setInt(1, index);
							if(get.execute()) {
								rs = get.getResultSet();
							} else {
								throw new RuntimeException();
							}
						}
						if(!rs.first()) {
							throw new RuntimeException("table block with id " + index + " not found!");
						}
						synchronized(matcher) {
							return parseTableBlock(matcher, index * BLOCK_SIZE, rs.getBytes(1));
						}
					} catch (SQLException e) {
						throw new RuntimeException(e);
					}
				}
			});

	public TableBlock getRawBlock(Integer id) {
		synchronized(rawBlockCache) {
			return rawBlockCache.getUnchecked(id);
		}
	}

	private void open() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tester?user=tester&password=tester");
			get = connection.prepareStatement("SELECT bz2 FROM log WHERE id = ?;");
			System.err.println("open ok!");
		} catch (ClassNotFoundException e) {
			System.err.println("open failed: class not found!");
			throw new RuntimeException(e);
		} catch (SQLException e) {
			System.err.println("open failed!");
			throw new RuntimeException(e);
		}
	}

	public FilteredTableBlockManager createFilter(Iterable<String> levels) {
		return new FilteredTableBlockManager(this, levels);
	}

	public PreparedStatement prepareStatement(String sql) {
		try {
			return connection.prepareStatement(sql);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
