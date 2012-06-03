package org.vaadin.addons.logview.filter;

import com.vaadin.ui.TreeTable;

public class FilterSettingContainer extends HierarchicalBeanItemContainer<FilterSettingData> {
	public final static String NAME = "name";
	public final static String ACTIVE = "active";
	public final static String DETAILS = "detail";
	public final static String GROUP = "group";
	public final static String TYPE = "type";
	public final static String SELF = "self";

	private final TreeTable tt;

	public FilterSettingContainer(TreeTable tt) {
		super(FilterSettingData.class);
		this.tt = tt;
		tt.setContainerDataSource(this);
		tt.setHierarchyColumn(NAME);
		tt.setColumnWidth(ACTIVE, 25);
		tt.setColumnHeader(ACTIVE, "");
		tt.setVisibleColumns(new String[] {
			ACTIVE, NAME
		});
	}

	public void update() {
		fireItemSetChange();
		tt.refreshRowCache();
	}
}
