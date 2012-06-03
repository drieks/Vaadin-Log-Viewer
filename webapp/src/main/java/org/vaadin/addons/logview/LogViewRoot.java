package org.vaadin.addons.logview;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Root;

public class LogViewRoot extends Root {
	@Override
	protected void init(WrappedRequest request) {
		setContent(new LogViewComponent());
	}
}
