package org.vaadin.addons.logview.filter;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.vaadin.addons.logview.LogViewComponent;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.TreeTable;

public class FilterEntryHelper {
	public static final String ACTIVE_PROPERTY = "active";
	public static final String NAME_PROPERTY = "name";
	public static final String REGEX_PROPERTY = "regex";

	private final TreeTable tt;

	private final Preferences sharedPrefs;
	private final Preferences filterPrefs;
	private final LogViewComponent logView;

	public FilterEntryHelper(LogViewComponent logView, TreeTable tt, Preferences sharedPrefs, Preferences tabPrefs) {
		this.logView = logView;
		this.tt = tt;
		this.sharedPrefs = sharedPrefs;
		this.filterPrefs = tabPrefs.node("filter");
		HierarchicalContainer ds = (HierarchicalContainer)tt.getContainerDataSource();
		ds.addListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				save();
			}
		});
	}

	public void newItem() {
		addEntry((int)(Math.random() * Integer.MAX_VALUE), -1, true, false, "Entry", "");
	}

	public void newCategory() {
		addEntry((int)(Math.random() * Integer.MAX_VALUE), -1, false, false, "Category", "");
	}

	public void remove(Object target) {
		tt.removeItem(target);
		save();
	}

	public boolean isCategory(Object id) {
		return tt.areChildrenAllowed(id);
	}

	private void addEntry(int id, int parent, boolean leave, boolean active, String name, String regex) {
		Item item = tt.getItem(id);
		if(item == null) {
			item = tt.addItem(id);
		}
		item.getItemProperty(NAME_PROPERTY).setValue(name);
		item.getItemProperty(ACTIVE_PROPERTY).setValue(active);
		item.getItemProperty(REGEX_PROPERTY).setValue(regex);
		if(parent != -1) {
			tt.setParent(id, parent);
		}
		if(leave) {
			tt.setChildrenAllowed(id, false);
		} else {
			tt.setCollapsed(id, !active);
		}
	}

	private void getIds(Object id, HashSet<Object> ids) {
		Collection<?> childs = tt.getChildren(id);
		if(childs != null) {
			for(Object child : childs) {
				ids.add(child);
				getIds(child, ids);
			}
		}
	}

	private HashSet<Object> getIds() {
		LinkedHashSet<Object> ret = new LinkedHashSet<Object>();
		for(Object id : tt.getItemIds()) {
			ret.add(id);
			getIds(id, ret);
		}
		return ret;
	}

	public void saveEntrys() {
		try {
			filterPrefs.clear();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
//		System.err.printf("------------------\n");
		int order = 0;
		for(Object id : getIds()) {
			order = saveEntrys(id, order);
		}
	}

	private int saveEntrys(Object id, int order) {
		Item item = tt.getItem(id);
		boolean leave = !tt.areChildrenAllowed(id);
		boolean active;
		if(leave) {
			active = (Boolean)item.getItemProperty(ACTIVE_PROPERTY).getValue();
		} else {
			active = !tt.isCollapsed(id);
		}
		String name = (String)item.getItemProperty(NAME_PROPERTY).getValue();
		String regex = (String)item.getItemProperty(REGEX_PROPERTY).getValue();
		Integer parent = (Integer)tt.getParent(id);
		if(parent == null) {
			parent = -1;
		}
//		System.err.printf("order:%d, id:%s parent:%s leave:%s active:%s %s\n", order, id, parent, leave, active, name);
		sharedPrefs.putInt("" + order, (Integer)id);
		sharedPrefs.putInt(id + ".parent", parent);
		sharedPrefs.putBoolean(id + ".leave", leave);
		sharedPrefs.putBoolean(id + ".active", active);
		filterPrefs.putBoolean(id + ".leave", leave);
		filterPrefs.putBoolean(id + ".active", active);
		sharedPrefs.put(id + ".name", name);
		sharedPrefs.put(id + ".regex", regex);
		return order + 1;
	}

	public void loadEntrys() {
		for(int i = 0;; i++) {
			int id = sharedPrefs.getInt("" + i, -1);
			if(id == -1) {
				break;
			}
			int parent = sharedPrefs.getInt(id + ".parent", -1);
			boolean leave;
			boolean active;
			if(filterPrefs.get(id + ".leave", null) != null) {
				leave = filterPrefs.getBoolean(id + ".leave", false);
				active = filterPrefs.getBoolean(id + ".active", false);
			} else {
				leave = sharedPrefs.getBoolean(id + ".leave", false);
				active = sharedPrefs.getBoolean(id + ".active", false);
			}
			String name = sharedPrefs.get(id + ".name", "" + id);
			String regex = sharedPrefs.get(id + ".regex", "");
//			System.err.printf("order:%d, id:%s parent:%s leave:%s active:%s %s\n", i, id, parent, leave, active, name);
			addEntry(id, parent, leave, active, name, regex);
		}
	}

	private boolean inAction = false;

	public synchronized void save() {
		if(inAction) {
			return;
		}
		inAction = true;

//		System.err.println("save start");
		saveEntrys();
		tt.refreshRowCache();
//		System.err.println("save done");
		inAction = false;
		logView.updateTabFilter();
	}

	public synchronized void load() {
		if(inAction) {
			return;
		}
		inAction = true;
//		System.err.println("load start");
		loadEntrys();
		tt.refreshRowCache();
//		System.err.println("load done");
		inAction = false;
	}

	public void updateTabFilter() {
		load();
	}
}
