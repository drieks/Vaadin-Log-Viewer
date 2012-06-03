package org.vaadin.addons.logview.filter;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import com.google.common.collect.Lists;

public class AbstractHierarchicalBean<T extends HierarchicalBean<T>> implements HierarchicalBean<T> {
	private final LinkedList<T> childs = Lists.newLinkedList();
	private boolean areChildrenAllowed = true;
	private T parent;

	@Override
	public Collection<T> getChildren() {
		return Collections.unmodifiableCollection(childs);
	}

	@Override
	public T getParent() {
		return parent;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setParent(T parent) {
		if(this.parent == parent) {
			return;
		}
		T old = this.parent;
		this.parent = parent;
		if(old != null) {
			old.remove((T)this);
		}
		if(this.parent != null) {
			this.parent.add((T)this);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void add(T node) {
		if(!childs.contains(node)) {
			childs.add(node);
			node.setParent((T)this);
		}
	}

	@Override
	public void remove(T node) {
		node.setParent(null);
		childs.remove(node);
	}

	@Override
	public boolean areChildrenAllowed() {
		return areChildrenAllowed;
	}

	@Override
	public boolean setChildrenAllowed(boolean areChildrenAllowed) {
		this.areChildrenAllowed = areChildrenAllowed;
		return true;
	}

	@Override
	public boolean hasChildren() {
		return !childs.isEmpty();
	}

	@Override
	public boolean hasChildren(T child) {
		return childs.contains(child);
	}

	@Override
	public void moveAfterSibling(T source, T target) {
		if(source == null || source.getParent() != this) {
			throw new IllegalArgumentException();
		}
		if(target != null && target.getParent() != this) {
			throw new IllegalArgumentException();
		}
		childs.remove(source);
		if(target == null) {
			childs.addFirst(source);
		} else {
			childs.add(childs.indexOf(target), source);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		for(T t : childs) {
			sb.append('\n');
			sb.append('\t');
			sb.append(t.toString());
		}
		return sb.toString();
	}
}
