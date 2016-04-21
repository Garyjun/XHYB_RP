package com.brainsoon.appframe.support;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ConstantsRepository implements IConstantsRepository {
	Map<Object, Object> map;
	private static class ConstantsRepositoryHolder {
		static ConstantsRepository instance = new ConstantsRepository();
	}
	
	public static ConstantsRepository getInstance() {
		return ConstantsRepositoryHolder.instance;
	}
	
	private ConstantsRepository() {
		map = new HashMap<Object, Object>();
	}
	public void register(Class cls, ConstantsMap constantsMap) {
		map.put(cls.getName(), constantsMap);
	}
	public ConstantsMap getConstantsMap(String className, String typeName) {
    	try {
    		String subClassName = className+"$"+typeName;
    		ConstantsMap object = (ConstantsMap)map.get(subClassName);
    		if(object == null) {
    			// force it to register
    			Class.forName(subClassName);
    			object = (ConstantsMap)map.get(subClassName);
    		}
   			return object;
    	} catch (ClassNotFoundException ex) {
    		return null;
    	}
    	
	}
	public String getConstantsDesc(String className, String typeName, Object value) {
		ConstantsMap constantsMap = getConstantsMap(className, typeName);
		return (String)constantsMap.getDescByValue(value);
	}

}
