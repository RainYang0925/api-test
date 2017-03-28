package cn.simafei.test.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.simafei.test.functions.Function;

public class FunctionUtil{

	private static final Map<String, Class<? extends Function>> functionsMap = new HashMap<>();

	static {
		List<Class<?>> clazzes = ClassFinder.getAllAssignedClass(Function.class);
		clazzes.forEach((clazz) -> {
			try {
				Function tempFunc = (Function) clazz.newInstance();
				String referenceKey = tempFunc.getReferenceKey();
				if (referenceKey.length() > 0) { // ignore self
					functionsMap.put(referenceKey, tempFunc.getClass());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}
	
	public static boolean isFunction(String functionName){
		return functionsMap.containsKey(functionName);
	}
	
	public static String getValue(String functionName,String[] args){
		try {
			String value = functionsMap.get(functionName).newInstance().execute(args);
			return value.replace("\\,", ",");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
