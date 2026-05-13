package com.life.music.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressWarnings("unused")
public class FileSearcher {

	/**
	 * 递归查找文件
	 * @param baseDirName  查找的文件夹路径
	 * @param targetFileName  需要查找的文件名
	 * @param fileList  查找到的文件集合
	 */
    @SuppressWarnings("unchecked")
	public static void findFiles(String baseDirName, String targetFileName, List fileList) {
        /**
         * 算法简述：
         * 从某个给定的需查找的文件夹出发，搜索该文件夹的所有子文件夹及文件，
         * 若为文件，则进行匹配，匹配成功则加入结果集，若为子文件夹，则进队列。
         * 队列不空，重复上述操作，队列为空，程序结束，返回结果。
         */
        String tempName = null;
        //判断目录是否存在
        File baseDir = new File(baseDirName);
        if (!baseDir.exists() || !baseDir.isDirectory()){
            System.out.println("文件查找失败：" + baseDirName + "不是一个目录！");
        } else {
        	String[] filelist = baseDir.list();
    	    for (int i = 0; i < filelist.length; i++) {
    	    	File readfile = new File(baseDirName + "\\" + filelist[i]);
    	    	//System.out.println(readfile.getName());
    	        if(!readfile.isDirectory()) {
    	        	tempName =  readfile.getName(); 
                    if (FileSearcher.wildcardMatch(targetFileName, tempName)) {
                        //匹配成功，将文件名添加到结果集
                        fileList.add(readfile.getAbsoluteFile()); 
                    }
    	        } else if(readfile.isDirectory()){
    	        	findFiles(baseDirName + "\\" + filelist[i],targetFileName,fileList);
    	        }
    	    }
        }
    }
    
    /**
     * 通配符匹配
     * @param pattern    通配符模式
     * @param str    待匹配的字符串
     * @return    匹配成功则返回true，否则返回false
     */
    private static boolean wildcardMatch(String pattern, String str) {
        int patternLength = pattern.length();
        int strLength = str.length();
        int strIndex = 0;
        char ch;
        for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
            ch = pattern.charAt(patternIndex);
            if (ch == '*') {
                //通配符星号*表示可以匹配任意多个字符
                while (strIndex < strLength) {
                    if (wildcardMatch(pattern.substring(patternIndex + 1),
                            str.substring(strIndex))) {
                        return true;
                    }
                    strIndex++;
                }
            } else if (ch == '?') {
                //通配符问号?表示匹配任意一个字符
                strIndex++;
                if (strIndex > strLength) {
                    //表示str中已经没有字符匹配?了。
                    return false;
                }
            } else {
                if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
                    return false;
                }
                strIndex++;
            }
        }
        return (strIndex == strLength);
    }
    
    
    
	public static String[] searches(String baseDIR,String fileName) {
        fileName = "*"+fileName+"*";
        String[] name = {};
    	StringBuffer buffer = new StringBuffer();
        @SuppressWarnings("rawtypes")
		List<Comparable> resultList = new ArrayList<Comparable>();
        FileSearcher.findFiles(baseDIR, fileName, resultList); 
        if (resultList.size() != 0) {
            for (int i = 0; i < resultList.size(); i++) {
            	String files = resultList.get(i).toString();
            	files = files.replaceAll("\\\\","");
            	buffer.append(files + "\n");
            }
        }
        if(resultList.size() != 0){
        	name = buffer.toString().split("\n");
        }
        return name;
    }
    
	
	public static final String[] searches(String fileName){
		String baseDIR = SearchInfo.MP3;
    	String[] s = searches(baseDIR,fileName);
    	return s;
	}
	
	public static final String[] searchesList(String fileName){
		String baseDIR = SearchInfo.MP3;
    	String[] song = searches(baseDIR,fileName);
    	StringBuffer buffer = new StringBuffer();
    	String[] name = {};
    	for(int i =0;i<song.length;i++){
    		buffer.append(getSongName(song[i].toString()) + "\n");
    	}
    	if(song.length != 0){
          	name = buffer.toString().split("\n");
        }
    	return name;
	}
	
	
	public static final String getSongName(String fileName){
		int i = 0;
		File file = new File(fileName);
		String song = file.getName();
		song = SearchInfo.musicFormat(song);
		return song;
	}
	
	
	public static final int getSongId(String song){
		int i = 0;
		i = Cfg.stringToInt(SearchInfo.getSongId(song));
		return i;
	}

    public static List<String> extractConentByRegular(String msg){  
        List<String> list=new ArrayList<String>();  
        Pattern p = Pattern.compile("(\\[[^\\]]*\\])");  
        Matcher m = p.matcher(msg);  
        while(m.find()){  
            list.add(m.group().substring(1, m.group().length()-1));  
        }  
        return list;  
    } 
	


}