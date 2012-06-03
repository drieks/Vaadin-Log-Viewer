package org.vaadin.addons.logview.filter.sub;

import org.vaadin.addons.logview.filter.FilterSettingData;

import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class RegexSettings extends AbstractSettings {
	public static final String REGEX = "regex";

	public RegexSettings(FilterSettingData data) {
		addComponent(new Label("RegEx:"));
		addComponent(new TextField(data.getExtendedProperty(REGEX)));
	}
}
