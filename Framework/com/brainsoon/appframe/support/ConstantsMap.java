package com.brainsoon.appframe.support;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("rawtypes")
public class ConstantsMap {
	private LinkedHashMap<Object, Object> map;

	public ConstantsMap() {
		map = new LinkedHashMap<Object, Object>();
	}

	public void putConstant(Object value, Object desc) {
		map.put(value, desc);
	}

	public Object getDescByValue(Object value) {
		return map.get(value);
	}

	public Object getValueByDesc(Object desc) {
		for (Object key : map.keySet()) {
			if (desc.equals(map.get(key))) {
				return key;
			}
		}
		return "";
	}

	public Set getValueSet() {
		return map.keySet();
	}

	public Collection getDescSet() {
		return map.values();
	}

	public Set getConstantsSet() {
		return map.entrySet();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}
	
	public Map getEntryMap() {
		return map;
	}
}
