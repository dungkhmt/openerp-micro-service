package com.real_estate.common.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Sorter {

	public static  <T> List<T> sortTrick(List<T> listObject, List<?> listKeys, String methodName, T defaultObject, boolean trick) throws Exception {
		Map<String, T> map = new HashMap<>();
		for (T obj : listObject) {
			Method method = obj.getClass().getMethod(methodName);
			String key = method.invoke(obj).toString();
			map.put(key, obj);
		}

		List<T> results = new ArrayList<>();
		for (Object key : listKeys) {
			if (!map.containsKey(key.toString()) && !trick) {
				String errorMsg = String.format("Cannot find key by %s method in listIds", methodName);
				throw new Exception(errorMsg);
			}
			if (map.get(key.toString()) != null) {
				results.add(map.get(key.toString()));
			} else {
				results.add(defaultObject);
			}
		}

		return results;
	}
}
