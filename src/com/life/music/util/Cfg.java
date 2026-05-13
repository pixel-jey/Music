package com.life.music.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class Cfg {
	
	public final static String WINDOW_TITLE_BR = "\t\t\t\t";

	public static String[] listToStrings(List<?> list){
		String[] arr = (String[]) list.toArray(new String[list.size()]);
		return arr;
	}
	
	public static int stringToInt(String s) {
		int value = 0;
		try {
			value = Integer.parseInt(s);
		} catch (NumberFormatException e) {
		}
		return value;
	}
	
	public static String intToString(int a) {
		return String.valueOf(a);
	}
	
	
	public static boolean isExistStrings(String[] stringArr,String s){
		boolean flag =  false;
		for(int i=0;i<stringArr.length;i++){
			if(stringArr[i].equals(s)){
				flag = true;
			}
		}
		return flag;
	}
	
	/**
	 * @Description:把数组转换为一个用逗号分隔的字符串 
	 */
	public static String stringsToString(String[] arr) {
		String str = "";
		if(arr.length!=0){
		if (arr != null && arr.length > 0) {
			for (int i = 0; i < arr.length; i++) {
				str += arr[i] + ",";
			}
		}
		str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	/**
	 * @Description:把list转换为一个用逗号分隔的字符串
	 */
	public static String listToString(List<?> list) {
		StringBuilder sb = new StringBuilder();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				if (i < list.size() - 1) {
					sb.append(list.get(i) + ",");
				} else {
					sb.append(list.get(i));
				}
			}
		}
		return sb.toString();
	}
	
	public static String listToString(List<?> list,String symbol) {
		StringBuilder sb = new StringBuilder();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				if (i < list.size() - 1) {
					sb.append(list.get(i) + symbol);
				} else {
					sb.append(list.get(i));
				}
			}
		}
		return sb.toString();
	}
	
	public static String encode(String str) {
		String s = "";
		try {
			s = java.net.URLEncoder.encode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		return s;
	}
	
	
	public static String decode(String str){
		String s = "";
		try {
			s  = java.net.URLDecoder.decode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
		}
		return s ;
	}
	
	public static String filePrefix(String filePath){
	      String prefix = "";
		try {
		      File f =new File(filePath);
		      String fileName=f.getName();
			prefix = fileName.substring(fileName.lastIndexOf(".")+1);
		} catch (Exception e) {
		}
		return prefix;
	}

}
