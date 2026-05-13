package com.life.music.util;

import android.annotation.SuppressLint;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@SuppressLint("NewApi")
public class JSONString {

	private static JSONString instatnce;

	private JSONString() {
	};

	public static JSONString getInstance() {
		if (instatnce == null) {
			synchronized (JSONString.class) {
				if (instatnce == null) {
					instatnce = new JSONString();
				}
			}
		}
		return instatnce;
	}

	public static String getStringsToReplace(String s, String[] content) {
		for (int i = 0; i < content.length; i++) {
			s = s.replace(content[i], "");
		}
		return s.toString();
	}

	public static String getValue(String content, int count, String name) {
		String str = "[" + content + "]";
		JSONArray jsonArray = null;
		String value = "";
		if (content.length() != 0) {
			try {
				jsonArray = new JSONArray(str);
				try {
					value = (String) jsonArray.getJSONObject(count).get(name)
							.toString();
				} catch (Exception e) {
					// e.printStackTrace();
				}

			} catch (JSONException e) {
				// e.printStackTrace();
			}
		}
		return value;
	}

	public static String sign(org.json.simple.JSONArray array, String type,
			String s) {
		String arr = array.toString();
		if (s.isEmpty()) {
			s = "";
		}
		return "{" + type + ":" + arr + "}" + s;
	}

	public static String midleSign(String content, String s) {
		return "[" + content + "]" + s;
	}

	public static String MapToJSON(Map<?, ?> json) {
		String string = "{";
		for (Iterator<?> it = json.entrySet().iterator(); it.hasNext();) {
			Entry<?, ?> e = (Entry<?, ?>) it.next();
			string += "\"" + e.getKey() + "\":";
			string += "\"" + e.getValue() + "\",";
		}
		string = string.substring(0, string.lastIndexOf(","));
		string += "}";
		return string;
	}

	public static String JSONObjectTOString(String content, String obj) {
		String str = "";
		try {
			JSONObject js = new JSONObject(content);
			str = js.getString(obj).toString();
		} catch (JSONException e) {
			// e.printStackTrace();
		}
		return str;
	}

	public static int JSONObjectTOStringSize(String content) {
		int count = 0;
		try {
			JSONObject js = new JSONObject(content);
			count = js.length();
		} catch (JSONException e) {
			// e.printStackTrace();
		}
		return count;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<String> JSONObjectTOStringList(String content) {
		int size = JSONObjectTOStringSize(content);
		List list = new ArrayList<Object>();
		for (int i = 0; i < size; i++) {
			list.add(JSONObjectTOString(content, Cfg.intToString(i)));
		}
		return list;
	}

	public static int JSONObjectTOInt(String content, String obj) {
		int o = 0;
		String str = JSONObjectTOString(content, obj);
		if (!str.equalsIgnoreCase("") && !str.equalsIgnoreCase(null)) {
			o = Cfg.stringToInt(str);
		}
		return o;
	}

	public static String StringToJSONObject(String content, String obj) {
		String strs = null;
		try {
			JSONObject js = new JSONObject(content);
			strs = js.getJSONObject(obj).toString();
		} catch (JSONException e) {
			// e.printStackTrace();
		}
		return strs;
	}

	public static String StringToJSONArray(String content, String obj, int i) {
		String strs = null;
		try {
			JSONObject jo = new JSONObject(content);
			JSONArray like = jo.getJSONArray(obj);
			strs = like.getString(i);
		} catch (JSONException e) {
			// e.printStackTrace();
		}
		return strs;
	}

	public static int StringToJSONArraySize(String content, String obj) {
		JSONObject jo;
		int count = 0;
		try {
			jo = new JSONObject(content);
			JSONArray like = jo.getJSONArray(obj);
			count = like.length();
		} catch (JSONException e) {
			// e.printStackTrace();
		}
		return count;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<String> StringToJSONArrays(String content, String obj) {
		List list = new ArrayList();
		int size = StringToJSONArraySize(content, obj);
		for (int i = 0; i < size; i++) {
			list.add(StringToJSONArray(content, obj, i).toString());
		}
		return list;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<String> StringToJSONArrayMap(String content, String obj) {
		List list = new ArrayList();
		int size = StringToJSONArraySize(content, obj);
		for (int i = 0; i < size; i++) {
			Map map = new LinkedHashMap();
			map.put("data", StringToJSONArray(content, obj, i).toString());
			list.add(map);
		}
		return list;
	}

	public static String JSONArrayToString(String content, String obj, int i) {
		JSONArray myJsonArray;
		String s = "";
		try {
			myJsonArray = new JSONArray(content);
			if (myJsonArray.length() != 0) {
				org.codehaus.jettison.json.JSONObject myjObject = myJsonArray
						.getJSONObject(i);
				s = myjObject.getString(obj);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return s;
	}

	public static int JSONArrayToStringSize(String content) {
		JSONArray myJsonArray;
		int c = 0;
		try {
			myJsonArray = new JSONArray(content);
			c = myJsonArray.length();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return c;
	}

}
