package org.vaadin.addons.logview.server;

import java.io.File;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public class LogViewServer {
	public static void main(String[] args) throws Exception {
		Server server = new Server();
		HandlerCollection handlers = new HandlerCollection();
		handlers.addHandler(new WebAppContext(new File("../.webapp/src/main/webapp").getCanonicalPath(), "/"));
		server.setHandler(handlers);
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(8080);
		connector.setConfidentialPort(8443);
		server.addConnector(connector);
		server.start();
		server.join();
	}
}
