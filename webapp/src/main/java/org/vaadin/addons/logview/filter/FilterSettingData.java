package org.vaadin.addons.logview.filter;

import java.io.Serializable;
import java.util.Map;

import org.vaadin.addons.logview.filter.sub.Setting;

import com.google.common.collect.Maps;

public class FilterSettingData extends AbstractHierarchicalBean<FilterSettingData> implements Serializable {
	private String name;
	private boolean detail;
	private boolean group;
	private boolean active;
	private Setting type;
	private final Map<String, String> extended = Maps.newHashMap();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDetail() {
		return detail;
	}

	public void setDetail(boolean detail) {
		this.detail = detail;
	}

	public boolean isGroup() {
		return group;
	}

	public void setGroup(boolean group) {
		this.group = group;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Setting getType() {
		return type;
	}

	public void setType(Setting type) {
		this.type = type;
	}

	public String getExtendedProperty(String string) {
		return null;
	}

	public FilterSettingData getSelf() {
		return this;
	}

	public Map<String, String> getExtended() {
		return extended;
	}

	@Override
	public boolean areChildrenAllowed() {
		return group;
	}

	@Override
	public boolean setChildrenAllowed(boolean areChildrenAllowed) {
		this.group = areChildrenAllowed;
		return true;
	}
}
