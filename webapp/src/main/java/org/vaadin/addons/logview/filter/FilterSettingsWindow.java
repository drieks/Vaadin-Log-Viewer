package org.vaadin.addons.logview.filter;

import org.vaadin.addons.logview.filter.sub.Setting;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Root;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class FilterSettingsWindow extends Window {
	private final VerticalLayout sub = new VerticalLayout();

	public FilterSettingsWindow(final Item item/*, final AbstractFilterSettingContainer container*/) {
		super("Edit Filter");
		@SuppressWarnings("unchecked")
		BeanItem<FilterSettingData> bi = (BeanItem<FilterSettingData>)item;
		final FilterSettingData data = bi.getBean();
		final FieldGroup group = new FieldGroup(item);

		setModal(true);
		VerticalLayout layout = (VerticalLayout)getContent();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeUndefined();
		setWidth("400px");

		// Name
		TextField tf = new TextField("Name:");
		group.bind(tf, FilterSettingContainer.NAME);
		addComponent(tf);

		// Details
		CheckBox details = new CheckBox("Show in Detail View only");
		group.bind(details, FilterSettingContainer.DETAILS);
		addComponent(details);

		// Children
		CheckBox children = new CheckBox("Children allowed?");
		// don't use FilterSettingContainer.CHILDREN, or getChildren is called...
		group.bind(children, FilterSettingContainer.GROUP);
		/* TODO
		if(data.hasChildren()) {
			children.setEnabled(false);
		}
		*/
		addComponent(children);

		// Type
		NativeSelect select = new NativeSelect("Type:");
		for(Setting type : Setting.values()) {
			select.addItem(type);
		}
		select.setImmediate(true);
		select.setNullSelectionAllowed(false);
		select.addListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				sub.removeAllComponents();
				sub.addComponent(data.getType().create(data));
			}
		});
		// group.bind(select, FilterSettingContainer.TYPE);
		addComponent(select);

		// Sub Settings
		addComponent(sub);

		// OK
		addComponent(new Button("OK", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					group.commit();
				} catch (CommitException e) {
					e.printStackTrace();
				}
				Root.getCurrentRoot().removeWindow(FilterSettingsWindow.this);
				// container.update();
			}
		}));

		// Cancel
		addComponent(new Button("Cancel", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Root.getCurrentRoot().removeWindow(FilterSettingsWindow.this);
			}
		}));
	}
}
