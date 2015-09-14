package cc.vplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cc.vplayer.widget.LightView;
import cc.vplayer.widget.MyVideoView;
import cc.vplayer.widget.SoundView;
import cc.vplayer.widget.LightView.OnLightChangeListener;
import cc.vplayer.widget.SoundView.OnVolumeChangeListener;

public class PlayUI extends Activity implements OnCompletionListener, OnErrorListener, OnInfoListener, OnPreparedListener,
		OnSeekCompleteListener, OnVideoSizeChangedListener {

	private View controlView;
	private PopupWindow controler;
	private TextView durationTextView;
	private TextView playedTextView;
	private ImageButton btn1, btn2, btn3, btn4, btn5;
	private MyVideoView vv;// mSurfaceView
	private int screenWidth = 0;
	/**
	 * popupwindow �����߶�
	 */
	private int controlHeight = 0;
	private int playTitleHeight = 0;
	private boolean isControllerShow;
	private Display mDisplay;
	private int mVideoWidth;
	private int mVideoHeight;
	private SeekBar mSeekBar;
	private boolean isPaused = false;
	private SoundView mSoundView;
	private PopupWindow mSoundPopupWindow;
	private boolean isSoundPopShow = false;
	private AudioManager mAudioManager;
	private LightView mLightView;
	private PopupWindow mLightPopupWindow;
	private boolean isLightPopShow = false;
	private View playTitleView;
	private PopupWindow playTitle;
	private TextView playTitleTX;
	private ImageButton imgbtnCoss;
	private ArrayList<HashMap<String, Object>> mResList;
	private static int mPostion = 0;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.play_layout);
		mDisplay = getWindowManager().getDefaultDisplay();
		// TODO getWindow().addFlags(PowerManager.SCREEN_BRIGHT_WAKE_LOCK);
		// ��ʼ����Ļ����
