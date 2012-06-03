package org.vaadin.addons.logview;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.vaadin.addons.logview.filter.FilterComponent;
import org.vaadin.addons.logview.table.LogTable;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalSplitPanel;

public class LogViewTabComponent extends CustomComponent {
	@AutoGenerated
	private HorizontalSplitPanel mainLayout;

	@AutoGenerated
	private LogTable logTable;

	@AutoGenerated
	private FilterComponent filter;

	private transient final Preferences tabPrefs;

	private transient final Preferences sharedPrefs;

	private final String name;

	private final LogViewComponent logView;

	private final int id;

	public LogViewTabComponent(LogViewComponent logView, int id, String name) {
		this.logView = logView;
		this.id = id;
		this.name = name;

		sharedPrefs = Preferences.userNodeForPackage(LogViewTabComponent.class);
		tabPrefs = sharedPrefs.node("settings." + id);

		buildMainLayout();
		setCompositionRoot(mainLayout);

		mainLayout.setSplitPosition(tabPrefs.getInt("splitter.filter", sharedPrefs.getInt("splitter.filter", 250)),
			Unit.PIXELS);
		mainLayout.setImmediate(true);
	}

	@AutoGenerated
	private HorizontalSplitPanel buildMainLayout() {
		// common part: create layout
		mainLayout = new HorizontalSplitPanel()/* {
												@Override
												public void changeVariables(Object source, Map<String, Object> variables) {
												super.changeVariables(source, variables);
												tabPrefs.putFloat("splitter.filter", mainLayout.getSplitPosition());
												sharedPrefs.putFloat("splitter.filter", mainLayout.getSplitPosition());
												}
												}*/;
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");

		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");

		// filter
		filter = new FilterComponent(logView, sharedPrefs, tabPrefs);
		filter.setImmediate(false);
		filter.setWidth("100.0%");
		filter.setHeight("100.0%");
		mainLayout.addComponent(filter);

		// logTable
		logTable = new LogTable();
		logTable.setImmediate(false);
		logTable.setWidth("100.0%");
		logTable.setHeight("100.0%");
		mainLayout.addComponent(logTable);

		return mainLayout;
	}

	public String getName() {
		return name;
	}

	public void updateTabFilter() {
		filter.updateTabFilter();
	}

	public void remove() {
		try {
			tabPrefs.removeNode();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public int getId() {
		return id;
	}
}
