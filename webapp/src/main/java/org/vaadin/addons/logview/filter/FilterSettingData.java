package org.vaadin.addons.logview.filter;

import java.io.Serializable;
import java.util.prefs.Preferences;

import org.vaadin.addons.logview.filter.sub.Setting;

public class FilterSettingData implements Serializable {
	private final int id;

	/*
	public FilterSettingData() {
		id = (int)(Math.random() * Integer.MAX_VALUE);
	}
	*/

	private transient final Preferences sharedPrefs;
	private transient final Preferences filterPrefs;

	public FilterSettingData(int order, Preferences sharedPrefs, Preferences filterPrefs) {
		this.sharedPrefs = sharedPrefs;
		this.filterPrefs = filterPrefs;
		id = sharedPrefs.getInt("" + order, (int)(Math.random() * Integer.MAX_VALUE));
	}

	public String getName() {
		return sharedPrefs.get(id + ".name", "" + id);
	}

	public void setName(String name) {
		sharedPrefs.put(id + ".name", name);
	}

	public boolean isDetail() {
		return sharedPrefs.getBoolean(id + ".detail", false);
	}

	public void setDetail(boolean detail) {
		sharedPrefs.putBoolean(id + ".detail", detail);
	}

	public boolean isGroup() {
		return sharedPrefs.getBoolean(id + ".group", false);
	}

	public void setGroup(boolean group) {
		sharedPrefs.putBoolean(id + ".group", group);
	}

	public boolean isActive() {
		if(isDetail()) {
			return false;
		}
		return filterPrefs.getBoolean(id + ".active", sharedPrefs.getBoolean(id + ".active", true));
	}

	public void setActive(boolean active) {
		sharedPrefs.putBoolean(id + ".active", active);
		filterPrefs.putBoolean(id + ".active", active);
	}

	public Setting getType() {
		return Setting.valueOf(sharedPrefs.get(id + ".type", Setting.GROUP.toString()).toUpperCase());
	}

	public void setType(Setting type) {
		if(type == null) {
			sharedPrefs.remove(id + ".type");
		} else {
			sharedPrefs.put(id + ".type", type.toString());
		}
	}

	public boolean isCollapsed() {
		return filterPrefs.getBoolean(id + ".collapsed", sharedPrefs.getBoolean(id + ".collapsed", false));
	}

	public void setCollapsed(boolean collapsed) {
		sharedPrefs.putBoolean(id + ".collapsed", collapsed);
		filterPrefs.putBoolean(id + ".collapsed", collapsed);
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return String.format("data('%s', %s) collapsed:%s", getName(), id, isCollapsed());
	}

	public void setParent(Integer parent) {
		String key = id + ".parent";
		if(parent == null) {
			sharedPrefs.remove(key);
		} else {
			sharedPrefs.putInt(key, parent);
		}
	}

	public Integer getParent() {
		String key = id + ".parent";
		if(sharedPrefs.get(key, null) == null) {
			return null;
		}
		return sharedPrefs.getInt(key, -1);
	}
}
