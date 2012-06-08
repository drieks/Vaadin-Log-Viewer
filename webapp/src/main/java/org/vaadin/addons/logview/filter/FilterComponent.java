package org.vaadin.addons.logview.filter;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.Root;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.CollapseEvent;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.TreeTable;

public abstract class FilterComponent extends CustomComponent {
	protected final FilterSettingContainer container;
	protected final TreeTable treetable;

	private final ItemClickEvent.ItemClickListener clickListener = new ItemClickEvent.ItemClickListener() {
		@Override
		public void itemClick(ItemClickEvent event) {
			if(event.getButton() == ClickEvent.BUTTON_LEFT && event.isDoubleClick()) {
				itemClicked(getItem(event.getItemId()));
			}
		}
	};

	private final Tree.CollapseListener collapseListener = new Tree.CollapseListener() {
		@Override
		public void nodeCollapse(CollapseEvent event) {
			itemCollapsed(getItem(event.getItemId()), true);
		}
	};

	private final Tree.ExpandListener expandListener = new Tree.ExpandListener() {
		@Override
		public void nodeExpand(ExpandEvent event) {
			itemCollapsed(getItem(event.getItemId()), false);
		}
	};

	protected FilterComponent(FilterSettingContainer containerArg) {
		container = containerArg;
		setSizeFull();

		treetable = new TreeTable();
		treetable.setSortDisabled(true);
		treetable.setEditable(true);
		treetable.setImmediate(true);
		treetable.setSizeFull();
		treetable.setContainerDataSource(container);
		treetable.setHierarchyColumn(FilterSettingContainer.NAME);
		treetable.setColumnWidth(FilterSettingContainer.ACTIVE, 25);
		treetable.setColumnHeader(FilterSettingContainer.ACTIVE, "");
		treetable.setVisibleColumns(new String[] {
			FilterSettingContainer.ACTIVE, FilterSettingContainer.NAME
		});
		treetable.setTableFieldFactory(new TableFieldFactory() {
			@Override
			public Field<?> createField(Container container, final Object itemId, Object propertyId, Component uiContext) {
				if(FilterSettingContainer.ACTIVE.equals(propertyId)) {
					final FilterSettingData data = getItem(itemId);
					final CheckBox ret = new CheckBox();
					if(data.isDetail()) {
						// ret.setVisible(false);
						ret.setEnabled(false);
					} else {
						ret.setImmediate(true);
						ret.addListener(new ValueChangeListener() {
							@Override
							public void valueChange(ValueChangeEvent event) {
								itemActive(data, (Boolean)event.getProperty().getValue());
							}
						});
					}
					return ret;
				}
				return null;
			}
		});

		setCompositionRoot(treetable);
	}

	protected void itemCollapsed(FilterSettingData data, boolean collapsed) {
		data.setCollapsed(collapsed);
	}

	protected void itemActive(FilterSettingData data, boolean active) {
		data.setActive(active);
	}

	protected void itemClicked(FilterSettingData id) {
		Item item = treetable.getItem(id.getId());
		Root.getCurrentRoot().addWindow(new FilterSettingsWindow(item));
	}

	protected FilterSettingData getItem(Object itemId) {
		BeanItem<FilterSettingData> item = container.getItem(itemId);
		if(item == null) {
			return null;
		}
		return item.getBean();
	}

	public void update() {
		// reload
		removeListener();
		container.removeAllItems();
		treetable.refreshRowCache();
		container.load();
		for(Integer id : container.getItemIds()) {
			// bug fix: expand all nodes
			treetable.setCollapsed(id, false);
			FilterSettingData data = getItem(id);
			Integer parent = data.getParent();
			// null is allowed here
			treetable.setParent(id, parent);
		}
		treetable.refreshRowCache();
		for(Integer id : container.getItemIds()) {
			FilterSettingData data = getItem(id);
			if(hasChilds(id, data)) {
				treetable.setCollapsed(id, data.isCollapsed());
			} else {
				treetable.setChildrenAllowed(id, false);
			}
		}
		treetable.refreshRowCache();
		addListener();
	}

	public boolean hasChilds(Integer id, FilterSettingData data) {
		return data.isGroup();
	}

	public void addListener() {
		treetable.addListener(clickListener);
		treetable.addListener(collapseListener);
		treetable.addListener(expandListener);
	}

	public void removeListener() {
		treetable.removeListener(clickListener);
		treetable.removeListener(collapseListener);
		treetable.removeListener(expandListener);
	}

	public void addFilter() {
	}

	public void removeFilter() {
	}
}
