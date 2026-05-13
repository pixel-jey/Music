package com.life.music.util;

import android.annotation.SuppressLint;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;

import com.life.music.search.Baidu;
import com.life.music.search.Kugo;

public class SearchInfo {

	@SuppressLint("SdCardPath")
	public static final String MUSIC_PATH = "/mnt/sdcard/Music";
	
	public static final String MP3 = MUSIC_PATH+"/mp3/";
	
	public static final String LRC = MUSIC_PATH+"/lrc/";

	private static void createDir(String path) {
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdir();
	}

	public final static void init(){
		createDir(MUSIC_PATH);
		createDir(MP3);
		createDir(LRC);
	}
	
	
	public static org.jsoup.nodes.Document getDocument(String url) {
		try {
			return Jsoup.connect(url).get();
		} catch (IOException e) {
		}
		return null;
	}
	

	public static void down(String urlSrc, String root, String fileName) {
		URL url;
		try {
			url = new URL(urlSrc);
			InputStream in;
			try {
				in = url.openStream();
				File dir = new File(root);
				File file = new File(dir, fileName);
				in = url.openStream();
				FileOutputStream out = new FileOutputStream(file);

				byte[] buffer = new byte[1024];
				int hasRead = 0;
				while ((hasRead = in.read(buffer)) > 0) {
					out.write(buffer, 0, hasRead);
				}
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	

	public static String getSize(File file) {
		double ss = (double) file.length();
		return FileSize.getSize(ss);
	}

	public static String getDirectoryFile(String filePath) {
		StringBuffer buffer = new StringBuffer();
		File dir = new File(filePath);
		File[] files = dir.listFiles();
		if (files == null)
			return null;
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isDirectory()) {
				buffer.append(files[i].getName() + "\n");
			}
		}
		return buffer.toString();
	}

	public static String[] songs() {
		String path = SearchInfo.MP3;
		String songinfo = getDirectoryFile(path);
		return songinfo.split("\n");
	}
	
	public static String[] lrcs() {
		String path = SearchInfo.LRC;
		String songinfo = getDirectoryFile(path);
		return songinfo.split("\n");
	}

	public static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}
	
	public static boolean FileIsExist(String sPath){
		boolean flag = false;
		File file = new File(sPath);
		// 判断目录或文件是否存在
		if (file.isFile() && file.exists()) { 
			flag=  true;
		}
		return flag;
	}
	
	public static String readFile(String path,String separateMarks) {
		FileReader fr;
		String s = "";
		File file = new File(path);
		if (file.isFile() && file.exists()) {
			try {
				fr = new FileReader(path);
				BufferedReader br = new BufferedReader(fr);
				StringBuilder sb = new StringBuilder();
				try {
					while ((s = br.readLine()) != null) {
						sb.append(s + separateMarks);
					}
					s = sb.toString();
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return s;
	}
	

	@SuppressWarnings("resource")
	public static String read(String path) {
		BufferedReader br;
		String data = "";
		try {
			br = new BufferedReader(new FileReader(path));
			try {
				data = br.readLine();
			} catch (IOException e) {
			}
		} catch (FileNotFoundException e) {
		}
		return data;
	}
	
	public static void create(String strPath, String txt) {
		File filename = new File(strPath);
		if (txt != "" && txt != null) {
			try {
				filename.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
				bw.write(txt);
				bw.flush();
				bw.close();
			} catch (IOException e) {}
		}
	}
	
	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				@SuppressWarnings("resource")
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
		}
	}
	
	public static void renameFile(String oldFileName, String newFileName) {
		try {
			File oldf = new File(oldFileName);
			File newf = null;
			if (oldf != null) {
				newf = new File(newFileName);
				oldf.renameTo(newf);
			}
		} catch (Exception e) {
		}
	}
	

	public static final String musicFormat(String title){
		String[] s = musicFormat();
		for(int i=0;i<s.length;i++){
			title = title.replace(s[i].toString(), "");
		}
		return title;
	}

	
	
	public static final String[] musicFormat(){
		return new String[]{".mp3",".m4a",".mp4",".wav",".wma",".wave",".mpc",".aac"};
	}
	

	public static final String getSongId(String song){
		String[] songs = songs();
		String title = "";
		String id = "";
		for(int i=0;i<songs.length;i++){
			title = songs[i];
			title =  musicFormat(title);
			if(title.equals(song)){
				id = String.valueOf(i);
			}
		}
		return id;
	}
	
	
	public static boolean checkSongExist(String song){
		boolean flag = false;
		String[] s = songs();
		for(int i=0;i<s.length;i++){
			String title = s[i];
			title = musicFormat(title);
			if(title.equals(song)){
				flag = true;
			}
		}
		return flag;
	}
	
	public static boolean search(String title, String artist) {
		createDir(MUSIC_PATH);
		createDir(MP3);
		createDir(LRC);
		boolean flag = Baidu.search(title, artist);
		if(flag==true){
			flag = true;
		}else{
			flag = Kugo.search(title, artist);
		}
		return flag;
	}
	

}
