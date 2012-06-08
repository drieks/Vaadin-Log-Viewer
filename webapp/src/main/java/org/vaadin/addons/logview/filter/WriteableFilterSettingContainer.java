/*
package org.vaadin.addons.logview.filter;

import java.util.LinkedList;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class WriteableFilterSettingContainer extends AbstractFilterSettingContainer {
	private final Preferences sharedPrefs;
	private final Preferences filterPrefs;
	private boolean preventUpdate = false;

	public WriteableFilterSettingContainer(Preferences sharedPrefs, Preferences tabPrefs) {
		super();
		this.sharedPrefs = sharedPrefs;
		this.filterPrefs = tabPrefs;
		load();
		updateCollapsed();
	}

	@Override
	public synchronized void update() {
		if(preventUpdate) {
			System.err.println("skip update");
		} else {
			System.err.println("update!");
			try {
				preventUpdate = true;
				fireItemSetChange();
				getTreeTable().refreshRowCache();
				save();
			} finally {
				preventUpdate = false;
			}
		}
	}

	@Override
	public synchronized void save() {
		boolean old = preventUpdate;
		try {
			preventUpdate = true;
			try {
				sharedPrefs.clear();
				filterPrefs.clear();
			} catch (BackingStoreException e) {
			}
			int i = 0;
			int root = getRoot().getId();
			LinkedList<FilterSettingData> order = Lists.newLinkedList();
			getRoot().getOrder(order);
			for(FilterSettingData data : order) {
				if(data == getRoot()) {
					continue;
				}
				data.store(root, i, sharedPrefs, filterPrefs);
				i++;
			}
			System.err.println("saved!");
		} finally {
			preventUpdate = old;
		}
	}

	public synchronized void load() {
		boolean old = preventUpdate;
		try {
			preventUpdate = true;

			Map<Integer, FilterSettingData> map = Maps.newHashMap();
			for(int i = 0;; i++) {
				if(sharedPrefs.getInt("" + i, -1) == -1) {
					break;
				}
				FilterSettingData data = new FilterSettingData(i, sharedPrefs, filterPrefs);
				addItem(data);
				map.put(data.getId(), data);
			}
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

			System.err.println("loaded!");

			fireItemSetChange();
			getTreeTable().refreshRowCache();
		} finally {
			preventUpdate = old;
		}
	}

	private void updateCollapsed() {
		for(FilterSettingData data : getItemIds()) {
			getTreeTable().setCollapsed(data, data.isCollapsed());
		}
	}
}
*/
