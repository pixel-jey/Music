package com.life.music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.life.music.BaseActivity;
import com.life.music.R.id;
import com.life.music.util.Cfg;
import com.life.music.util.FileSearcher;
import com.life.music.util.SearchInfo;
import com.life.music.util.Service;
import com.life.music.R;


public class SelectListsActivity extends BaseActivity {
	
	final int checkin = Color.argb(0x56, 0x56, 0x56, 0x56);  
	final int checkout = Color.TRANSPARENT;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_songs);
		SearchInfo.init();
		new Thread(mRunnable).start();
		onDisplay();
		setTitle();
	}
	
	protected void setTitle(){
		
	}

	public void HandlerStartDoing(){
		try {
			if (Service.getInstance().player.isPlaying()) {
				playStatus();
				String s= Service.getInstance().songName;
				music(getResource(R.string.title_now_play_song,s));
			}
		} catch (Exception e) {
		}
		try {
			String s= PlayActivity.songName;
			if(PlayActivity.mediaPlayer.isPlaying()){
				music(getResource(R.string.title_now_play_song,s));
			}
		} catch (Exception e1) {
		}
	}
	
	private void music(String name){
		final String Intent_Action = "com.android.BroadcastReceiver";//定义广播，方便我们接收这个广播
		Intent intent = new Intent(Intent_Action);
		intent.putExtra("name", name); 
		this.sendBroadcast(intent);
	}	
	
	
	protected ArrayList<HashMap<String, Object>> getArrayListItemAdapterData() {
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		return listItem;
	}

	protected void onListItemClick(String id) {
		showMessage(this,getResource(R.string.title_click_event));
	}
	
	
	@SuppressWarnings("rawtypes")
	public void onSelectAllClicked(View v) {
		Button button3 = (Button) v.findViewById(R.id.button3);
		String s = button3.getText().toString();
		String tag = "";
		ArrayList lists = getArrayListItemAdapterData();
		int count = lists.size();
		StringBuffer buff = new StringBuffer();
		TextView text = (TextView) findViewById(R.id.textView1);
		
		if (count > 0) {
			if (s.equals(getResource(R.string.selectAllButton))) {
				for(int i = 0;i<lists.size();i++){
					Map map = (Map)lists.get(i);
					String id = map.get("ItemId").toString();
					buff.append(id+",");
				}
				tag = buff.toString();
				tag  = tag.substring(0, tag.length()-1);
				text.setTag(tag);
				button3.setText(R.string.cancelButton);
				ListView listView = (ListView) findViewById(R.id.listView1);
				listView.setBackgroundColor(checkin);
			} else {
				text.setTag("");
				ListView listView = (ListView) findViewById(R.id.listView1);
				listView.setBackgroundColor(checkout);
				button3.setText(R.string.selectAllButton);
			}
		}
	}

	public void onSelectCancelClicked(View v) {
		TextView text = (TextView) findViewById(R.id.textView1);
		text.setTag("");
		v.setBackgroundColor(checkout);
	}
	
	
	public void onSearchClicked(View view){
		seachEvent();
	}
	
	
	private void seachEvent(){
		final Dialog dialog =  dialogWindow(getResource(R.string.title_name),R.layout.activity_search,R.style.MyDialog); 
		final EditText editText1 = (EditText) dialog.findViewById(id.editText1);
		final EditText editText2 = (EditText) dialog.findViewById(id.editText2);
		final Button button1 = (Button) dialog.findViewById(id.button1);
		final Button button2 = (Button) dialog.findViewById(id.button2);	
		
		editText1.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				String song = editText1.getText().toString().trim();
				if(SearchInfo.checkSongExist(song)==true){
					isPlay(song + " "+getResource(R.string.title_Already_exists_play_now), song);
				}
				else if(song.length() != 0){
					String[] songs =  FileSearcher.searchesList(song);
					if(songs.length !=0){
						ArrayList<HashMap<String, Object>> adapterData = getSearchSongsArrayListItemAdapterData(songs);
						String title = getResource(R.string.title_choose_play);
						menuDialog(title,adapterData,song,true);
					}
				}
			}
		});		
		
		
		
		
		editText2.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				String song = editText1.getText().toString().trim();
				if(SearchInfo.checkSongExist(song)==true){
					isPlay(song + " "+getResource(R.string.title_Already_exists_play_now), song);
				}
			}
		});
		
		button1.setText(getResource(R.string.search));
		button1.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				String song = editText1.getText().toString().trim();
				String singer = editText2.getText().toString().trim();
				if (song.equals("")) {
					showMessage(ctx, getResource(R.string.title_song_empty));
				} else if (singer.equals("")) {
					showMessage(ctx, getResource(R.string.title_singer_empty));
				} else {
					if (SearchInfo.checkSongExist(song) == true) {
						isPlay(song + " "+getResource(R.string.title_Already_exists_play_now), song);
					} else {
						button1.setVisibility(View.INVISIBLE);
						TextView music = (TextView) findViewById(id.textViewSong);
						music.setText(song);
						music.setTag(singer);
						onUpdateReloadData();
						showMessage(ctx, getResource(R.string.title_search_down));
						editText1.setText("");
						button1.setVisibility(View.VISIBLE);
					}
				}
			}
		});
		
		button2.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if(editText1.getText().toString().equals(editText2.getText().toString())){
					dialog.hide();
				}else{
					editText1.setText("");
					editText2.setText("");					
				}
			}
		}); 
		
	}
	
	
	public void menuDialogEvent(String song) {
		int i =  FileSearcher.getSongId(song);
		Service.getInstance().init(i);
		songSelection(i);
	}
	
	
	public void onUpdateReloadData() {
		onThreadStart();
	}
	
	public void onThreadStartDoing() {
		TextView music = (TextView) findViewById(id.textViewSong);
		String song = music.getText().toString().trim();
		String singer = music.getTag().toString().trim();
		SearchInfo.search(song, singer);
	}
	
	
	private boolean status() {
		TextView music = (TextView) findViewById(id.textViewSong);
		String song = music.getText().toString().trim();
		boolean flag = false;
		if(SearchInfo.checkSongExist(song)==true){
			flag = true;
		}
		return flag;
	}
	
	private void isPlay(String msg,final String song){
		new AlertDialog.Builder(this)
		.setTitle(getResource(R.string.title_prompt))
		.setMessage(msg + "？")
		.setIcon(R.drawable.ic_launcher)
		.setPositiveButton(getResource(R.string.sureButton), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				setResult(RESULT_OK);
				// 确定按钮事件
				if(SearchInfo.checkSongExist(song)==true){
					int i = Cfg.stringToInt(SearchInfo.getSongId(song));
					Service.getInstance().init(i);
					songSelection(i);
				}
			}
		})
		.setNegativeButton(getResource(R.string.cancelButton), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// 取消按钮事件
			}
		}).show();
	}
	
	public void HandlerDoing() {
		TextView music = (TextView) findViewById(id.textViewSong);
		String song = music.getText().toString().trim();
		boolean flag = status();
		String msg = "";
		if(flag == false){
			msg = getResource(R.string.title_available_download);
			showMessage(ctx, msg);
		}else{
			isPlay(getResource(R.string.title_download_successful),song);
			onDisplay();
		}
	}
	
	
	
	
	
	
	public void onPlayClicked(View view) {
		Button button2 = (Button) view.findViewById(R.id.button2);
		try {
			if (!getValue().equals("")) {
				checkPlay();
				button2.setText(R.string.pause);
			} else {
				if (Service.getInstance().player.isPlaying()) {
					button2.setText(R.string.play);
				} else {
					button2.setText(R.string.pause);
				}
				Service.getInstance().pause();
			}
		} catch (Exception e) {
			checkPlay();
			button2.setText(R.string.pause);
		}
		TextView text = (TextView) findViewById(R.id.textView1);
		text.setTag("");
		songSelection();
	}
	
	private void checkPlay(){
		String items = getValue();
		if(items.length()!=0){
			String[] ids = items.split(",");
			String s = ids[0].toString();
			int id = Cfg.stringToInt(s);
			Service.getInstance().init(id);
		}else{
			Service.getInstance().init(0);
		}
	}

	public void onExitClicked(View view) {
		finish();
		try {
			android.os.Process.killProcess(android.os.Process.myPid());
		} catch (Exception e) {
			Intent intent= new Intent(Intent.ACTION_MAIN);  
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent); 
		}
	}	
	
	private void hideFirstLine(){
		TextView text1 = (TextView) findViewById(R.id.textView1);
		TextView text2 = (TextView) findViewById(R.id.textView2);
		TextView text3 = (TextView) findViewById(R.id.textView3);
		text1.setVisibility(View.GONE);
		text2.setVisibility(View.GONE);
		text3.setVisibility(View.GONE);
	}
	
	protected void showEnd(){
		
	}
	
	private void playStatus() {
		Button button2 = (Button) findViewById(R.id.button2);
		try {
			if (Service.getInstance().player.isPlaying()) {
				button2.setText(R.string.pause);
			} else {
				button2.setText(R.string.play);
			}
		} catch (Exception e) {

		}

	}
	
	private void songSelection(int id){
		ListView listView = (ListView) findViewById(R.id.listView1);
		listView.setSelection(id);
	}
	
	public void songSelection(){
		String song= Service.getInstance().songName;
		if(SearchInfo.checkSongExist(song)==true){
			int i = Cfg.stringToInt(SearchInfo.getSongId(song));
			songSelection(i);
		}
	}
	
	protected void onDisplay() {
		hideFirstLine();
		final ListView listView = (ListView) findViewById(R.id.listView1);
		ArrayList<HashMap<String, Object>> listItem = this
				.getArrayListItemAdapterData();
		final SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.activity_list_item_click, new String[] { "ItemId",
						"ItemTitle" ,"ItemTitleEnd" }, new int[] { R.id.textView1,
						R.id.textView2 ,R.id.textView3 });
		showEnd();
		playStatus();
		
		TextView text = (TextView) findViewById(R.id.textView1);
		text.setVisibility(View.GONE);
		listView.setAdapter(listItemAdapter); 
		
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				TextView text = (TextView) arg1.findViewById(R.id.textView1);
				text.setVisibility(View.GONE);
				String id = text.getText().toString();
				onListItemClick(id);
			}

		});
		
		
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				TextView text = (TextView) arg1.findViewById(R.id.textView1);
				String id = text.getText().toString();
				onChecked(id);
				return true;
			}
		});
		
		
		
	}
	
	protected void checkedMessage(String[] ids){
		
	}
	
	protected void onChecked(String id) {
		String items = getValue();
		String[] stringArr = items.split(",");
		String ids = "";
		ArrayList<String> list = new ArrayList<String>();
		if (! items.equals("")) {
			for (int i = 0; i < stringArr.length; i++) {
				list.add(stringArr[i]);
			}
			if (Cfg.isExistStrings(stringArr, id) == true) {
				list.remove(id);
			} else {
				list.add(id);
			}
			ids = Cfg.listToString(list);
		} else {
			ids = id;
		}
		TextView text = (TextView) findViewById(R.id.textView1);
		text.setTag(ids);
		if(! ids.equals("")){
			checkedMessage(ids.split(","));
		}
	}
	
	
	protected void onSubmit(String ids) {
	}
	
	
	public void showSelectEvent(View view) {
		String items = getValue();
		String message;
		if(items.length()!=0){
			message = getValue();
			this.onSubmit(message);
		} else {
			message = getResource(R.string.title_must_select_one);
			this.showMessage(this, message);
		}
	}
	
	
	private String getValue(){
		String s = "";
		try {
			TextView text = (TextView) findViewById(R.id.textView1);
			s =text.getTag().toString();
		} catch (Exception e) {
		}
		return s;
	}

	

}