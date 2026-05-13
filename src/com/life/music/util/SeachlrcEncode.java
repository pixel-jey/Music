package com.life.music.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class SeachlrcEncode {

	private static String getFilecharset(String strPath) {
		File sourceFile = new File(strPath);
		String charset = "GBK";
		byte[] first3Bytes = new byte[3];
		try {
			boolean checked = false;
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(sourceFile));
			bis.mark(0);
			int read = bis.read(first3Bytes, 0, 3);
			if (read == -1) {
				return charset; // 文件编码为 ANSI
			} else if (first3Bytes[0] == (byte) 0xFF
					&& first3Bytes[1] == (byte) 0xFE) {
				charset = "UTF-16LE"; // 文件编码为 Unicode
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xFE
					&& first3Bytes[1] == (byte) 0xFF) {
				charset = "UTF-16BE"; // 文件编码为 Unicode big endian
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xEF
					&& first3Bytes[1] == (byte) 0xBB
					&& first3Bytes[2] == (byte) 0xBF) {
				charset = "UTF-8"; // 文件编码为 UTF-8
				checked = true;
			}
			bis.reset();
			if (!checked) {
				int loc = 0;
				while ((read = bis.read()) != -1) {
					loc++;
					if (read >= 0xF0)
						break;
					if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
						break;
					if (0xC0 <= read && read <= 0xDF) {
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)
							// (0x80
							// - 0xBF),也可能在GB编码内
							continue;
						else
							break;
					} else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) {
							read = bis.read();
							if (0x80 <= read && read <= 0xBF) {
								charset = "UTF-8";
								break;
							} else
								break;
						} else
							break;
					}
				}
			}
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return charset;
	}

	public static String read(String filePathAndName) {
		String fileContent = "";
		try {
			File f = new File(filePathAndName);
			if (f.isFile() && f.exists()) {
				String encode = getFilecharset(filePathAndName);
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(f), encode);
				BufferedReader reader = new BufferedReader(read);
				String line;
				while ((line = reader.readLine()) != null) {
					fileContent += line + "\r\n";
				}
				read.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileContent;
	}

	private static void write(String strPath, String txt) {
		File filename = new File(strPath);
		if (txt != "" && txt != null) {
			try {
				filename.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
				bw.write(txt);
				bw.flush();
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	public static void setLrc(String lrc) {
		if(! getFilecharset(lrc).equals("UTF-8")){
			String content = read(lrc);
			write(lrc, content);
		}
	}

	
	public static boolean checkLrc(String lrc){
		String str  = read(lrc);
		String code = "[00:0";
		boolean flag;
		if (str.indexOf(code) != -1) {
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}
	
	
	public static boolean checkStrLrc(String str){
		String code = "[00:";
		boolean flag;
		if (str.indexOf(code) != -1) {
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

	
	public static String autoAddTitle(String songName,String txt){
		return  "[00:00:00]" + songName + "\n" + txt;
	}
	

}
