package org.vaadin.addons.logview.api;

import java.io.File;

public interface Loader extends Appender {
	LogFile load(File file);
}
