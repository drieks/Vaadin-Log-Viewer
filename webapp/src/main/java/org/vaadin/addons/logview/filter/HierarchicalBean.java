package org.vaadin.addons.logview.filter;

import java.util.Collection;

public interface HierarchicalBean<T extends HierarchicalBean<T>> {
	Collection<T> getChildren();

	T getParent();

	void setParent(T parent);

	void add(T node);

	void remove(T node);

	boolean areChildrenAllowed();

	boolean setChildrenAllowed(boolean areChildrenAllowed);

	boolean hasChildren();

	boolean hasChildren(T child);

	void moveAfterSibling(T source, T target);
}
