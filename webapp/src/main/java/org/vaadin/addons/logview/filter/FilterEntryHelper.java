package org.vaadin.addons.logview.filter;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.vaadin.addons.logview.LogViewComponent;

import com.vaadin.data.Item;
import com.vaadin.ui.TreeTable;

public class FilterEntryHelper {
	public static final String ACTIVE_PROPERTY = "active";
	public static final String NAME_PROPERTY = "name";
	public static final String REGEX_PROPERTY = "regex";

	private final TreeTable tt;

	private final Preferences prefs = Preferences.userNodeForPackage(LogViewComponent.class).node("filter");

	public FilterEntryHelper(TreeTable tt) {
		this.tt = tt;
	}

	public void newItem() {
		addEntry((int)(Math.random() * Integer.MAX_VALUE), -1, true, false, "Eintrag", "");
		// save();
		tt.refreshRowCache();
	}

	public void newCategory() {
		addEntry((int)(Math.random() * Integer.MAX_VALUE), -1, false, false, "Kategorie", "");
		// save();
		tt.refreshRowCache();
	}

	public void remove(Object target) {
		tt.removeItem(target);
		// save();
		tt.refreshRowCache();
	}

	public boolean isCategory(Object id) {
		return tt.areChildrenAllowed(id);
	}

	private void addEntry(int id, int parent, boolean leave, boolean active, String name, String regex) {
		Item item = tt.addItem(id);
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

	public void saveEntrys(Preferences p) {
		try {
			p.clear();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
//		System.err.printf("------------------\n");
		int order = 0;
		for(Object id : getIds()) {
			order = saveEntrys(p, id, order);
		}
	}

	private int saveEntrys(Preferences p, Object id, int order) {
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
		p.putInt("" + order, (Integer)id);
		p.putInt(id + ".parent", parent);
		p.putBoolean(id + ".leave", leave);
		p.putBoolean(id + ".active", active);
		p.put(id + ".name", name);
		p.put(id + ".regex", regex);
		return order + 1;
	}

	public void loadEntrys(Preferences p) {
		for(int i = 0;; i++) {
			int id = p.getInt("" + i, -1);
			if(id == -1) {
				break;
			}
			int parent = p.getInt(id + ".parent", -1);
			boolean leave = p.getBoolean(id + ".leave", false);
			boolean active = p.getBoolean(id + ".active", false);
			String name = p.get(id + ".name", "" + id);
			String regex = p.get(id + ".regex", "");
			addEntry(id, parent, leave, active, name, regex);
		}
		tt.refreshRowCache();
	}

	public void save() {
		saveEntrys(prefs);
	}

	public void load() {
		loadEntrys(prefs);
	}
}
