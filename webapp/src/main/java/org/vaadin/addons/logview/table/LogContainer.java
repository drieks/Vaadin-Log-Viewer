package org.vaadin.addons.logview.table;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.github.logview.api.DetailLogEntry;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.ImmutableList;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;

public class LogContainer implements Container, Indexed {
	private final BeanItemContainer<DetailLogEntry> container = new BeanItemContainer<DetailLogEntry>(
			DetailLogEntry.class);

	private final TableBlockManager manager = new TableBlockManager();

	private final FilteredTableBlockManager filtered = manager.createFilter(ImmutableList.of("info", "warn", "error",
		"fatal"));

	private final LoadingCache<Integer, DetailLogEntry> cache = CacheBuilder.newBuilder() //
			.maximumSize(1000) //
			.weakValues() //
			.removalListener(new RemovalListener<Integer, DetailLogEntry>() {
				@Override
				public void onRemoval(RemovalNotification<Integer, DetailLogEntry> notification) {
//					System.err.printf("unload %d\n", notification.getKey());
					container.removeItem(notification.getValue());
				}
			}) //
			.build(new CacheLoader<Integer, DetailLogEntry>() {
				@Override
				public DetailLogEntry load(Integer index) throws Exception {
					// System.err.printf("load %d\n", index);
					DetailLogEntry ret = filtered.getEntry(index);
					container.addBean(ret);
					return ret;
				}
			});

	private final LinkedHashMap<Integer, Integer> index = filtered.getIndex();

	@Override
	public Object nextItemId(Object itemId) {
		return container.nextItemId(itemId);
	}

	@Override
	public Object prevItemId(Object itemId) {
		return container.prevItemId(itemId);
	}

	@Override
	public Object firstItemId() {
		return container.firstItemId();
	}

	@Override
	public Object lastItemId() {
		return container.lastItemId();
	}

	@Override
	public boolean isFirstId(Object itemId) {
		return container.isFirstId(itemId);
	}

	@Override
	public boolean isLastId(Object itemId) {
		return container.isLastId(itemId);
	}

	@Override
	public Object addItemAfter(Object previousItemId) throws UnsupportedOperationException {
		return container.addItemAfter(previousItemId);
	}

	@Override
	public Item addItemAfter(Object previousItemId, Object newItemId) throws UnsupportedOperationException {
		return container.addItemAfter(previousItemId, newItemId);
	}

	@Override
	public int indexOfId(Object itemId) {
		return container.indexOfId(itemId);
	}

	@Override
	public Object getIdByIndex(int i) {
		Integer last = null;
		int sum = 0;
		int lastSum = 0;
		for(Entry<Integer, Integer> e : index.entrySet()) {
			int id = e.getKey();
			if(last == null) {
				last = id;
			}
			if(i + 1 <= sum) {
				Integer ret = Integer.valueOf(last * TableBlockManager.BLOCK_SIZE + (i - lastSum));
				/*
				System.err.printf("id %d block %d sum %d lastSum %d off %d abs %d\n", i, last, sum, lastSum, i
						- lastSum, ret);
				*/
				return cache.getUnchecked(ret);
			}
			last = id;
			lastSum = sum;
			sum += e.getValue();
		}
		return null;
	}

	@Override
	public Object addItemAt(int index) throws UnsupportedOperationException {
		return container.addItemAt(index);
	}

	@Override
	public Item addItemAt(int index, Object newItemId) throws UnsupportedOperationException {
		return container.addItemAt(index, newItemId);
	}

	@Override
	public Item getItem(Object itemId) {
		return container.getItem(itemId);
	}

	@Override
	public Collection<?> getContainerPropertyIds() {
		return container.getContainerPropertyIds();
	}

	@Override
	public Collection<?> getItemIds() {
		return container.getItemIds();
	}

	@Override
	public Property<?> getContainerProperty(Object itemId, Object propertyId) {
		return container.getContainerProperty(itemId, propertyId);
	}

	@Override
	public Class<?> getType(Object propertyId) {
		return container.getType(propertyId);
	}

	@Override
	public int size() {
		return filtered.size();
	}

	@Override
	public boolean containsId(Object itemId) {
		return container.containsId(itemId);
	}

	@Override
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		return container.addItem(itemId);
	}

	@Override
	public Object addItem() throws UnsupportedOperationException {
		return container.addItem();
	}

	@Override
	public boolean removeItem(Object itemId) throws UnsupportedOperationException {
		return container.removeItem(itemId);
	}

	@Override
	public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue)
			throws UnsupportedOperationException {
		return container.addContainerProperty(propertyId, type, defaultValue);
	}

	@Override
	public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
		return container.removeContainerProperty(propertyId);
	}

	@Override
	public boolean removeAllItems() throws UnsupportedOperationException {
		return container.removeAllItems();
	}
}
