package org.vaadin.addons.logview.filter;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class CategorySettings extends Window {
	public CategorySettings(Window root, Item item) {
		super("Edit Category");
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

		Button ok = new Button("OK", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				getParent().removeWindow(CategorySettings.this);
			}
		});
		addComponent(ok);

		root.addWindow(this);
	}
}
