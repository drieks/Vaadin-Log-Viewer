package org.vaadin.addons.logview.filter;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class FilterSettings extends Window {
	public FilterSettings(Window root, Item item) {
		super("Edit Filter");
		setModal(true);
		VerticalLayout layout = (VerticalLayout)getContent();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeUndefined();

		Label l = new Label("Name:");
		l.setWidth("400px");
		addComponent(l);

		TextField tf = new TextField(item.getItemProperty(FilterEntryHelper.NAME_PROPERTY));
		tf.setWidth("400px");
		addComponent(tf);

		Label l2 = new Label("RegEx:");
		l.setWidth("400px");
		addComponent(l2);

		TextField tf2 = new TextField(item.getItemProperty(FilterEntryHelper.REGEX_PROPERTY));
		tf.setWidth("400px");
		addComponent(tf2);

		Button ok = new Button("OK", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				getParent().removeWindow(FilterSettings.this);
			}
		});
		addComponent(ok);

		root.addWindow(this);
	}
}
