package com.life.music.search;


import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.life.music.util.Cfg;
import com.life.music.util.JSONString;
import com.life.music.util.SeachlrcEncode;
import com.life.music.util.SearchInfo;

public class Kugo {
	
	static SeachlrcEncode search;
	
	static SearchInfo music;
	
	static JSONString js;
	
	private static String hash(String title, String artist){
		String str = title + "-" + artist;
		String s ="";
		Document doc = SearchInfo
				.getDocument("http://mobilecdn.kugou.com/api/v3/search/song?format=jsonp&keyword="+Cfg.encode(str)+"&page=1&pagesize=10&showtype=1&callback=kgJSONP238513750>");
		if (doc != null) {
			Elements result = doc.children();
			s = hash(result.text());
		}
		return s; 
	}
	
	@SuppressWarnings("static-access")
	private static String hash(String s){
		  String content = getData(s);
		  String data =  js.StringToJSONObject(content,"data");
		  String info =  js.getValue(data,0,"info");
		  String hash = "";
		  if(js.JSONArrayToStringSize(info) != 0){
			  hash =  js.JSONArrayToString(info,"hash",0);
		  }
		  return hash;
	}
	
	
	
	public static String getData(String content) {
		content = content.replace("(", "");
		content = content.replace(")", "");
		return content.toString();
	}
	
	
	private static String fs(String hash){
		Document doc = SearchInfo.getDocument("http://m.kugou.com/app/i/getSongInfo.php?hash="+hash+"&cmd=playInfo");
		String url = "";
		if (doc != null) {
			Elements result = doc.children();
			url = result.text();
			url = JSONString.getValue(url,0,"url");
		}
		return url;
	}
		
	@SuppressWarnings("static-access")
	private static boolean getMP3(String title, String artist) {
		boolean flag = false;
		String hashcode = hash(title,artist);
		if(! hashcode.equals("")){
			String url = fs(hashcode);
			String f = Cfg.filePrefix(url);
			music.down(url, music.MP3, title + "."+f);
			flag =  true;
		}else{
			flag =  false;
		}
		return flag;
	}
	
	public static boolean search(String title, String artist) {
		return getMP3(title, artist);
	}

}
