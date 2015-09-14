package cc.vplayer.widget;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.AttributedCharacterIterator.Attribute;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.provider.MediaStore.Files;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

public class MyVideoView extends SurfaceView implements MediaPlayerControl {
	private String TAG = "VideoView";
	private int mDuration;
	private int mCurrentPosition;
	private int mBufferPercentage;
	private int mVideoHeight;
	private int mVideoWidth;
	private MediaPlayer mMediaPlayer;
	private MediaController mMediaController;
	private boolean mIsPrepared;
	private Context mContext;
	private Uri mUri;
	private int mSeekWhenPrepared;
	private boolean mStartWhenPrepared;
	private SurfaceHolder mSurfaceHolder;
	private int mSurfaceWidth;
	private int mSurfaceHeight;
	private int mRequestedHeight;// videoHeight
	private int mRequestedWidth;// videoWidth

	public MyVideoView(Context context) {
		super(context);
		mContext = context;
		initVideoView();
		mMediaController = new MediaController(context);
	}

	public MyVideoView(Context context, AttributeSet atr) {
		super(context, atr, 0);
		mContext = context;
		initVideoView();
	}

	public MyVideoView(Context context, AttributeSet atr, int defStyle) {
		super(context, atr, defStyle);
		mContext = context;
		initVideoView();
	}

	private void initVideoView() {
		mVideoHeight = 0;
		mVideoWidth = 0;
		getHolder().addCallback(mSHCallbcak);
		/**
		 * SURFACE_TYPE_NORMAL:RAM缓存的原生数据 SURFACE_TYPE_HARDWARE:通过DMA，direct
		 * memory access，就是直接写屏技术获取到的数据，或者其他硬件加速的数据 SURFACE_TYPE_GPU:通过GPU加速的数据
		 * SURFACE_TYPE_PUSH_BUFFERS
		 * :标识数据来源于其他对象，比如照相机，比如视频播放服务器（android内部有视频播放的服务器，所有播放视频相当于客户端）
		 */
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
		// System.out.println("init complete!");
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(mRequestedWidth, widthMeasureSpec);
		int height = getDefaultSize(mRequestedHeight, heightMeasureSpec);
		/*
		 * if (width>0 && height>0) { if (mRequestedWidth * height > width *
		 * mRequestedHeight) { height = width * mRequestedHeight /
		 * mRequestedWidth; } else if (mRequestedWidth * height < width *
		 * mRequestedHeight) { width = height * mRequestedWidth /
		 * mRequestedHeight; } }
		 */
		setMeasuredDimension(width, height);
	}

	public void setMediaUri(String path) {
		setMediaUri(Uri.parse(path));
	}

	public void setMediaUri(Uri uri) {
		mStartWhenPrepared = false;
		mSeekWhenPrepared = 0;
		mUri = uri;
		openVideo();
		requestLayout(); // Call this when something has changed which has
							// invalidated the layout of
							// this view. This will schedule a layout pass of
							// the view tree.

		invalidate(); // Invalidate the whole view. a non-UI thread, call
						// postInvalidate().
	}

	private void openVideo() {
		if (mUri == null || mSurfaceHolder == null) {
			// not ready for playback just yet, will try again later
			System.out.println("has  return !" + mUri + "--" + mSurfaceHolder);
			return;
		}

		// Tell the music playback service to pause
		// TODO: these constants need to be published somewhere in the
		// framework.
		Intent i = new Intent("com.android.music.musicservicecommand");
		i.putExtra("command", "pause");
		mContext.sendBroadcast(i);

		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}

