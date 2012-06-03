package org.vaadin.addons.logview.filter;

import java.util.Map;

import com.vaadin.data.Property;

public class MapProperty<K, V> implements Property<V> {
	private boolean readOnly = false;
	private final Map<K, V> map;
	private final K key;
	private final Class<V> valueClazz;

	public MapProperty(Map<K, V> map, K key, Class<V> valueClazz) {
		this.map = map;
		this.key = key;
		this.valueClazz = valueClazz;
	}

	@Override
	public Class<V> getType() {
		return valueClazz;
	}

	@Override
	public V getValue() {
		return map.get(key);
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValue(Object value) throws ReadOnlyException {
		if(readOnly) {
			throw new ReadOnlyException();
		}
		map.put(key, (V)value);
	}
}
