package org.vaadin.addons.logview.filter.sub;

import java.util.Set;

import org.vaadin.addons.logview.filter.FilterSettingData;

import com.google.common.collect.ImmutableSet;
import com.vaadin.data.Item;

public enum Setting {
	GROUP("Group", GroupSettings.class),
	MENU("Menu", GroupSettings.class),
	AND("And", GroupSettings.class),
	OR("Or", GroupSettings.class),
	NOT("Not", GroupSettings.class),
	ACCEPT("Accept", GroupSettings.class),
	IGNORE("Ignore", GroupSettings.class),
	COLOR("Color", ColorSettings.class),
	REGEX("Regex", RegexSettings.class, ImmutableSet.of(RegexSettings.REGEX));

	private final Class<? extends AbstractSettings> clazz;
	private final String label;
	private final ImmutableSet<String> extended;

	private Setting(String label, Class<? extends AbstractSettings> clazz) {
		this(label, clazz, new ImmutableSet.Builder<String>().build());
	}

	private Setting(String label, Class<? extends AbstractSettings> clazz, ImmutableSet<String> extended) {
		this.clazz = clazz;
		this.label = label;
		this.extended = extended;
	}

	public String getLabel() {
		return label;
	}

	public AbstractSettings create(FilterSettingData data) {
		try {
			return clazz.getConstructor(Item.class).newInstance(data);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return label;
	}

	public Set<String> getExtended() {
		return extended;
	}
}
