/*
package org.vaadin.addons.logview.filter;


import com.vaadin.data.Item;
import com.vaadin.data.Property;

public class FilterSettingProperty {

	private final Item item;
	private final FilterSettingData source;

	public FilterSettingProperty(Item item) {
		this.item = item;
		source = (FilterSettingData)item.getItemProperty(SELF).getValue();
	}

	public Property<?> getName() {
		return item.getItemProperty(NAME);
	}

	public Property<?> getDetail() {
		return item.getItemProperty(DETAIL);
	}

	public Property<?> getGroup() {
		return item.getItemProperty(GROUP);
	}

	public Property<?> getActive() {
		return item.getItemProperty(ACTIVE);
	}

	public Property<?> getType() {
		return item.getItemProperty(TYPE);
	}

	public FilterSettingData getSource() {
		return source;
	}

	public Property<?> getExtendedProperty(String type) {
		return new MapProperty<String, String>(source.getExtended(), type, String.class);
	}
}
*/
