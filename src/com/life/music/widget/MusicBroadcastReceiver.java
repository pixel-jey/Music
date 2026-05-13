package com.life.music.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MusicBroadcastReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		String name = intent.getStringExtra("name");// 获得广播发出者传递的值
		Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
	}

}