package org.vaadin.addons.logview.filter;

/*
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.TreeTable;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.vaadin.addons.logview.LogViewComponent;
import org.vaadin.addons.logview.filter.sub.Setting;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.HierarchicalContainer;

public class FilterEntryHelper {
	private final TreeTable tt;

	public FilterEntryHelper(TreeTable tt) {
		this.tt = tt;
	}

	public void newItem() {
		int id = (int)(Math.random() * Integer.MAX_VALUE);
		Item item = tt.getItem(id);
		if(item == null) {
			item = tt.addItem(id);
		}
		Property property = item.getItemProperty(FilterSettingContainer.ENTRY);
		if(property.getValue() == null) {
			property.setValue(new FilterSettingProperties());
		}
	}
	public static final String DETAIL_PROPERTY = "detail";
	public static final String REGEX_PROPERTY = "regex";
	public static final String TYPE_PROPERTY = "type";


	private final Preferences sharedPrefs;
	private final Preferences filterPrefs;
	private final LogViewComponent logView;

	public FilterEntryHelper(LogViewComponent logView, TreeTable tt, Preferences sharedPrefs, Preferences tabPrefs) {
		// Add Table columns
		tt.addContainerProperty(FilterEntryHelper.NAME_PROPERTY, String.class, "");
		tt.addContainerProperty(FilterEntryHelper.ACTIVE_PROPERTY, Boolean.class, false);
		tt.addContainerProperty(FilterEntryHelper.DETAIL_PROPERTY, Boolean.class, false);
		tt.addContainerProperty(FilterEntryHelper.REGEX_PROPERTY, String.class, "");
		tt.addContainerProperty(FilterEntryHelper.TYPE_PROPERTY, Setting.class, Setting.GROUP);

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
		//addEntry((int)(Math.random() * Integer.MAX_VALUE), -1, false, false, false, true, "Item", Setting.GROUP, "");
	}

	public void remove(Object target) {
		tt.removeItem(target);
		save();
	}

	public boolean isCategory(Object id) {
		return tt.areChildrenAllowed(id);
	}

	private void addEntry(int id, int parent, boolean group, boolean collapsed, boolean active, boolean detail,
			String name, Setting type, String regex) {
		Item item = tt.getItem(id);
		if(item == null) {
			item = tt.addItem(id);
		}
		item.getItemProperty(NAME_PROPERTY).setValue(name);
		item.getItemProperty(ACTIVE_PROPERTY).setValue(active);
		item.getItemProperty(DETAIL_PROPERTY).setValue(detail);
		item.getItemProperty(REGEX_PROPERTY).setValue(regex);
		item.getItemProperty(TYPE_PROPERTY).setValue(type);
		if(parent != -1) {
			tt.setParent(id, parent);
		}
		tt.setChildrenAllowed(id, group);
		tt.setCollapsed(id, collapsed);
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
		System.err.printf("------------------\n");
		int order = 0;
		for(Object id : getIds()) {
			order = saveEntrys(id, order);
		}
	}

	private int saveEntrys(Object id, int order) {
		Item item = tt.getItem(id);
		boolean active = (Boolean)item.getItemProperty(ACTIVE_PROPERTY).getValue();
		boolean detail = (Boolean)item.getItemProperty(DETAIL_PROPERTY).getValue();
		boolean collapsed = tt.isCollapsed(id);
		boolean group = tt.areChildrenAllowed(id);
		String name = (String)item.getItemProperty(NAME_PROPERTY).getValue();
		Setting type = (Setting)item.getItemProperty(TYPE_PROPERTY).getValue();
		String regex = (String)item.getItemProperty(REGEX_PROPERTY).getValue();
		Integer parent = (Integer)tt.getParent(id);
		if(parent == null) {
			parent = -1;
		}
		System.err.printf("[save] order:%d, id:%s parent:%s collapsed:%s active:%s leave:%s detail:%s type:%s %s\n",
			order, id, parent, collapsed, active, detail, group, type, name);
		sharedPrefs.putInt("" + order, (Integer)id);
		sharedPrefs.putInt(id + ".parent", parent);
		sharedPrefs.putBoolean(id + ".group", group);
		sharedPrefs.putBoolean(id + ".detail", detail);
		sharedPrefs.putBoolean(id + ".collapsed", collapsed);
		sharedPrefs.putBoolean(id + ".active", active);
		filterPrefs.putBoolean(id + ".collapsed", collapsed);
		filterPrefs.putBoolean(id + ".active", active);
		sharedPrefs.put(id + ".name", name);
		sharedPrefs.put(id + ".type", type.toString());
		sharedPrefs.put(id + ".regex", regex);
		return order + 1;
	}

	public void loadEntrys() {
		System.err.printf("------------------\n");
		for(int i = 0;; i++) {
			int id = sharedPrefs.getInt("" + i, -1);
			if(id == -1) {
				break;
			}
			int parent = sharedPrefs.getInt(id + ".parent", -1);
			Setting type = Setting.valueOf(sharedPrefs.get(id + ".type", Setting.GROUP.toString()).toUpperCase());
			boolean group = sharedPrefs.getBoolean(id + ".group", false);
			boolean detail = sharedPrefs.getBoolean(id + ".detail", false);
			boolean active;
			boolean collapsed;
			if(filterPrefs.get(id + ".active", null) != null) {
				active = filterPrefs.getBoolean(id + ".active", false);
				collapsed = filterPrefs.getBoolean(id + ".collapsed", false);
			} else {
				active = sharedPrefs.getBoolean(id + ".active", false);
				collapsed = sharedPrefs.getBoolean(id + ".collapsed", false);
			}
			String name = sharedPrefs.get(id + ".name", "" + id);
			String regex = sharedPrefs.get(id + ".regex", "");
			System.err.printf(
				"[load] order:%d, id:%s parent:%s collapsed:%s active:%s leave:%s detail:%s type:%s %s\n", i, id,
				parent, collapsed, active, detail, group, type, name);
			addEntry(id, parent, group, collapsed, active, detail, name, type, regex);
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
*/
