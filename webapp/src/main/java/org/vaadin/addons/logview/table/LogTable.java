package org.vaadin.addons.logview.table;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CustomComponent;

public class LogTable extends CustomComponent {
	@AutoGenerated
	private AbsoluteLayout mainLayout;

	public LogTable() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	@AutoGenerated
	private void buildMainLayout() {
		mainLayout = new AbsoluteLayout();
	}
}
