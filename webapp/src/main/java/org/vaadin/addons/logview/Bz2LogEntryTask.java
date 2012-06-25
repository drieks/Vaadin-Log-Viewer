package org.vaadin.addons.logview;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.tools.bzip2.CBZip2OutputStream;
import org.vaadin.addons.logview.table.DetailLogEntry;

import com.github.logview.api.LogEntry;
import com.github.logview.importer.LogEntryTask;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

public class Bz2LogEntryTask extends LogEntryTask {
	private static final int MAX_SIZE = 2500;

	private final AtomicLong raw = new AtomicLong();
	private final AtomicLong bz2 = new AtomicLong();
	private final AtomicLong lines = new AtomicLong();

	private final AtomicInteger level_trace = new AtomicInteger();
	private final AtomicInteger level_debug = new AtomicInteger();
	private final AtomicInteger level_info = new AtomicInteger();
	private final AtomicInteger level_warn = new AtomicInteger();
	private final AtomicInteger level_error = new AtomicInteger();
	private final AtomicInteger level_fatal = new AtomicInteger();

	private final Map<String, AtomicInteger> levels = new ImmutableMap.Builder<String, AtomicInteger>()
			.put("trace", level_trace) //
			.put("debug", level_debug) //
			.put("info", level_info) //
			.put("warn", level_warn) //
			.put("error", level_error) //
			.put("fatal", level_fatal) //
			.build();

	private Connection connection;

	private PreparedStatement query;
	private int count = 0;

	@Override
	public void actionStart() {
		super.actionStart();
		open();
	}

	@Override
	protected void update() {
		if(entrys.size() == MAX_SIZE) {
			flushEntrys();
		}
	}

	@Override
	public void actionFinished() {
		flushEntrys();
		total();
		close();
	}

	private void total() {
		System.err.printf("Total: %d/%d MB %d kLines\n", raw.get() / 1024 / 1024, bz2.get() / 1024 / 1024,
			lines.get() / 1000);
	}

	private void open() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tester?user=tester&password=tester");
			query = connection
					.prepareStatement("INSERT INTO `log` (date_from, date_to, level_trace, level_debug, level_info,level_warn, level_error, level_fatal, bz2) VALUES (?,?,?,?,?,?,?,?,?);");
			System.err.println("open ok!");
		} catch (ClassNotFoundException e) {
			System.err.println("open failed: class not found!");
			throw new RuntimeException(e);
		} catch (SQLException e) {
			System.err.println("open failed!");
			throw new RuntimeException(e);
		}
	}

	private void close() {
		System.err.println("close");
		if(connection != null) {
			try {
				flushSql();
				connection.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void flushEntrys() {
		try {
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			CBZip2OutputStream os = new CBZip2OutputStream(bao);
			PrintWriter out = new PrintWriter(os);
			long s = 0;
			int l = 0;
			GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
			cal.clear();
			cal.set(2012, 1, 1);

			LoadingCache<String, LoadingCache<String, Set<Integer>>> tags = CacheBuilder.newBuilder().build(
				new CacheLoader<String, LoadingCache<String, Set<Integer>>>() {
					@Override
					public LoadingCache<String, Set<Integer>> load(final String tag) throws Exception {
						System.err.println("new tag " + tag);
						return CacheBuilder.newBuilder().build(new CacheLoader<String, Set<Integer>>() {
							@Override
							public Set<Integer> load(String name) throws Exception {
								System.err.println("new tag " + tag + ":" + name);
								return Sets.newLinkedHashSet();
							}
						});
					}
				});

			long from = ((Date)entrys.getFirst().getLine().getValue(0)).getTime() + cal.getTimeInMillis();
			long to = ((Date)entrys.getLast().getLine().getValue(0)).getTime() + cal.getTimeInMillis();
			resetLevels();
			for(LogEntry entry : entrys) {
				DetailLogEntry d = new DetailLogEntry(entry);
				String level = (String)entry.getLine().getValue(1);
				level = level.toLowerCase().trim();
				levels.get(level).incrementAndGet();
				String source = entry.getLine().getSource();
				out.println(source);
				s += source.getBytes().length;
				tags.getUnchecked("class").getUnchecked(d.getClassName()).add(l);
				tags.getUnchecked("level").getUnchecked(d.getLevel()).add(l);
				for(String ndc : d.getNDC().split(" ")) {
					ndc = ndc.trim();
					if(ndc.length() != 0) {
						tags.getUnchecked("ndc").getUnchecked(ndc).add(l);
					}
				}
				l++;
				System.err.println(entry.getLine());
				for(String line : entry.getLines()) {
					l++;
					out.println(line);
					s += line.getBytes().length;
				}
			}
			for(Entry<String, LoadingCache<String, Set<Integer>>> tag : tags.asMap().entrySet()) {
				for(Entry<String, Set<Integer>> name : tag.getValue().asMap().entrySet()) {
					System.err.printf("%s:%s %d\n", tag.getKey(), name.getKey(), name.getValue().size());
				}
			}
			System.exit(0);
			out.close();
			os.close();
			byte[] data = bao.toByteArray();
			addSql(from, to, new ByteArrayInputStream(data));
			raw.addAndGet(s);
			bz2.addAndGet(data.length);
			lines.addAndGet(l);
			total();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		entrys.clear();
	}

	private void resetLevels() {
		for(Entry<String, AtomicInteger> entry : levels.entrySet()) {
			entry.getValue().set(0);
		}
	}

	private void addSql(long from, long to, InputStream blob) throws SQLException {
		if(true) {
			return;
		}
		query.setTimestamp(1, new Timestamp(from));
		query.setTimestamp(2, new Timestamp(to));
		query.setInt(3, level_trace.get());
		query.setInt(4, level_debug.get());
		query.setInt(5, level_info.get());
		query.setInt(6, level_warn.get());
		query.setInt(7, level_error.get());
		query.setInt(8, level_fatal.get());
		query.setBlob(9, blob);
		query.addBatch();
		count++;
		if(count > 10) {
			count = 0;
			flushSql();
		}
	}

	private void flushSql() throws SQLException {
		query.executeBatch();
	}
}

/*
SET @sum = 0;
SELECT *,
	@sum := @sum + level_info + level_warn AS sum
FROM log
WHERE ID >= 2
AND @sum < 40
LIMIT 2, 2
 */
