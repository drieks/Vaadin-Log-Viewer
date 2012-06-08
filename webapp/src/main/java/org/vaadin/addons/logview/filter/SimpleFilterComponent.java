package org.vaadin.addons.logview.filter;

import java.util.Collection;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;

public class SimpleFilterComponent extends FilterComponent {
	private final Filter filter = new Filter() {
		@Override
		public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
			FilterSettingData data = getItem(itemId);
			return !data.isDetail();
		}

		@Override
		public boolean appliesToProperty(Object propertyId) {
			return FilterSettingContainer.DETAILS.equals(propertyId);
		}
	};

	public SimpleFilterComponent(FilterSettingContainer container) {
		super(container);
	}

	@Override
	public void addFilter() {
		container.addContainerFilter(filter);
	}

	@Override
	public void removeFilter() {
		container.removeContainerFilter(filter);
	}

	@Override
	public boolean hasChilds(Integer id, FilterSettingData data) {
		Collection<?> ret = treetable.getChildren(id);
		if(ret == null) {
			return false;
		}
		return !ret.isEmpty();
	}
}
