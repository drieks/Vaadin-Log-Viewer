package org.vaadin.addons.logview.filter;

import java.io.Serializable;
import java.util.prefs.Preferences;

import org.vaadin.addons.logview.filter.sub.Setting;

public class FilterSettingData implements Serializable {
	private final int id;
	private transient final Preferences globalPrefs;
	private transient final Preferences configPrefs;

	public static FilterSettingData forNewId(Preferences globalPrefs, Preferences configPrefs) {
		return forId((int)(Math.random() * Integer.MAX_VALUE), globalPrefs, configPrefs);
	}

	public static FilterSettingData forId(int id, Preferences globalPrefs, Preferences configPrefs) {
		return new FilterSettingData(id, globalPrefs.node("" + id), configPrefs.node("" + id));
	}

	private FilterSettingData(int id, Preferences globalPrefs, Preferences configPrefs) {
		this.id = id;
		this.globalPrefs = globalPrefs;
		this.configPrefs = configPrefs;
	}

	public String getName() {
		return globalPrefs.get("name", "" + id);
	}

	public void setName(String name) {
		globalPrefs.put("name", name);
	}

	public boolean isDetail() {
		return globalPrefs.getBoolean("detail", false);
	}

	public void setDetail(boolean detail) {
		globalPrefs.putBoolean("detail", detail);
	}

	public boolean isGroup() {
		return globalPrefs.getBoolean("group", false);
	}

	public void setGroup(boolean group) {
		globalPrefs.putBoolean("group", group);
	}

	public boolean isActive() {
		if(isDetail()) {
			return false;
		}
		return configPrefs.getBoolean("active", globalPrefs.getBoolean("active", true));
	}

	public void setActive(boolean active) {
		globalPrefs.putBoolean("active", active);
		configPrefs.putBoolean("active", active);
	}

	public Setting getType() {
		return Setting.valueOf(globalPrefs.get("type", Setting.GROUP.toString()).toUpperCase());
	}

	public void setType(Setting type) {
		if(type == null) {
			globalPrefs.remove("type");
		} else {
			globalPrefs.put("type", type.toString());
		}
	}

	public boolean isCollapsed() {
		return configPrefs.getBoolean("collapsed", globalPrefs.getBoolean("collapsed", false));
	}

	public void setCollapsed(boolean collapsed) {
		globalPrefs.putBoolean("collapsed", collapsed);
		configPrefs.putBoolean("collapsed", collapsed);
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return String.format("data('%s', %s) collapsed:%s", getName(), id, isCollapsed());
	}

	public void setParent(Integer parent) {
		String key = "parent";
		if(parent == null) {
			globalPrefs.remove(key);
		} else {
			globalPrefs.putInt(key, parent);
		}
	}

	public Integer getParent() {
		String key = "parent";
		if(globalPrefs.get(key, null) == null) {
			return null;
		}
		return globalPrefs.getInt(key, -1);
	}
}
