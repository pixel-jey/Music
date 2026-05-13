package com.life.music;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.view.KeyEvent;
import android.widget.ListView;


import com.life.music.util.Cfg;
import com.life.music.util.SearchInfo;
import com.life.music.util.Service;
import com.life.music.R;

public class SongsActivity extends SelectListsActivity {

	public void setTitle() {
		setTitle(" ");
	}

	public void showEnd(){
		String keyId = this.paramString("keyId");
		if(keyId!=null){
			ListView listView = (ListView) findViewById(R.id.listView1);
			listView.setStackFromBottom(true);
		}
	}
	

	public ArrayList<HashMap<String, Object>> getArrayListItemAdapterData() {
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		String[] s = SearchInfo.songs();
		String title = null;
		String size = null;
		for(int i=0;i<s.length;i++){
			HashMap<String, Object> map = new HashMap<String, Object>();
			title = s[i].toString();
			title = SearchInfo.musicFormat(title);
			size  = SearchInfo.getSize(new File(SearchInfo.MP3+s[i]));			
			map.put("ItemId", i);
			map.put("ItemTitle", title);
			map.put("ItemTitleEnd", size);
			listItem.add(map);
		}
		return listItem;
	}		
	
	
	
	public void play(String ids){
		try {
			Service.getInstance().stop();
		} catch (Exception e) {
		}
		bundle.putString("num", ids);
		activity(this, PlayActivity.class,bundle,false);
	}
	
	
	
	public void onListItemClick(String id) {
		play(id);
	}
	
	
	private String getTitle(int id){
		String[] s = SearchInfo.songs();
		String str = "";
		try {
			str =  s[id];
		} catch (Exception e) {
		}
		return str;
	}
	
	
	public void checkedMessage(String[] ids) {
		String title = "";
		String msg = "";
		if (ids.length != 0) {
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < ids.length; i++) {
				title = getTitle(Cfg.stringToInt(ids[i]));
				title = title.replace(".mp3", "");
				list.add(title);
			}
			msg = Cfg.listToString(list, "\n");
			msg = getResource(R.string.title_selected_list)+"\n" +msg;
			showMessage(this,msg);
		}
	}
	

	public void sureEvent(String ids) {
		String[] title = ids.split(",");
		String src = null;
		String[] song = SearchInfo.songs();
		for(int i=0;i<title.length;i++){
			int id = Cfg.stringToInt(title[i].toString());
			src  = song[id];
			String path = SearchInfo.MP3 + src;
			SearchInfo.deleteFile(path);
		}
		showMessage(this,getResource(R.string.title_successful_operation));
		onDisplay();
	}

	
	
	public void onSubmit(final String ids) {
		String[] items = ids.split(",");
		showConfirmMessage(getResource(R.string.title_selected_remove,items.length), ids,0, 0);
	}
	
	
	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
		if ((paramInt == 4)) {
			close();
			try {
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
			} catch (Exception e) {
				Intent intent= new Intent(Intent.ACTION_MAIN);  
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent); 
			}			
			return true;
		}
		return false;
	}

	

}
