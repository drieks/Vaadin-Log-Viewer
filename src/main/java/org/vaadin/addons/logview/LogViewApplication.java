package org.vaadin.addons.logview;

import com.vaadin.Application;
import com.vaadin.ui.Window;

public class LogViewApplication extends Application {
	@Override
	public void init() {
		setMainWindow(new Window("LogViewer", new LogViewComponent()));
	}
}
