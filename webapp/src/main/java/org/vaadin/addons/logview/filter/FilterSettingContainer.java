package org.vaadin.addons.logview.filter;

import java.util.LinkedHashSet;
import java.util.prefs.Preferences;

import com.google.common.collect.Sets;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.TreeTable;

public class FilterSettingContainer extends BeanContainer<Integer, FilterSettingData> {
	public final static String NAME = "name";
	public final static String ACTIVE = "active";
	public final static String DETAILS = "detail";
	public final static String GROUP = "group";
	public final static String TYPE = "type";
	public final static String SELF = "self";

	private transient final Preferences configPrefs;

	private transient final Preferences globalPrefs;
	private final ItemSetChangeListener listener = new ItemSetChangeListener() {
		@Override
		public void containerItemSetChange(ItemSetChangeEvent event) {
			updateOrder();
		}
	};

	public FilterSettingContainer(Preferences globalPrefs, Preferences configPrefs) {
		super(FilterSettingData.class);
		this.globalPrefs = globalPrefs;
		this.configPrefs = configPrefs;
		load();
	}

	public void refresh(TreeTable treetable) {
		removeListener(listener);
		updateOrder();
		removeAllItems();
		treetable.refreshRowCache();
		load();
	}

	public void load() {
		removeListener(listener);
		for(int i = 0;; i++) {
			int id = globalPrefs.getInt("" + i, -1);
			if(id == -1) {
				break;
			}
			addBean(FilterSettingData.forId(id, globalPrefs, configPrefs));
		}
		addListener(listener);
	}

	private void updateOrder() {
		LinkedHashSet<Integer> ids = Sets.newLinkedHashSet();
		findChilds(ids, null);
		int i = 0;
		for(int id : ids) {
			globalPrefs.putInt("" + i, id);
			i++;
		}
		globalPrefs.putInt("" + i, -1);
	}

	private void findChilds(LinkedHashSet<Integer> ids, Integer id) {
		if(id != null) {
			ids.add(id);
		}
		for(Integer i : getAllItemIds()) {
			FilterSettingData data = getData(i);
			Integer parent = data.getParent();
			if(parent == null && id == null || parent != null && parent.equals(id)) {
				findChilds(ids, data.getId());
			}
		}
	}

	@Override
	protected Integer resolveBeanId(FilterSettingData data) {
		return data.getId();
	}

	public void createItem(boolean group, FilterSettingData parent) {
		FilterSettingData data = FilterSettingData.forNewId(globalPrefs, configPrefs);

		if(parent != null) {
			data.setParent(parent.getId());
		}

		data.setGroup(group);
		data.setName(group ? "Group" : "Item");

		addBean(data);
	}

	protected FilterSettingData getData(Object itemId) {
		BeanItem<FilterSettingData> item = getItem(itemId);
		if(item == null) {
			return null;
		}
		return item.getBean();
	}

	public void moveAfterSibling(FilterSettingData source, FilterSettingData target) {
		removeItem(source.getId());
		addBeanAfter(target == null ? null : target.getId(), source);
	}

	public boolean isParentOf(int source, FilterSettingData target) {
		if(target == null) {
			return false;
		}
		Integer parentId = target.getParent();
		if(parentId != null && parentId == source) {
			System.err.println("test!");
			return true;
		}
		if(isParentOf(source, getData(parentId))) {
			return true;
		}
		return false;
	}
}
