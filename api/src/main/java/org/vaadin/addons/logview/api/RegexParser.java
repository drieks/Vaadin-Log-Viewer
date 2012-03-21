package org.vaadin.addons.logview.api;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexParser {
	private final Pattern pattern;
	private final Pattern[] patterns;
	private final int max;
	private final String[] field;
	private final String[] type;
	private final Integer[] ids;
	private final SimpleDateFormat[] sdf;

	public RegexParser(String prefix, Class<?> clazz, String resource) throws IOException {
		this(prefix, loadProps(clazz, resource));
	}

	private static Properties loadProps(Class<?> clazz, String resource) throws IOException {
		Properties props = new Properties();
		props.load(clazz.getResourceAsStream(resource));
		return props;
	}

	public RegexParser(String prefix, Properties props) {
		if(prefix == null || "".equals(prefix)) {
			pattern = null;
			prefix = "";
		} else {
			pattern = Pattern.compile((String)props.get(prefix));
			prefix = prefix + ".";
		}

		int max;
		for(max = 0;; max++) {
			if(props.getProperty(prefix + max) == null) {
				break;
			}
		}
		this.max = max;

		if(pattern == null) {
			patterns = new Pattern[max];
			for(int i = 0; i < max; i++) {
				patterns[i] = Pattern.compile((String)props.get(Integer.toString(i)));
			}
		} else {
			patterns = null;
		}

		field = new String[max];
		type = new String[max];
		sdf = new SimpleDateFormat[max];
		ids = new Integer[max];

		for(int i = 0; i < max; i++) {
			field[i] = props.getProperty(prefix + i);
			type[i] = props.getProperty(prefix + i + ".type");
			String id = props.getProperty(prefix + i + ".id");
			if(id != null) {
				ids[i] = Integer.parseInt(id);
			}
			if("date".equals(type[i])) {
				String format = props.getProperty(prefix + i + ".format");
				sdf[i] = new SimpleDateFormat(format);
			}
		}
	}

	public static class MatchResult {
		public Integer id;
		public Object value;
	}

	public Map<String, MatchResult> matchLine(String line) throws ParseException {
		if(pattern == null) {
			throw new IllegalStateException("no pattern set!");
		}
		Matcher matcher = pattern.matcher(line);
		if(!matcher.find()) {
			return null;
		}
		if(matcher.groupCount() != max) {
			throw new RuntimeException(String.format("wrong entry count on line (is:%d should:%d):\n%s",
				matcher.groupCount(), max, line));
		}
		LinkedHashMap<String, MatchResult> ret = new LinkedHashMap<String, MatchResult>();
		for(int i = 0; i < max; i++) {
			MatchResult result = new MatchResult();
			result.id = ids[i];
			String group = matcher.group(i + 1).trim();
			if(type[i] == null) {
				result.value = group;
			} else if("date".equals(type[i])) {
				result.value = sdf[i].parse(group);
			} else {
				throw new RuntimeException("wrong type " + type[i]);
			}
			ret.put(field[i], result);
		}
		return ret;
	}

	public MatchResult matchTag(String tag) throws ParseException {
		if(patterns == null) {
			throw new IllegalStateException("no patterns set!");
		}

		for(int i = 0; i < max; i++) {
			Matcher matcher = patterns[i].matcher(tag);
			if(matcher.find()) {
				MatchResult ret = new MatchResult();
				ret.id = ids[i];
				String group = matcher.group(1);
				if(type[i] == null) {
					ret.value = group;
				} else if("date".equals(type[i])) {
					ret.value = sdf[i].parse(group);
				} else if("int".equals(type[i])) {
					ret.value = Integer.parseInt(group);
				} else if("long".equals(type[i])) {
					ret.value = Long.parseLong(group);
				} else if("double".equals(type[i])) {
					ret.value = Double.parseDouble(group);
				} else {
					throw new RuntimeException("wrong type " + type[i]);
				}
				return ret;
			}
		}
		return null;
	}
}
