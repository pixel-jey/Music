package com.life.music.util;

import java.util.ArrayList;
import java.util.List;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

public class Service {

	public List<String> musicList; // 存放找到的所有mp3的绝对路径。
	public MediaPlayer player; // 定义多媒体对象
	public int songNum; // 当前播放的歌曲在List中的下标
	public String songName; // 当前播放的歌曲名

	private static Service instatnce;

	private Service() {
	};

	public static Service getInstance() {
		if (instatnce == null) {
			synchronized (Service.class) {
				if (instatnce == null) {
					instatnce = new Service();
				}
			}
		}
		return instatnce;
	}

	public void init(int id) {
		try {
			getInstance().stop();
		} catch (Exception e) {
		}
		songNum = id;
		getInstance().start();
	}

	public void start() {
		musicList = new ArrayList<String>();
		String[] s = SearchInfo.songs();
		for (int i = 0; i < s.length; i++) {
			musicList.add(SearchInfo.MP3 + s[i]);
		}
		try {
			player = new MediaPlayer();
			player.reset(); // 重置多媒体
			String dataSource = musicList.get(songNum);// 得到当前播放音乐的路径
			songName = s[songNum];
			songName = SearchInfo.musicFormat(songName);
			player.setDataSource(dataSource);// 为多媒体对象设置播放路径
			player.prepare();// 准备播放
			player.start();// 开始播放
			// setOnCompletionListener 当当前多媒体对象播放完成时发生的事件
			player.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer arg0) {
					next();// 如果当前歌曲播放完毕,自动播放下一首.
				}
			});
		} catch (Exception e) {
			Log.v("MusicService", e.getMessage());
		}
	}

	public void next() {
		songNum = songNum == musicList.size() - 1 ? 0 : songNum + 1;
		start();
	}

	public void last() {
		songNum = songNum == 0 ? musicList.size() - 1 : songNum - 1;
		start();
	}

	public void pause() {
		if (player.isPlaying())
			player.pause();
		else
			player.start();
	}

	public void stop() {
		if (player.isPlaying()) {
			player.stop();
		}

	}
}
