package com.life.music;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.life.music.R.id;
import com.life.music.util.Cfg;
import com.life.music.util.FileSearcher;
import com.life.music.util.SeachlrcEncode;
import com.life.music.util.SearchInfo;
import com.life.music.widget.LyricView;

public class PlayActivity extends BaseActivity {

	private LyricView lyricView;
	public static MediaPlayer mediaPlayer;
	public List<String> musicList; // 存放找到的所有mp3的绝对路径。
	public int songNum; // 当前播放的歌曲在List中的下标
	public static String songName; // 当前播放的歌曲名

	private Button button;
	private Button previous;
	private Button next;
	private Button down;
	
	private Button addsrc;
	private Button deles;
	private Button searlrc;
	
	private SeekBar seekAudio;
	private SeekBar seekBar;
	private String mp3Path;
	private int INTERVAL = 45;// 歌词每行的间隔
	private Long time;

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plays);
		init();
		new Thread(new runable()).start();
	}

	public void init() {
		int id = Cfg.stringToInt(paramString("num"));
		try {
			stop();
			next();
		} catch (Exception e) {
			songNum = id;
			start();
		}
	}

	public void stop() {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}

	}

	private void startButton() {
		if (mediaPlayer.isPlaying()) {
			button.setText("▶");
		} else {
			button.setText("| |");
		}
	}

	public void start() {
		musicList = new ArrayList<String>();
		String[] s = SearchInfo.songs();
		for (int i = 0; i < s.length; i++) {
			musicList.add(SearchInfo.MP3 + s[i]);
		}
		lyricView = (LyricView) findViewById(R.id.mylrc);
		mediaPlayer = new MediaPlayer();

		String dataSource = musicList.get(songNum);// 得到当前播放音乐的路径
		songName = s[songNum];
		songName = SearchInfo.musicFormat(songName);
		mp3Path = dataSource;
		ResetMusic(mp3Path);
		SerchLrc();
		lyricView.SetTextSize();
		
		addsrc = (Button) findViewById(R.id.button_add);
		addsrc.setText("+");
		deles = (Button) findViewById(R.id.button_dele);
		deles.setText("-");
		searlrc = (Button) findViewById(R.id.button_lrc);
		searlrc.setText("$");
		
		previous = (Button) findViewById(R.id.button_previous);
		previous.setText("←");
		next = (Button) findViewById(R.id.button_next);
		next.setText("→");
		down = (Button) findViewById(R.id.button_down);
		down.setText("↓");
		button = (Button) findViewById(R.id.button);
		startButton();
		seekBar = (SeekBar) findViewById(R.id.seekbarmusic);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					mediaPlayer.seekTo(progress);
					lyricView.setOffsetY(220 - lyricView.SelectIndex(progress)
							* (lyricView.getSIZEWORD() + INTERVAL - 1));
				}
			}
		});		
		seekAudio = (SeekBar) findViewById(R.id.seekbarAudio);
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
		current = (100/16)*current;
		seekAudio.setProgress(current);
		seekAudio.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				
					int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC ); 
					
					progress = (int) (progress*(max*0.01));
					
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_VIBRATE);
					
				}
			}
		});

		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mediaPlayer.isPlaying()) {
					button.setText("▶"); // ▷▶ ||
					mediaPlayer.pause();
				} else {
					button.setText("| |");
					mediaPlayer.start();
					lyricView.setOffsetY(220
							- lyricView.SelectIndex(mediaPlayer
									.getCurrentPosition())
							* (lyricView.getSIZEWORD() + INTERVAL - 1));
				}
			}
		});

		previous.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				previous();
			}
		});

		next.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				mediaPlayer.stop();
				next();
			}
		});
		
		addsrc.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				onEdit();
			}
		});
		
		deles.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				onDelete();
			}
		});
		
		searlrc.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Uri uri = Uri.parse("https://www.baidu.com/s?f=8&rsv_bp=0&rsv_idx=1&tn=baidu&wd="+songName+"歌词 lrc");
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});
		
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						next();
					}
				});
		seekBar.setMax(mediaPlayer.getDuration());
		mediaPlayer.start();
		showLRCStatus();
	}

	private final void showLRCStatus() {
		String lrc = SearchInfo.LRC + songName + ".lrc".trim();
		boolean flag = SearchInfo.FileIsExist(lrc);
		Button down = (Button) findViewById(R.id.button_down);
		if (flag == false) {
			onLrcEvent();
		} else if (lyricView.getSIZEWORD() == 10) {
			onLrcEvent();
		} else {
			onLrcEvent();
			down.setVisibility(View.VISIBLE);
		}
	}

	private void onLrcEvent() {
		down.setVisibility(View.VISIBLE);
		down.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				downMessageMenuWindow();
			}
		});

	}
	
	private void downMessageMenuWindow() {
		onDown();
	}

	private void onDown() {
		final Dialog dialog = dialogWindow(Cfg.WINDOW_TITLE_BR+getResource(R.string.title_name), R.layout.activity_search,R.style.MyDialog);
		final EditText editText1 = (EditText) dialog.findViewById(id.editText1);
		final EditText editText2 = (EditText) dialog.findViewById(id.editText2);
		final Button button1 = (Button) dialog.findViewById(id.button1);
		final Button button2 = (Button) dialog.findViewById(id.button2);

		editText1.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				String song = editText1.getText().toString().trim();
				if (SearchInfo.checkSongExist(song) == true) {
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
				if (SearchInfo.checkSongExist(song) == true) {
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
						Button btnsong = (Button) findViewById(id.button_previous);
						Button btnsinger = (Button) findViewById(id.button_next);
						btnsong.setTag(song);
						btnsinger.setTag(singer);
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
				if (editText1.getText().toString()
						.equals(editText2.getText().toString())) {
					dialog.hide();
				} else {
					editText1.setText("");
					editText2.setText("");
				}
			}
		});
		
	}
	
	public void menuDialogEvent(String song) {
		int i =  FileSearcher.getSongId(song);
		songNum = i;
		reset();
		start();
		mediaPlayer.start();
	}
	

	public void onUpdateReloadData() {
		onThreadStart();
	}

	
	public void onThreadStartDoing() {
		Button btnsong = (Button) findViewById(id.button_previous);
		Button btnsinger = (Button) findViewById(id.button_next);

		String song = btnsong.getTag().toString().trim();
		String singer = btnsinger.getTag().toString().trim();

		SearchInfo.search(song, singer);
	}

	private boolean status() {
		Button btnsong = (Button) findViewById(id.button_previous);
		String song = btnsong.getTag().toString().trim();
		boolean flag = false;
		if (SearchInfo.checkSongExist(song) == true) {
			flag = true;
		}
		return flag;
	}

	private void isPlay(String msg, final String song) {
		new AlertDialog.Builder(this)
				.setTitle(getResource(R.string.title_prompt))
				.setMessage(msg + "？")
				.setIcon(R.drawable.ic_launcher)
				.setPositiveButton(getResource(R.string.sureButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						setResult(RESULT_OK);
						// 确定按钮事件
						reset();
						int i =  FileSearcher.getSongId(song);
						songNum = i;
						start();
						mediaPlayer.start();
					}
				})
				.setNegativeButton(getResource(R.string.cancelButton), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// 取消按钮事件
					}
				}).show();
	}

	public void HandlerDoing() {
		Button btnsong = (Button) findViewById(id.button_previous);
		String song = btnsong.getTag().toString().trim();
		boolean flag = status();
		String msg = "";
		if (flag == false) {
			msg = getResource(R.string.title_available_download);
			showMessage(ctx, msg);
		} else {
			isPlay(getResource(R.string.title_download_successful), song);
		}
	}
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")	
	public void onCoppy() {
		final ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		clipBoard.setText(songName);
		showMessage(ctx, songName+"\t"+getResource(R.string.title_copied));
	}
	
	public void onShare() {
		shareMsg(songName,getResource(R.string.title_File),"",mp3Path);
	}
	
	public void onEdit() {
		final Dialog dialog = dialogWindow(getResource(R.string.title_lrc_content), R.layout.dialog_textare,R.style.MyDialog);
		final EditText editTextContent = (EditText) dialog
				.findViewById(R.id.editTextContent);
		final String path = SearchInfo.LRC + songName + ".lrc";
		String str = SearchInfo.readFile(path, "\n");
		editTextContent.setText(str);
		Button save = (Button) dialog.findViewById(R.id.button1);
		save.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				EditText editTextContent = (EditText) dialog
						.findViewById(R.id.editTextContent);
				if (!editTextContent.getText().toString().trim().equals("")) {
					String txt = editTextContent.getText().toString().trim();
					SearchInfo.create(path, txt);
					String msg = getResource(R.string.title_successful_operation);
					setTime(null);
					showMessage(ctx, msg);
					dialog.hide();
				}
			}
		});
		final Button top = (Button) dialog.findViewById(R.id.button2);
		top.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				EditText editTextContent = (EditText) dialog
						.findViewById(R.id.editTextContent);
				CharSequence text = editTextContent.getText();
				Spannable spanText = (Spannable) text;
				if (top.getText().toString().endsWith(getResource(R.string.LineTopButton))) {
					Selection.setSelection(spanText, 0);
					top.setText(getResource(R.string.LineCenterButton));
				} else if (top.getText().toString().endsWith(getResource(R.string.LineCenterButton))) {
					int a = text.length();
					int b = 2;
					int c = a % b == 0 ? a / b : a / b + 1;
					Selection.setSelection(spanText, c);
					top.setText(getResource(R.string.LineBottomButton));
				} else {
					Selection.setSelection(spanText, text.length());
					top.setText(getResource(R.string.LineTopButton));
				}
				hideEventSoftInput(editTextContent);
			}
		});
		final Button bottom = (Button) dialog.findViewById(R.id.button3);
		bottom.setText(getResource(R.string.backButton));
		bottom.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				setTime(null);
				dialog.hide();
			}
		});
		final Button butlrc = (Button) dialog.findViewById(R.id.button4);
		butlrc.setText(getResource(R.string.LrcButton));
		setTime(System.currentTimeMillis());
		butlrc.setOnClickListener(new OnClickListener() {
			@SuppressLint("SimpleDateFormat")
			public void onClick(View arg0) {
				  Date date=new Date();
				  DateFormat format=new SimpleDateFormat("mm:ss.SS");
				  String time=format.format(date);
				  if(getTime()==null){
					  time = "00:00.00";
					  setTime(System.currentTimeMillis());
				  }else{
					  Long l = System.currentTimeMillis();
					  l = l-getTime()+2000;
					  String hms = format.format(l); 
					  time = hms.substring(0, 8);
				  }
				  time = "["+time+"]";
				  EditText editTextContent = (EditText) dialog.findViewById(R.id.editTextContent);
				  int index = editTextContent.getSelectionStart();  
				  Editable editable = editTextContent.getText();  
				  editable.insert(index, time); 
			}
		});
		final Button butcls = (Button) dialog.findViewById(R.id.button5);
		butcls.setText(getResource(R.string.clsButton));
		butcls.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				EditText editTextContent = (EditText) dialog
						.findViewById(R.id.editTextContent);
				String msg = editTextContent.getText().toString();
				List<String> list = FileSearcher.extractConentByRegular(msg);
				for (int i = 0; i < list.size(); i++) {
					String ss = "[" + list.get(i) + "]";
					msg = msg.replace(ss, "");
				}
				editTextContent.setText(msg);
			}
		});			
	}

	public void sureEvent(String ids) {
		String path = "";
		String extensions = "";
		String songs[] = SearchInfo.musicFormat();
		for (int i = 0; i < songs.length; i++) {
			extensions = songs[i];
			path = ids + extensions;
			SearchInfo.deleteFile(path);
		}
		next();
	}

	public void onDelete() {
		String path = SearchInfo.MP3 + songName;
		showConfirmMessage(getResource(R.string.deleButton)+" " + songName, path, 1, 1);
	}

	public void onEdit(final String songlrcName) {
		final Dialog dialog = dialogWindow(getResource(R.string.title_lrc_content), R.layout.dialog_textare,R.style.MyDialog);
		final EditText editTextContent = (EditText) dialog
				.findViewById(R.id.editTextContent);
		final String path = SearchInfo.LRC + songlrcName;
		String str = SearchInfo.readFile(path, "\n");
		editTextContent.setText(str);
		Button save = (Button) dialog.findViewById(R.id.button1);
		save.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				EditText editTextContent = (EditText) dialog
						.findViewById(R.id.editTextContent);
				if (!editTextContent.getText().toString().trim().equals("")) {
					String txt = editTextContent.getText().toString().trim();
					boolean flag = SeachlrcEncode.checkStrLrc(txt);
					String msg = "";
					if (flag == true) {
						SearchInfo.create(path, txt);
						msg = getResource(R.string.title_successful_operation);
					} else {
						// msg = "不完整的LRC文件！";
						String songName = songlrcName.replace(".lrc", "");
						txt = SeachlrcEncode.autoAddTitle(songName, txt);
						SearchInfo.create(path, txt);
						msg = getResource(R.string.title_successful_operation);
					}
					showMessage(ctx, msg);
					dialog.hide();
				}
			}
		});
		final Button top = (Button) dialog.findViewById(R.id.button2);
		top.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				EditText editTextContent = (EditText) dialog
						.findViewById(R.id.editTextContent);
				CharSequence text = editTextContent.getText();
				Spannable spanText = (Spannable) text;
				if (top.getText().toString().endsWith(getResource(R.string.LineTopButton))) {
					Selection.setSelection(spanText, 0);
					top.setText(getResource(R.string.LineCenterButton));
				} else if (top.getText().toString().endsWith(getResource(R.string.LineCenterButton))) {
					int a = text.length();
					int b = 2;
					int c = a % b == 0 ? a / b : a / b + 1;
					Selection.setSelection(spanText, c);
					top.setText(getResource(R.string.LineBottomButton));
				} else {
					Selection.setSelection(spanText, text.length());
					top.setText(getResource(R.string.LineTopButton));
				}
				hideEventSoftInput(editTextContent);
			}
		});
		final Button bottom = (Button) dialog.findViewById(R.id.button3);
		bottom.setText(getResource(R.string.cancelButton));
		bottom.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				dialog.hide();
			}
		});
		final Button butlrc = (Button) dialog.findViewById(R.id.button4);
		butlrc.setText(getResource(R.string.LrcButton));
		setTime(System.currentTimeMillis());
		butlrc.setOnClickListener(new OnClickListener() {
			@SuppressLint("SimpleDateFormat")
			public void onClick(View arg0) {
				  Date date=new Date();
				  DateFormat format=new SimpleDateFormat("mm:ss.SS");
				  String time=format.format(date);
				  if(getTime()==null){
					  time = "00:00.00";
					  setTime(System.currentTimeMillis());
				  }else{
					  Long l = System.currentTimeMillis();
					  l = l-getTime()+2500;
					  String hms = format.format(l); 
					  time = hms.substring(0, 8);
				  }
				  time = "["+time+"]";
				  EditText editTextContent = (EditText) dialog.findViewById(R.id.editTextContent);
				  int index = editTextContent.getSelectionStart();  
				  Editable editable = editTextContent.getText();  
				  editable.insert(index, time); 
			}
		});	
		final Button butcls = (Button) dialog.findViewById(R.id.button5);
		butcls.setText(getResource(R.string.clsButton));
		butcls.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				EditText editTextContent = (EditText) dialog
						.findViewById(R.id.editTextContent);
				String msg = editTextContent.getText().toString();
				List<String> list = FileSearcher.extractConentByRegular(msg);
				for (int i = 0; i < list.size(); i++) {
					String ss = "[" + list.get(i) + "]";
					msg = msg.replace(ss, "");
				}
				editTextContent.setText(msg);
			}
		});			
	}

	private String[] lrcItems() {
		String[] s = SearchInfo.lrcs();
		return s;
	}

	private void onLrcs() {
		final Dialog dialog = dialogWindow(getResource(R.string.title_activity_song), R.layout.dialog_select,R.style.MyDialog);
		final ListView listView = (ListView) dialog.findViewById(R.id.listview);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_multiple_choice,
				this.lrcItems());
		listView.setAdapter(adapter);
		listView.setItemsCanFocus(false);

		final Button button1 = (Button) dialog.findViewById(R.id.button1);
		Button button2 = (Button) dialog.findViewById(R.id.button2);
		Button button3 = (Button) dialog.findViewById(R.id.button3);
		Button button4 = (Button) dialog.findViewById(R.id.button4);

		button1.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				int count = lrcItems().length;
				String s = button1.getText().toString();
				if (count > 0) {
					if (s.equals(getResource(R.string.selectAllButton))) {
						for (int i = 0; i < count; i++) {
							listView.setItemChecked(i, true);
						}
						button1.setText(R.string.cancelButton);
					} else {
						for (int i = 0; i < count; i++) {
							listView.setItemChecked(i, false);
						}
						button1.setText(R.string.selectAllButton);
					}
				}
			}
		});

		button2.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				long[] items = getListSelectededItemIds(listView);
				String name = "";
				String ids = "";
				String value;
				String message;
				if (items.length > 0) {
					for (int i = 0; i < items.length; i++) {
						name += lrcItems()[(int) items[i]] + ",";
						ids += (int) items[i] + ",";
					}
					name = name.substring(0, name.length() - 1);
					value = ids.substring(0, ids.length() - 1);
					message = name.substring(0);
					String lrc = viewLrc(value);
					onEdit(lrc);
				} else {
					message = getResource(R.string.title_must_select_one);
					showMessage(ctx, message);
				}
			}
		});

		button3.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				long[] items = getListSelectededItemIds(listView);
				String name = "";
				String ids = "";
				String value;
				String message;
				if (items.length > 0) {
					for (int i = 0; i < items.length; i++) {
						name += lrcItems()[(int) items[i]] + ",";
						ids += (int) items[i] + ",";
					}
					name = name.substring(0, name.length() - 1);
					value = ids.substring(0, ids.length() - 1);
					message = name.substring(0);
					deleLrc(value);
					dialog.hide();
				} else {
					message = getResource(R.string.title_must_select_one);
					showMessage(ctx, message);
				}
			}
		});
		
		button4.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				dialog.hide();
			}
		});
	}

	private String viewLrc(String ids) {
		String[] title = ids.split(",");
		String src = null;
		String[] lrc = SearchInfo.lrcs();

		int id = Cfg.stringToInt(title[0].toString());
		src = lrc[id];
		String path = src;
		return path;
	}

	private void deleLrc(String ids) {
		String[] title = ids.split(",");
		String src = null;
		String[] lrc = SearchInfo.lrcs();
		for (int i = 0; i < title.length; i++) {
			int id = Cfg.stringToInt(title[i].toString());
			src = lrc[id];
			String path = SearchInfo.LRC + src;
			SearchInfo.deleteFile(path);
		}
		showMessage(ctx, getResource(R.string.title_successful_operation));
	}

	private long[] getListSelectededItemIds(ListView listView) {
		long[] ids = new long[listView.getCount()];
		int checkedTotal = 0;
		for (int i = 0; i < listView.getCount(); i++) {
			if (listView.isItemChecked(i)) {
				ids[checkedTotal++] = i;
			}
		}
		if (checkedTotal < listView.getCount()) {
			final long[] selectedIds = new long[checkedTotal];
			System.arraycopy(ids, 0, selectedIds, 0, checkedTotal);
			return selectedIds;
		} else {
			return ids;
		}
	}

	private void hideEventSoftInput(EditText editTextContent) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editTextContent.getWindowToken(), 0);
	}

	private String path() {
		String root = SearchInfo.MUSIC_PATH + "lrc.log";
		return SearchInfo.read(root);
	}

	public String[] lrcs() {
		String songinfo = SearchInfo.getDirectoryFile(path());
		return songinfo.split("\n");
	}

	public void SerchLrc() {
		String lrc = mp3Path;
		lrc = SearchInfo.LRC + songName + ".lrc".trim();
		lyricView.read(songName.trim(),lrc);
		lyricView.SetTextSize();
		lyricView.setOffsetY(200);
	}

	public void ResetMusic(String path) {
		mediaPlayer.reset();
		try {
			mediaPlayer.setDataSource(mp3Path);
			mediaPlayer.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void reset() {
		String mp3Path = musicList.get(songNum);
		ResetMusic(mp3Path);
		lyricView.SetTextSize();
		lyricView.setOffsetY(400);
	}

	public void previous() {
		mediaPlayer.stop();
		if (songNum != 0) {
			songNum = songNum - 1;
		}
		reset();
		start();
		mediaPlayer.start();
	}

	public void next() {
		songNum = songNum == musicList.size() - 1 ? 0 : songNum + 1;
		reset();
		start();
		mediaPlayer.start();
	}

	class runable implements Runnable {

		public void run() {
			while (true) {
				try {
					Thread.sleep(100);
					if (mediaPlayer.isPlaying()) {
						lyricView.setOffsetY(lyricView.getOffsetY()
								- lyricView.SpeedLrc());
						lyricView.SelectIndex(mediaPlayer.getCurrentPosition());
						seekBar.setProgress(mediaPlayer.getCurrentPosition());
						mHandler.post(mUpdateResults);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	Handler mHandler = new Handler();
	Runnable mUpdateResults = new Runnable() {
		public void run() {
			lyricView.invalidate(); // 更新视图
		}
	};

	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
		if ((paramInt == 4)) {
			close();
			try {
				activity(this, SongsActivity.class);
				android.os.Process.killProcess(android.os.Process.myPid());
			} catch (Exception e) {
			}
			return true;
		}
		return false;
	}
}