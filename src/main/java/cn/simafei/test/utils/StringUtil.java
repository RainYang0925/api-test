package cn.simafei.test.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

public class StringUtil {
	public static boolean isNotEmpty(String str) {
		return null != str && !"".equals(str);
	}

	public static boolean isEmpty(String str) {
		return null == str || "".equals(str);
	}
	
	/**
	 * 
	 * @param sourceStr 待替换字符串
	 * @param matchStr  匹配字符串
	 * @param replaceStr  目标替换字符串
	 * @return
	 */
	public static String replaceFirst(String sourceStr,String matchStr,String replaceStr){
		int index = sourceStr.indexOf(matchStr);
		int matLength = matchStr.length();
		int sourLength = sourceStr.length();
		String beginStr = sourceStr.substring(0,index);
		String endStr = sourceStr.substring(index+matLength,sourLength);
		sourceStr = beginStr+replaceStr+endStr;
		return sourceStr;
	}

	public static boolean isJSON(String str) {
		try {
			JSON.parse(str);
			return true;
		} catch (JSONException e) {
			return false;
		}
	}

	public static boolean isXML(String str) {
		try {
			DocumentHelper.parseText(str);
			return true;
		} catch (DocumentException e) {
			return false;
		}
	}
}
