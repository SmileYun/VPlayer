package cc.vplayer;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.R.bool;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.text.StaticLayout;
import android.text.format.DateUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import cc.vplayer.Util.AnimatorBuilder;
import cc.vplayer.adapter.ResultAdapter;
import cc.vplayer.adapter.SerchContentAdapter;
import cc.vplayer.db.dal.LocalContentDB;
import cc.vplayer.model.VedioInfo;
import cc.vplayer.widget.RefreshListView;
import cc.vplayer.widget.RefreshListView.RefreshListener;

public class MainActivity extends Activity /* implementsRefreshListener, */{

	private ArrayList<HashMap<String, Object>> mList;
	private String rootPath;
	private String currentPath;
	private String selectPath;
	private BaseAdapter serchContentAdapter;
	private ArrayList<HashMap<String, Object>> mResultList;
	private PullToRefreshListView mResultListView;
	private Handler mFHHandler;
	private View mFooter;
	private LocalContentDB mLocalContentDB;
	private BaseAdapter mAdapter;

	public static final int LISTVIEW_REFRESH_COMPLETED = 11;
	public static final int VEDIO_SERCH_COMPLETED = 13;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState != null) {
			mResultList = (ArrayList<HashMap<String, Object>>) savedInstanceState.get("result");
		}

		rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		currentPath = rootPath;

		if (mList == null) {
			mList = new ArrayList<HashMap<String, Object>>();
		}

		if (mResultList == null) {
			mResultList = new ArrayList<HashMap<String, Object>>();
		}

		mResultListView = (PullToRefreshListView) findViewById(R.id.play_list);

		mAdapter = new ResultAdapter<HashMap<String, Object>>(mResultList, MainActivity.this);

		mResultListView.setAdapter(mAdapter);
		mResultListView.setOnItemClickListener(new ResultContentListener());

		mFHHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 11:
					mResultListView.onRefreshComplete();
					break;

				case 13:
					setAdapterMap((ArrayList<VedioInfo>) msg.obj);
					mResultListView.setOnItemClickListener(new ResultContentListener());
					mAdapter.notifyDataSetChanged();
					break;
				default:
					break;
				}

			}
		};

		// ((RefreshListView) mResultListView).setOnRefreshListener(this);
		// 正在刷新事件
		mResultListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				new GetDataTask().execute();
			}
		});

		if (mLocalContentDB == null) {
			mLocalContentDB = new LocalContentDB(this);
		}
		// serchVedioAllStorage();
		queryInfoByDB();
	}

	private void scanFileFolder(String path) {
		File f = new File(path);
		File[] files = f.listFiles();
		mList.removeAll(mList);

		if (!currentPath.equals(rootPath)) {
			HashMap<String, Object> _mapb = new HashMap<String, Object>();
			_mapb.put("icon", R.drawable.back);
			_mapb.put("type", "back");
			_mapb.put("name", "(返回上一级)");
			mList.add(_mapb);
		}
		if (files != null && files.length > 0) {
			for (File fFile : files) {

				HashMap<String, Object> _map = new HashMap<String, Object>();
				if (fFile.isDirectory()) {// 是否为文件夹

					_map.put("icon", R.drawable.filefolder);
					_map.put("type", "filefolder");

				} else if (fFile.isFile()) {// 是否为文件
					_map.put("icon", R.drawable.file);
					_map.put("type", "file");
				}
				_map.put("name", fFile.getName());
				_map.put("itemFilePath", fFile.getAbsolutePath());// 保存文件绝对路径
				mList.add(_map);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.player_add_menu:
			View viewScanFile = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_player_dialog, null);
			ListView lvScanFile = (ListView) viewScanFile.findViewById(R.id.add_player_list);
			lvScanFile.setOnItemClickListener(new SerchContentListener());
			scanFileFolder(rootPath);
			serchContentAdapter = new SerchContentAdapter<HashMap<String, Object>>(mList, MainActivity.this);

			lvScanFile.setAdapter(serchContentAdapter);

			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setPositiveButton("确定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// mResultList.removeAll(mResultList);
					mWaitDialog = new ProgressDialog(MainActivity.this);
					mWaitDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Inverse);
					mWaitDialog.setTitle("正在扫描文件...");
					mWaitDialog.setMessage("请稍后...");
					mWaitDialog.show();

					new Thread(new Runnable() {

						@Override
						public void run() {
							getVedioData(currentPath);
							mSerchCompletedHandler.sendEmptyMessage(SERCH_COMPLETED);
						}
					}).start();
				}

			}).setNegativeButton("取消", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).setNeutralButton("全盘搜索", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					serchVedioAllStorage();
				}
			}).setCancelable(false);
			AlertDialog dialog = builder.create();
			dialog.setView(viewScanFile);
			dialog.setTitle("导入");
			dialog.show();
			dialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			android.view.WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
			Display display = getWindowManager().getDefaultDisplay();
			lp.height = (int) (display.getHeight() * 0.85f);
			lp.alpha = 20f;
			lp.gravity = Gravity.CLIP_VERTICAL;
			dialog.getWindow().setAttributes(lp);
			dialog.getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
					android.view.WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 得到该目录下的视频文件
	 */
	private void getVedioData(String path) {

		File _f = new File(path);
		File[] files = _f.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isFile()) {
					if (f.getName().endsWith(".mp4") || f.getName().endsWith(".3gp")) {
						if (isRestored(f.getAbsolutePath()))
							continue;
						HashMap<String, Object> _map = new HashMap<String, Object>();
						_map.put("name", f.getName());
						// _map.put("icon",((BitmapDrawable)getResources().getDrawable(R.drawable.sp)).getBitmap());
						// 获取略缩图
						Bitmap b = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), Thumbnails.MINI_KIND);
						Bitmap resThubBitmap = ThumbnailUtils.extractThumbnail(b, 130, 130,
								ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
						_map.put("icon", resThubBitmap);
						_map.put("itemPath", f.getAbsolutePath());
						mResultList.add(_map);

						VedioInfo _v = new VedioInfo();
						_v.setDisplayName(f.getName());
						_v.setPath(f.getAbsolutePath());
						_v.setThumb(resThubBitmap);
						mLocalContentDB.insertVedioInfo(_v);
					}
				} else if (f.isDirectory()) {
					getVedioData(f.getAbsolutePath());
				}
			}
		}
	}

	ImageView _arrow;
	RotateAnimation _rDown, _rUp;
	AbsListView.LayoutParams lpListView;
	float mDownY = 0f;
	int mMoveY;

	/**
	 * 全盘搜索
	 */
	private void serchVedioAllStorage() {
		Cursor _c = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
		final List<VedioInfo> _list = new ArrayList<VedioInfo>();
		if (_c != null) {
			mResultList.removeAll(mResultList);
			mLocalContentDB.deleteAllInfo();
			while (_c.moveToNext()) {
				VedioInfo _v = new VedioInfo();
				_v.setId(_c.getLong(_c.getColumnIndexOrThrow(MediaStore.Video.Media._ID)));
				_v.setAlbum(_c.getString(_c.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM)));
				_v.setArtist(_c.getString(_c.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST)));
				_v.setDisplayName(_c.getString(_c.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
				_v.setDuration(_c.getInt(_c.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));
				_v.setMimeType(_c.getString(_c.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)));
				String path = _c.getString(_c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
				_v.setPath(path);// path
				_v.setSize(_c.getInt(_c.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)));
				_v.setTitle(_c.getString(_c.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));

				// Bitmap b = ThumbnailUtils.createVideoThumbnail(_v.getPath(),
				// Thumbnails.MINI_KIND);
				// Bitmap resBm = ThumbnailUtils.extractThumbnail(b, 130, 130,
				// ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
				// if (resBm != null) {
				// _v.setThumb(resBm);
				// }else {
				// _v.setThumb(getOptsBitmap(R.drawable.defualt));
				// }

				if (!isRestored(path)) {
					_list.add(_v);
					// mLocalContentDB.insertVedioInfo(_v);
				}
			}

			_c.close();
		}

		Message msg = mFHHandler.obtainMessage(13);
		msg.obj = _list;
		msg.sendToTarget();

		new Thread(new Runnable() {

			@Override
			public void run() {
				int position = 0;
				for (final VedioInfo _v : _list) {
					Bitmap b = ThumbnailUtils.createVideoThumbnail(_v.getPath(), Thumbnails.MINI_KIND);
					Bitmap resBm = ThumbnailUtils.extractThumbnail(b, 130, 130, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
					Map map = mResultList.get(position);

					if (resBm != null) {
						map.put("icon", resBm);
						_v.setThumb(resBm);
					} else {
						map.put("icon", getOptsBitmap(R.drawable.defualt));
					}
					mLocalContentDB.insertVedioInfo(_v);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mAdapter.notifyDataSetChanged();
						}
					});
					position++;
				}
			}
		}).start();
	}

	public void queryInfoByDB() {
		mResultList.removeAll(mResultList);

		ArrayList<VedioInfo> list = mLocalContentDB.queryAllInfo();

		this.setAdapterMap(list);

		mAdapter.notifyDataSetChanged();
	}

	private void setAdapterMap(ArrayList<VedioInfo> list) {
		if (list == null)
			return;
		for (VedioInfo _v : list) {
			if (isRestored(_v.getPath()))
				continue;
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("name", _v.getDisplayName());
			map.put("icon", _v.getThumb());
			map.put("itemPath", _v.getPath());
			mResultList.add(map);
		}
	}
	
	private boolean isRestored(String path) {
		for (HashMap<String, Object> map : mResultList)
			if (((String) map.get("itemPath")).equals(path))
				return true;
		return false;
	}

	/**
	 * 
	 * 添加对话框，item监听器
	 */
	private class SerchContentListener implements OnItemClickListener {

		@SuppressWarnings("unchecked")
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			HashMap<String, Object> _item = (HashMap<String, Object>) parent.getItemAtPosition(position);

			selectPath = (String) _item.get("name");
			String _type = (String) _item.get("type");
			if ("back".equals(_type)) {
				currentPath = new File(currentPath).getParent();
			} else if ("filefolder".equals(_type)) {
				currentPath = (String) _item.get("itemFilePath");
			}
			scanFileFolder(currentPath);

			serchContentAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 
	 * 视频文件item监听器
	 */
	private class ResultContentListener implements OnItemClickListener {

		@SuppressWarnings("unchecked")
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			HashMap<String, Object> _item = (HashMap<String, Object>) parent.getItemAtPosition(position);
			if (_item != null) {
				Intent intent = new Intent(MainActivity.this, PlayUI.class);
				String path = (String) _item.get("itemPath");
				String title = (String) _item.get("name");
				intent.putExtra("path", path);
				intent.putExtra("title", title);
				intent.putExtra("list", mResultList);
				intent.putExtra("postion", position);
				startActivity(intent);
			} else {
			}
		}
	}

	private static final int SERCH_COMPLETED = 0;
	private ProgressDialog mWaitDialog;
	Handler mSerchCompletedHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SERCH_COMPLETED:
				mResultListView.setOnItemClickListener(new ResultContentListener());

				mWaitDialog.dismiss();

				// mResultListView.setAdapter(mAdapter);
				mAdapter.notifyDataSetChanged();

				break;
			}
		};
	};

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelableArrayList("result", (ArrayList<? extends Parcelable>) mResultList);
		super.onSaveInstanceState(outState);
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			serchVedioAllStorage();
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			mAdapter.notifyDataSetChanged();
			// Call onRefreshComplete when the list has been refreshed.
			mResultListView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}

	/** 获取优化位图 */
	private Bitmap getOptsBitmap(int resId) {
		InputStream _in = this.getResources().openRawResource(resId);
		BitmapFactory.Options _opts = new Options();
		_opts.inInputShareable = true;
		// _opts.inSampleSize = 2;
		_opts.inPurgeable = true;
		Bitmap _map = BitmapFactory.decodeStream(_in, null, _opts);
		return _map;
	}
}
