package com.life.music;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.life.music.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;	
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

@SuppressLint({ "HandlerLeak", "NewApi" })
public class BaseActivity extends Activity {
	Bundle bundle = new Bundle();
	Context ctx = this;

	protected void activity(Context ctx, Class<?> cls) {
		Intent intent = new Intent(ctx, cls);
		startActivity(intent);
	}

	protected void activity(Context ctx, Class<?> cls, Bundle bundle,
			boolean flag) {
		Intent intent = new Intent(ctx, cls);
		intent.putExtras(bundle);
		startActivity(intent);
		if (flag == true) {
			finish();
		}
	}

	protected void showMessage(Context ctx, String message) {
		Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
	}

	protected void showMessage(String msg) {
		new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.title_prompt))
				.setIcon(R.drawable.ic_launcher).setMessage(msg).show();
	}

	protected void sureEvent(String ids) {
	}

	protected void cancelEvent() {
	}

	protected void showConfirmMessage(String msg, final String ids, int yes,
			int no) {
		new AlertDialog.Builder(this)
				.setTitle(getResources().getString(R.string.title_prompt))
				.setMessage(msg + "？")
				.setIcon(R.drawable.ic_launcher)
				.setPositiveButton(getResources().getString(R.string.sureButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						setResult(RESULT_OK);// 确定按钮事件
						sureEvent(ids);
					}
				})
				.setNegativeButton(getResources().getString(R.string.cancelButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// 取消按钮事件
						cancelEvent();
					}
				}).show();
	}

	protected void CUSTOM_DISPLAY() {

	}

	protected void CUSTOM_TITLE(int main, int titlebar) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(main);
		CUSTOM_DISPLAY();
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, titlebar);
	}
	
	/**
	 * 分享功能
	 * @param context 上下文
	 * @param activityTitle Activity的名字
	 * @param msgTitle 消息标题
	 * @param msgText 消息内容
	 * @param imgPath 图片路径，不分享图片则传null           
	 */
	protected void shareMsg(String activityTitle, String msgTitle, String msgText, String imgPath) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		if (imgPath == null || imgPath.equals("")) {
			intent.setType("text/plain"); // 纯文本
		} else {
			File f = new File(imgPath);
			if (f != null && f.exists() && f.isFile()) {
				intent.setType("image/jpg");
				Uri u = Uri.fromFile(f);
				intent.putExtra(Intent.EXTRA_STREAM, u);
			}
		}
		intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
		intent.putExtra(Intent.EXTRA_TEXT, msgText);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, activityTitle));
	}	

	@SuppressWarnings("deprecation")
	protected void transparenWindow() {
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay();
		android.view.WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
		p.height = (int) (d.getHeight() * 0.5); // 高度设置为屏幕的1.0
		p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.8
		p.alpha = 1.0f; // 设置本身透明度
		p.dimAmount = 5.0f; // 设置黑暗度
		getWindow().setAttributes(p);
	}

	@SuppressWarnings("deprecation")
	protected Dialog dialogWindow(String msg, int id) {
		Dialog dialog = new Dialog(this);
		if (msg.equals("")) {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
			// 模糊度
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, 
			WindowManager.LayoutParams.FLAG_BLUR_BEHIND); 
			dialog.getWindow().setAttributes(lp);
			lp.alpha=0.5f;//（0.0-1.0）//透明度，黑暗度为lp.dimAmount=1.0f;
		}
		dialog.setTitle(msg);
		dialog.setCancelable(true);
		dialog.setContentView(id);
		dialog.show();
		return dialog;
	}
	
	@SuppressWarnings("deprecation")
	protected Dialog dialogWindow(String msg, int id,int styleId) {
		Dialog dialog = new Dialog(this,styleId);
		if (msg.equals("")) {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
			// 模糊度
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, 
			WindowManager.LayoutParams.FLAG_BLUR_BEHIND); 
			dialog.getWindow().setAttributes(lp);
			lp.alpha=0.5f;//（0.0-1.0）//透明度，黑暗度为lp.dimAmount=1.0f;
		}
		dialog.setTitle(msg);
		dialog.setCancelable(true);
		dialog.setContentView(id);
		dialog.show();
		setProgressBarIndeterminate(true);
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}
	

	protected void onDialogSubmit(View DialogView) {
		// 获取布局中的控件
		/* mUserName = (EditText)DialogView.findViewById(R.id.edit_username); */
	}

	protected void dialog(Context mContext, int rid, String msg) {
		// 动态加载布局生成View对象
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		final View DialogView = layoutInflater.inflate(rid, null);

		// 创建一个AlertDialog对话框
		Dialog dialog = new AlertDialog.Builder(mContext).setTitle(msg)
				.setView(DialogView)
				// 加载自定义的对话框式样
				.setIcon(R.drawable.ic_launcher)
				.setPositiveButton(getResources().getString(R.string.sureButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						setResult(RESULT_OK);// 确定按钮事件
						onDialogSubmit(DialogView);
					}
				}).setNeutralButton(getResources().getString(R.string.cancelButton), (OnClickListener) this).create();
		dialog.show();
	}

	protected void menuDialogEvent(int id) {

	}
	
	protected void menuDialogEvent(String name, int id) {

	}
	
	protected void menuDialogEvent(String value) {

	}
	
	protected void menuDialog(String[] menu, String title) {
		new AlertDialog.Builder(this)
				.setTitle(title)
				.setIcon(R.drawable.ic_launcher)
				.setSingleChoiceItems(menu, 0,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								menuDialogEvent(which);
							}
						}).setNegativeButton(getResources().getString(R.string.cancelButton), null).show();
	}

	protected void menuDialog(String[] menu, final String name, String title) {
		new AlertDialog.Builder(this)
				.setTitle(title)
				.setSingleChoiceItems(menu, 0,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								menuDialogEvent(name, which);
							}
						}).setNegativeButton(getResources().getString(R.string.cancelButton), null).show();
	}
	
	protected ArrayList<HashMap<String, Object>> getSearchSongsArrayListItemAdapterData(String[] songs) {
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		for(int i=0;i<songs.length;i++){
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemId", i);
			map.put("ItemTitle", songs[i].toString());
			listItem.add(map);
		}
		return listItem;
	}

	protected void menuDialog(String title,ArrayList<HashMap<String, Object>> getArrayListItemAdapterData,final String song,final boolean isHide){
		final Dialog dialog =  dialogWindow("\t\t\t\t"+title,R.layout.dialog_item,R.style.MyDialog);
		ListView list = (ListView)  dialog.findViewById(R.id.listView1);
		ArrayList<HashMap<String, Object>> listItem = getArrayListItemAdapterData;
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.activity_list_item_click, new String[] { "ItemId",
						"ItemTitle" }, new int[] { R.id.textView1,
						R.id.textView2 });
		TextView text = (TextView)  dialog.findViewById(R.id.textView1);
		text.setVisibility(View.GONE);
		list.setAdapter(listItemAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				TextView text = (TextView) arg1.findViewById(R.id.textView2);
				menuDialogEvent(text.getText().toString());
				if(isHide==true){
					dialog.hide();
				}
			}

		});		
		
	}
	
	
	protected void onThreadStartDoing() {

	}

	protected void onThreadStart() {

		new Thread(new Runnable() {
			public void run() {
				onThreadStartDoing();
				handler.sendEmptyMessage(0);
			}
		}).start();
	}

	protected void HandlerDoing() {

	}

	protected Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			HandlerDoing();
		}
	};

	protected int paramInt(String name) {
		return getIntent().getIntExtra(name, 0);
	}

	protected String paramString(String name) {
		String keyId = null;
		try {
			keyId = getIntent().getStringExtra(name);
		} catch (Exception e) {
		}
		return keyId;
	}
	
	protected String getResource(int rid){
		return getResources().getString(rid);	
	}
	
	protected String getResource(int rid, Object... formatArgs){
		return getResources().getString(rid,formatArgs);	
	}
	
	protected String getStringSplit(int id){
		return " "+id+" ";
	}
	
	protected String getStringSplit(String s){
		return " " +s+" ";
	}
	
	protected void playAudio(String audioPath) {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse(audioPath), "audio/mp3");
		intent.setComponent(new ComponentName("com.android.music",
				"com.android.music.MediaPlaybackActivity"));
		startActivity(intent);

	}

	boolean stopThread = false;
	private int count = 0;
	Handler mHandler = new ThreadStart();

	public Runnable mRunnable = new Runnable() {

		public void run() {

			while (!stopThread) {
				count++;
				ThreadSleepTime();
				// 虽然Message的构造函数是public的，但是最好是使用Message.obtain(
				// )或Handler.obtainMessage(
				// )函数来获取Message对象，因为Message的实现中包含了回收再利用的机制，可以提供效率。
				Message message = mHandler.obtainMessage();
				message.what = 0;
				message.obj = count;
				mHandler.sendMessage(message);
			}
		}
	};

	protected void HandlerStartDoing() {

	}

	protected class ThreadStart extends Handler {

		public void handleMessage(Message msg) {
			HandlerStartDoing();
		}
	}

	protected void ThreadSleepTime() {
		try {
			Thread.sleep(30000); // 线程暂停10秒，单位毫秒 10000
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected void debug(int id, String msg) {
		System.out.println(id + "---------" + msg);
	}

	protected void onDestroy() {
		System.out.println("-----------onDestroy------");
		stopThread = true;
		super.onDestroy();
	};

	protected void close() {
		this.finish();
		stopThread = true;
		super.onDestroy();
	}
}
