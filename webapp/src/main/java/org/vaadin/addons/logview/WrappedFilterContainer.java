/*
package org.vaadin.addons.logview;

import java.util.Map;

import org.vaadin.addons.logview.filter.AbstractFilterSettingContainer;
import org.vaadin.addons.logview.filter.FilterSettingData;

import com.google.common.collect.Maps;
import com.vaadin.data.Container.ItemSetChangeListener;

public class WrappedFilterContainer extends AbstractFilterSettingContainer implements ItemSetChangeListener {
	private final AbstractFilterSettingContainer container;

	private final Map<FilterSettingData, FilterSettingData> map = Maps.newHashMap();

	public WrappedFilterContainer(AbstractFilterSettingContainer container) {
		this.container = container;
		container.addListener(this);
		containerItemSetChange(null);
	}

	@Override
	public void containerItemSetChange(ItemSetChangeEvent event) {
		removeAllItems();
		copy(container.getRoot(), null);
		updateLeaves(getRoot());
	}

	@Override
	public void save() {
		container.save();
	}

	@Override
	public void update() {
		updateLeaves(getRoot());
	}

	private void updateLeaves(FilterSettingData data) {
		boolean childs = data.hasChildren();
		data.setChildrenAllowed(childs);
		if(childs) {
			FilterSettingData orginal = map.get(data);
			if(orginal != null) {
				getTreeTable().setCollapsed(data, orginal.isCollapsed());
				data.setActive(orginal.isActive());
			}
			for(FilterSettingData child : data.getChildren()) {
				updateLeaves(child);
			}
		}
	}

	private void copy(FilterSettingData from, FilterSettingData to) {
		for(FilterSettingData child : from.getChildren()) {
			if(child.isDetail()) {
				FilterSettingData copy = child.clone();
				map.put(copy, child);
				addItem(copy);
				if(to != null) {
					setParent(copy, to);
				}
				copy(child, copy);
			} else {
				copy(child, to);
			}
		}
	}

	@Override
	public void setCollapsed(FilterSettingData data, boolean collapsed) {
		FilterSettingData orginal = map.get(data);
		if(orginal != null) {
			orginal.setCollapsed(collapsed);
			container.save();
		}
	}

	@Override
	public FilterSettingData map(FilterSettingData data) {
		return map.get(data);
	}
}
*/
