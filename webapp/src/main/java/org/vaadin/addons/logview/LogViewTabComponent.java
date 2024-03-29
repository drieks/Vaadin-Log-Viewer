package org.vaadin.addons.logview;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.vaadin.addons.logview.filter.DetailFilterComponent;
import org.vaadin.addons.logview.filter.FilterComponent;
import org.vaadin.addons.logview.filter.FilterSettingContainer;
import org.vaadin.addons.logview.filter.SimpleFilterComponent;
import org.vaadin.addons.logview.table.LogTable;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalSplitPanel;

public class LogViewTabComponent extends CustomComponent {
	@AutoGenerated
	private HorizontalSplitPanel mainLayout;

	@AutoGenerated
	private LogTable logTable;

	private transient final Preferences configPrefs;

	private transient final Preferences globalPrefs;

	private final String name;

	private final int id;

	private final FilterSettingContainer container;

	private final DetailFilterComponent detail;

	private final SimpleFilterComponent simple;

	public LogViewTabComponent(LogViewComponent logView, int id, String name) {
		this.id = id;
		this.name = name;

		Preferences root = Preferences.userNodeForPackage(LogViewTabComponent.class);
		globalPrefs = root.node("global");
		configPrefs = root.node("user").node("default").node("" + id);

		container = new FilterSettingContainer(globalPrefs, configPrefs);
		detail = new DetailFilterComponent(container);
		simple = new SimpleFilterComponent(container);

		buildMainLayout();
		setCompositionRoot(mainLayout);

		mainLayout.setSplitPosition(configPrefs.getInt("splitter.filter", globalPrefs.getInt("splitter.filter", 250)),
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

		setSizeFull();
		mainLayout.setSizeFull();

		logTable = new LogTable();
		logTable.setImmediate(false);
		logTable.setSizeFull();
		detail.setSizeFull();
		simple.setSizeFull();
		mainLayout.setSecondComponent(logTable);

		return mainLayout;
	}

	public String getName() {
		return name;
	}

	public void remove() {
		try {
			configPrefs.removeNode();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public int getId() {
		return id;
	}

	public void setDetailsVisible(boolean detailsVisible) {
		FilterComponent old = (FilterComponent)mainLayout.getFirstComponent();
		if(old != null) {
			old.removeFilter();
		}
		FilterComponent filter = detailsVisible ? detail : simple;
		mainLayout.setFirstComponent(filter);
		filter.addFilter();
		filter.update();
	}
}