//		imgBtnClick(null);
		mResList = (ArrayList<HashMap<String, Object>>) getIntent().getSerializableExtra("list");
		// mPostion = getIntent().getIntExtra("postion", 0);
		doInit();
	}

	private void doInit() {
		// ������ͼ
		controlView = getLayoutInflater().inflate(R.layout.controler, null);
		controler = new PopupWindow(controlView);
		controler.setAnimationStyle(R.style.popuwin_sty);

		durationTextView = (TextView) controlView.findViewById(R.id.duration);
		playedTextView = (TextView) controlView.findViewById(R.id.has_played);

		// ������ͼ
		playTitleView = getLayoutInflater().inflate(R.layout.play_title_layout, null);
		playTitle = new PopupWindow(playTitleView);
		playTitle.setAnimationStyle(R.style.popuwin_title_sty);

		playTitleTX = (TextView) playTitleView.findViewById(R.id.playTile);
		playTitleTX.setText(getIntent().getStringExtra("title"));

		imgbtnCoss = (ImageButton) playTitleView.findViewById(R.id.coss);
		imgbtnCoss.setOnClickListener(onClickListener);

		// ����
		btn1 = (ImageButton) controlView.findViewById(R.id.button1);
		btn1.setOnClickListener(onClickListener);

		// ��һ��
		btn2 = (ImageButton) controlView.findViewById(R.id.button2);
		btn2.setOnClickListener(onClickListener);

		// ����
		btn3 = (ImageButton) controlView.findViewById(R.id.button3);
		btn4 = (ImageButton) controlView.findViewById(R.id.button4);
		btn4.setOnClickListener(onClickListener);
		btn5 = (ImageButton) controlView.findViewById(R.id.button5);

		vv = (MyVideoView) findViewById(R.id.playUI);

		Uri uri = getIntent().getData();

		if (uri != null) {
			// if(vv.getVideoHeight()==0){
			vv.setMediaUri(uri);
			// }
			btn3.setImageResource(R.drawable.pause);
		} else {
			btn3.setImageResource(R.drawable.play);
		}

		btn3.setOnClickListener(onClickListener);

		// ����view
		mSoundView = new SoundView(this);
		mSoundView.setOnVolumeChangeListener(new OnVolumeChangeListener() {

			@Override
			public void volumeChange(int index) {
				myHandler.removeMessages(HIDE_SOUND_VIEW);
				myHandler.removeMessages(HIDE_LIGHT_VIEW);
				myHandler.removeMessages(HIDE_CONTROLER);
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

				myHandler.sendEmptyMessageDelayed(HIDE_SOUND_VIEW, 2500);
				myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
				myHandler.removeMessages(HIDE_LIGHT_VIEW, 2500);
			}
		});
		mSoundPopupWindow = new PopupWindow(mSoundView);

		// ������ť
		btn5.setOnClickListener(onClickListener);

		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		getScreenSize();

		int _initLight = (int) (this.getWindow().getAttributes().screenBrightness * 15);
		// ����view
		mLightView = new LightView(PlayUI.this, _initLight);
		mLightView.setOnLightChangeListener(new OnLightChangeListener() {

			@Override
			public void lightChange(int index) {

				// �ı�����
				myHandler.removeMessages(HIDE_SOUND_VIEW);
				myHandler.removeMessages(HIDE_LIGHT_VIEW);
				myHandler.removeMessages(HIDE_CONTROLER);
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.screenBrightness = index / 15.0f;
				getWindow().setAttributes(lp);
				myHandler.sendEmptyMessageDelayed(HIDE_SOUND_VIEW, 2500);
				myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
				myHandler.sendEmptyMessageDelayed(HIDE_LIGHT_VIEW, 2500);
			}
		});
		mLightPopupWindow = new PopupWindow(mLightView);

		// �϶���
		mSeekBar = (SeekBar) controlView.findViewById(R.id.seekbar);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				myHandler.removeMessages(HIDE_CONTROLER);
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					vv.seekTo(progress);
				}
			}
		});

		// ע�������
		vv.setOnPreparedListener(this);
		vv.setOnCompletionListener(this);
		vv.setOnErrorListener(this);
		vv.setOnCompletionListener(this);
		vv.setOnSizeChangedListener(this);

		vv.setMediaUri(getIntent().getStringExtra("path"));
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.coss: // ImageBtn ��ת
				imgBtnClick(v);
				break;
			case R.id.button1: // ����btn
				button1Click(v);
				break;
			case R.id.button2:// ��һ��
				button2Click(v);
				break;
			case R.id.button3: // ����
				button3Click(v);
				break;
			case R.id.button4: // ��һ��
				button4Click(v);
				break;
			case R.id.button5:// ����
				button5Click(v);
				break;
			}
		}
	};

	/**
	 * ��Ļ��ת����
	 * 
	 * @param v
	 */
	private void imgBtnClick(View v) {
		switch (PlayUI.this.getRequestedOrientation()) {
		case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
			PlayUI.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			// convertScreen();
			break;
		case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
			PlayUI.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			convertScreen();
			break;
		// case ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED:
		default:
			PlayUI.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			// convertScreen();
			break;
		}
	}

	/**
	 * ��������BTN
	 * 
	 * @param v
	 */
	private void button1Click(View v) {
		if (!isLightPopShow) {
			mLightPopupWindow.showAtLocation(vv, Gravity.LEFT | Gravity.TOP, 0, 50);
			mLightPopupWindow.update(20, 130, LightView.MY_WIDTH, LightView.MY_HEIGHT);
			isLightPopShow = true;
		} else {
			isLightPopShow = false;
			mLightPopupWindow.dismiss();
		}
	}

	/**
	 * ��һ��BTN
	 * 
	 * @param v
	 */
	private void button2Click(View v) {
		int count = mResList.size();
		vv.stopPlayback();
		int nowPostion = Math.abs(mPostion - 1);
		System.out.println("+++++" + (mResList.get(nowPostion % count)).get("itemPath"));
		vv.setMediaUri((String) (mResList.get(nowPostion % count)).get("itemPath"));

		mPostion = nowPostion;
	}

	/**
	 * ����/��ͣBTN
	 * 
	 * @param v
	 */
	private void button3Click(View v) {
		if (isPaused) {
			vv.start();
			btn3.setImageResource(R.drawable.pause);
			hideControllerDelay();
		} else {
			vv.pause();
			btn3.setImageResource(R.drawable.play);
		}
		isPaused = !isPaused;
	}

	/**
	 * ��һ��BTN
	 * 
	 * @param v
	 */
	private void button4Click(View v) {
		int count = mResList.size();
		vv.stopPlayback();
		mPostion += 1;
		int nowPostion = Math.abs(mPostion) % count;
		System.out.println(mPostion + "+++++" + nowPostion);
		vv.setMediaUri((String) (mResList.get(nowPostion)).get("itemPath"));
		mPostion = nowPostion;
	}

	/**
	 * ��������BTN
	 * 
	 * @param v
	 */
	private void button5Click(View v) {
		if (!isSoundPopShow) {
			mSoundPopupWindow.showAtLocation(vv, Gravity.RIGHT | Gravity.TOP, 0, 50);
			mSoundPopupWindow.update(20, 130, SoundView.MY_WIDTH, SoundView.MY_HEIGHT);
			isSoundPopShow = true;
		} else {
			isSoundPopShow = false;
			mSoundPopupWindow.dismiss();
		}
	}

	private final static int PROGRESS_CHANGED = 0;
	private final static int HIDE_CONTROLER = 1;
	private final static int HIDE_SOUND_VIEW = 2;
	private final static int HIDE_LIGHT_VIEW = 3;

	private final static int TIME = 6868;
	Handler myHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PROGRESS_CHANGED:

				int i = vv.getCurrentPosition();
				mSeekBar.setProgress(i);

				i /= 1000;
				int minute = i / 60;
				int hour = minute / 60;
				int second = i % 60;
				minute %= 60;
				playedTextView.setText(String.format("%02d:%02d:%02d", hour, minute, second));

				sendEmptyMessage(PROGRESS_CHANGED);
				break;
			case HIDE_CONTROLER:
				hideController();
				break;
			case HIDE_SOUND_VIEW:
				mSoundPopupWindow.dismiss();
				break;
			case HIDE_LIGHT_VIEW:
				mLightPopupWindow.dismiss();
				break;
			}
		};
	};

	private void showController() {
		controler.showAtLocation(vv, Gravity.BOTTOM, 0, 0);
		controler.update(0, 0, screenWidth, controlHeight);
		playTitle.showAtLocation(vv, Gravity.TOP, 0, 0);
		playTitle.update(0, 0, screenWidth, playTitleHeight);

		isControllerShow = true;
	}

	private void hideController() {
		if (controler.isShowing()) {
			// controler.update(0,0,screenWidth, 0);
			controler.dismiss();
			playTitle.dismiss();
			mSoundPopupWindow.dismiss();
			mLightPopupWindow.dismiss();
			isControllerShow = false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (!isControllerShow) {
				showController();
				hideControllerDelay();
			} else {
				hideController();
			}
		}
		return super.onTouchEvent(event);
	}

	/**
	 * popupwindow ������С
	 */
	private void getScreenSize() {
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			screenWidth = mDisplay.getWidth();
			controlHeight = mDisplay.getHeight() / 4;
			playTitleHeight = mDisplay.getHeight() / 9;
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			screenWidth = mDisplay.getWidth();
			controlHeight = mDisplay.getHeight() / 5;
			playTitleHeight = mDisplay.getHeight() / 12;
		}
	}

	@Override
	protected void onDestroy() {
		if (controler.isShowing()) {
			playTitle.dismiss();
			controler.dismiss();
			mSoundPopupWindow.dismiss();
			mLightPopupWindow.dismiss();
		}
		// ȡ��seekbar����
		myHandler.removeMessages(PROGRESS_CHANGED);
		myHandler.removeMessages(HIDE_CONTROLER);
		isNeedRotateScreen = true;
		super.onDestroy();
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {

	}

	/**
	 * ׼�����������˴������ڶ�̬����SurfaceView�Ŀ�͸�!!! MediaPlayer.OnPreparedListener�ӿ�
	 */
	@Override
	public void onPrepared(MediaPlayer mp) {
		mVideoWidth = mp.getVideoWidth();
		mVideoHeight = mp.getVideoHeight();
		// ���MediaPlayer�Ŀ������Ļ�� ���� �߶ȴ�����Ļ�߶�
		if (mVideoWidth > mDisplay.getWidth() || mVideoHeight > mDisplay.getHeight()) {
			// mediaPlayer��/��Ļ��
			float heightRatio = (float) mVideoHeight / (float) mDisplay.getHeight();
			float widthRatio = (float) mVideoWidth / (float) mDisplay.getWidth();

			if (heightRatio > 1 || widthRatio > 1) {

				if (heightRatio > widthRatio) {
					mVideoHeight = (int) Math.ceil((float) mVideoHeight / (float) heightRatio);
					mVideoWidth = (int) Math.ceil((float) mVideoWidth / (float) heightRatio);
				} else {
					mVideoHeight = (int) Math.ceil((float) mVideoHeight / (float) widthRatio);
					mVideoWidth = (int) Math.ceil((float) mVideoWidth / (float) widthRatio);
				}
			}
		}
		vv.setmIsPrepared(true);
		int i = vv.getDuration();
		mSeekBar.setMax(i);

		i /= 1000;
		int minute = i / 60;
		int hour = minute / 60;
		int second = i % 60;
		durationTextView.setText(String.format("%02d:%02d:%02d", hour, minute, second));
		vv.start();
		btn3.setImageResource(R.drawable.pause);
		hideControllerDelay();
		myHandler.sendEmptyMessage(PROGRESS_CHANGED);
	}

	/**
	 * ������MediaPlayer.OnInfoListener�ӿ� �����ֹ��ڲ���ý����ض���Ϣ������Ҫ���������ʱ�򽫵��ø÷���
	 * ���翪ʼ���塢��������������ٶȱ仯(���д���֤) С��:��ЩInfo������MediaPlayer.MEDIA_INFO_��ͷ��
	 */
	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		if (what == MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING) {
			// ��Ƶ����Ƶ���ݲ���ȷ�ؽ���ʱ�����ָ���ʾ��Ϣ.��һ��
			// ��ȷ�����ý���ļ���,��Ƶ����Ƶ��������������,�Ӷ�
			// ʹ�ò��ſ�����Ч��ƽ�ȵؽ���
		}
		if (what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
			// ��ý�岻����ȷ��λʱ�����ָ���ʾ��Ϣ. ��ʱ��ζ����������һ��������
		}
		if (what == MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING) {
			// ���豸�޷�������Ƶʱ�����ָ���ʾ��Ϣ
			// ������Ƶ̫���ӻ������ʹ���
		}
		if (what == MediaPlayer.MEDIA_INFO_METADATA_UPDATE) {
			// ���µ�Ԫ���ݿ���ʱ�����ָ���ʾ��Ϣ ,android 2.0���ϰ汾���á�
		}
		if (what == MediaPlayer.MEDIA_INFO_UNKNOWN) {
			// ���಻��֪��ʾ��Ϣ
		}
		return false;
	}

	/**
	 * ������MediaPlayer.OnErrorListener�ӿ� MediaPlayer��������ʱ����ø÷��� ֻ�����������ִ���.
	 * С��:��Щ��������MediaPlayer.MEDIA_ERROR.��ͷ��
	 */
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if (what == MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
			System.out.println("��һ�ִ���");
		}
		if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
			System.out.println("�ڶ��ִ���");
		}
		if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
			System.out.println("�����ִ���");
		}
		return false;
	}

	// ������MediaPlayer.OnCompletionListener�ӿ�
	// ��MediaPlayer�������ļ�ʱ,����ø÷���.
	// ��ʱ���Խ���һЩ�����Ĳ�������:������һ����Ƶ
	@Override
	public void onCompletion(MediaPlayer mp) {
		finish();
	}

	private void hideControllerDelay() {
		myHandler.removeMessages(HIDE_CONTROLER);
		myHandler.sendEmptyMessageDelayed(HIDE_CONTROLER, TIME);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			System.out.println("�л�Ϊ����");
			convertScreen();
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			convertScreen();
			System.out.println("�л�Ϊ����");
		}
	}

	/**
	 * ��ѡ����Ļʱ
	 */
	private void convertScreen() {
		if (isControllerShow) {
			hideController();
			getScreenSize();
			showController();
		} else {
			getScreenSize();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong("currentPlayTime", vv.getCurrentPosition());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		vv.seekTo((int) savedInstanceState.getLong("currentPlayTime"));
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	//TODO: ���޵�����ת��
	private static boolean isNeedRotateScreen = true;
	@Override
	protected void onResume() {
		super.onResume();
		if(isNeedRotateScreen){
			imgBtnClick(null);
			isNeedRotateScreen = false;
		}
	}

}
