package org.vaadin.addons.logview.filter;

import java.util.prefs.Preferences;

import com.vaadin.data.util.BeanContainer;

public class FilterSettingContainer extends BeanContainer<Integer, FilterSettingData> {
	public final static String NAME = "name";
	public final static String ACTIVE = "active";
	public final static String DETAILS = "detail";
	public final static String GROUP = "group";
	public final static String TYPE = "type";
	public final static String SELF = "self";

	private transient final Preferences filterPrefs;

	private transient final Preferences sharedPrefs;

	public FilterSettingContainer(Preferences sharedPrefs, Preferences filterPrefs) {
		super(FilterSettingData.class);
		this.sharedPrefs = sharedPrefs;
		this.filterPrefs = filterPrefs;
		load();
	}

	public void load() {
		// Map<Integer, FilterSettingData> map = Maps.newHashMap();
		for(int i = 0;; i++) {
			if(sharedPrefs.getInt("" + i, -1) == -1) {
				break;
			}
			FilterSettingData data = new FilterSettingData(i, sharedPrefs, filterPrefs);
			addBean(data);
			// map.put(data.getId(), data);
		}
		/*
		for(FilterSettingData data : getItemIds()) {
			if(data == getRoot()) {
				continue;
			}
			int parent = sharedPrefs.getInt(data.getId() + ".parent", -1);
			if(parent != -1) {
				setParent(data, map.get(parent));
			}
		}
		updateCollapsed();
		*/
	}

	@Override
	protected Integer resolveBeanId(FilterSettingData data) {
		return data.getId();
	}
}