		try {
			mMediaPlayer = new MediaPlayer();
			// 标识流媒体BUFFER 状态发生改变时候调用
			mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
			// 播放完成
			mMediaPlayer.setOnCompletionListener(mCompletionListener);
			// 异步操作错误的时候
			mMediaPlayer.setOnErrorListener(mOnErrorListener);
			// 第一次知道视频尺寸和尺寸更改时调用
			mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
			// 媒体准备好去播放的时候调用
			mMediaPlayer.setOnPreparedListener(mPreparedListener);
			mIsPrepared = false;
			mDuration = -1;
			mBufferPercentage = 0;
			// mUri =
			// Uri.parse("http://113.251.222.244:8080/NetEaseServer/blank.mp4");
			mMediaPlayer.setDataSource(mContext, mUri);
			mMediaPlayer.setScreenOnWhilePlaying(true);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setDisplay(mSurfaceHolder);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			attchMediaController();
		} catch (IllegalArgumentException e) {
			Log.w(TAG, "Unable to open content: " + mUri, e);
			return;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			Log.w(TAG, "Unable to open content: " + mUri, e);
			return;
		}
	}

	public void setMediaController(MediaController mediaController) {
		if (mMediaController != null) {
			mMediaController.hide();
		}
		mMediaController = mediaController;
		attchMediaController();
	}

	/**
	 * 初始化界面，媒体操作界面，和功能 把控制条附加到播放视频的SurfaceView上
	 */
	private void attchMediaController() {
		if (mMediaPlayer != null && mMediaController != null) {
			mMediaController.setMediaPlayer(this);
			View anchorView = this.getParent() instanceof View ? (View) this.getParent() : this;

			mMediaController.setAnchorView(anchorView);
			mMediaController.setEnabled(mIsPrepared);
		}
	}

	public boolean onTouchEvent(android.view.MotionEvent event) {
		if (mIsPrepared && mMediaPlayer != null && mMediaController != null) {
			mMediaController.show();
		}
		return false;
	};

	OnBufferingUpdateListener mBufferingUpdateListener = new OnBufferingUpdateListener() {

		@Override
		public void onBufferingUpdate(MediaPlayer mp, int percent) {

		}
	};

	OnErrorListener mOnErrorListener = new OnErrorListener() {

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			return false;
		}
	};

	OnVideoSizeChangedListener mSizeChangedListener = new OnVideoSizeChangedListener() {

		@Override
		public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

		}
	};

	OnCompletionListener mCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
		}
	};

	OnPreparedListener mPreparedListener = new OnPreparedListener() {

		@Override
		public void onPrepared(MediaPlayer mp) {
			mDuration = mMediaPlayer.getDuration();
		}
	};

	private Callback mSHCallbcak = new Callback() {

		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			mSurfaceWidth = w;
			mSurfaceHeight = h;
			if (mMediaPlayer != null && mIsPrepared && mVideoWidth == w && mVideoHeight == h) {
				if (mSeekWhenPrepared != 0) {
					mMediaPlayer.seekTo(mSeekWhenPrepared);
					mSeekWhenPrepared = 0;
				}
				mMediaPlayer.start();
				if (mMediaController != null) {
					mMediaController.show();
				}
			}
		}

		public void surfaceCreated(SurfaceHolder holder) {
			mSurfaceHolder = holder;
			openVideo();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// after we return from this we can't use the surface any more
			mSurfaceHolder = null;
			if (mMediaController != null)
				mMediaController.hide();
			if (mMediaPlayer != null) {
				mMediaPlayer.reset();
				mMediaPlayer.release();
				mMediaPlayer = null;
			}
		}
	};

	public void stopPlayback() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	@Override
	public void start() {
		if (mMediaPlayer != null && mIsPrepared) {
			mMediaPlayer.start();
			mStartWhenPrepared = false;
		} else {
			mStartWhenPrepared = true;
		}
	}

	@Override
	public void pause() {
		if (mMediaPlayer != null && mIsPrepared) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause();
			}
		} else {
			mStartWhenPrepared = false;
		}
	}

	@Override
	public int getDuration() {
		mDuration = mMediaPlayer.getDuration();
		return mMediaPlayer.getDuration();
	}

	@Override
	public int getCurrentPosition() {
		return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : 0;
	}

	@Override
	public void seekTo(int pos) {
		if (mMediaPlayer != null) {
			mMediaPlayer.seekTo(pos);
		} else {
			System.out.println("mediaplayer is null!");
		}
	}

	@Override
	public boolean isPlaying() {
		return mMediaPlayer.isPlaying();
	}

	@Override
	public int getBufferPercentage() {
		return mBufferPercentage;
	}

	@Override
	public boolean canPause() {
		return false;
	}

	@Override
	public boolean canSeekBackward() {
		return false;
	}

	@Override
	public boolean canSeekForward() {
		return false;
	}

	/**
	 * Register a callback to be invoked when the media file is loaded and ready
	 * to go.
	 *
	 * @param l
	 *            The callback that will be run
	 */
	public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
		mPreparedListener = l;
	}

	/**
	 * Register a callback to be invoked when the end of a media file has been
	 * reached during playback.
	 *
	 * @param l
	 *            The callback that will be run
	 */
	public void setOnCompletionListener(OnCompletionListener l) {
		mCompletionListener = l;
	}

	/**
	 * Register a callback to be invoked when an error occurs during playback or
	 * setup. If no listener is specified, or if the listener returned false,
	 * VideoView will inform the user of any errors.
	 *
	 * @param l
	 *            The callback that will be run
	 */
	public void setOnErrorListener(OnErrorListener l) {
		mOnErrorListener = l;
	}

	public boolean ismIsPrepared() {
		return mIsPrepared;
	}

	public void setmIsPrepared(boolean mIsPrepared) {
		this.mIsPrepared = mIsPrepared;
	}

	public void setOnSizeChangedListener(OnVideoSizeChangedListener mSizeChangedListener) {
		this.mSizeChangedListener = mSizeChangedListener;
	}

	@Override
	public int getAudioSessionId() {
		return 0;
	}
}