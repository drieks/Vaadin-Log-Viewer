package org.vaadin.addons.logview.filter;

import java.util.Collection;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;

public class HierarchicalBeanItemContainer<T extends HierarchicalBean<T>> extends BeanItemContainer<T> implements
		Container.Hierarchical {
	private final T root;

	public HierarchicalBeanItemContainer(Class<? super T> type) throws IllegalArgumentException {
		super(type);
		root = createItem();
		addItem(root, root);
		fireItemSetChange();
	}

	public void moveAfterSibling(Object sourceItemId, Object targetItemId) {
		T source = convert(sourceItemId);
		T target = convert(targetItemId);
		source.getParent().moveAfterSibling(source, target);
		fireItemSetChange();
	}

	protected T createItem() {
		try {
			return convert(getBeanType().newInstance());
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean removeItem(Object itemId) {
		convert(itemId).setParent(null);
		return super.removeItem(itemId);
	}

	@Override
	public T addItem() {
		T ret = createItem();
		addItem(ret, ret);
		root.add(ret);
		fireItemSetChange();
		return ret;
	}

	@SuppressWarnings("unchecked")
	private T convert(Object itemId) {
		return (T)itemId;
	}

	@Override
	public Collection<T> getChildren(Object itemId) {
		return convert(itemId).getChildren();
	}

	@Override
	public T getParent(Object itemId) {
		return convert(itemId).getParent();
	}

	@Override
	public Collection<T> rootItemIds() {
		return root.getChildren();
	}

	@Override
	public boolean setParent(Object itemId, Object newParentId) throws UnsupportedOperationException {
		T node = convert(itemId);
		T newParent = convert(newParentId);
		T oldParent = node.getParent();
		if(oldParent != null) {
			oldParent.remove(node);
		}
		if(newParent == null) {
			root.add(node);
		} else {
			newParent.add(node);
		}
		fireItemSetChange();
		return true;
	}

	@Override
	public boolean areChildrenAllowed(Object itemId) {
		return convert(itemId).areChildrenAllowed();
	}

	@Override
	public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed) throws UnsupportedOperationException {
		convert(itemId).setChildrenAllowed(areChildrenAllowed);
		fireItemSetChange();
		return true;
	}

	@Override
	public boolean isRoot(Object itemId) {
		return root.hasChildren(convert(itemId));
	}

	@Override
	public boolean hasChildren(Object itemId) {
		return convert(itemId).hasChildren();
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + root.toString();
	}
}
