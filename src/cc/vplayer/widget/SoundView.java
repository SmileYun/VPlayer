package cc.vplayer.widget;

import java.io.InputStream;
import java.text.AttributedCharacterIterator.Attribute;

import cc.vplayer.R;
import cc.vplayer.R.drawable;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SoundView extends View {

	private final static int HEIGHT = 20;
	public final static int MY_HEIGHT = 350;
	public final static int MY_WIDTH = 170;
	private Context mContext;
	private Bitmap mBmSelectSound;
	private Bitmap mBmUnSelectSound;
	private int index;
	private OnVolumeChangeListener mOnVolumeChangeListener;
	
	public void setOnVolumeChangeListener(OnVolumeChangeListener mOnVolumeChangeListener) {
		this.mOnVolumeChangeListener = mOnVolumeChangeListener;
	}

	public interface OnVolumeChangeListener{
		public void volumeChange(int index);
	}
	
	public SoundView(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	public SoundView(Context context,AttributeSet att) {
		super(context,att);
		mContext = context;
		init();
	}
	
	public SoundView(Context context,AttributeSet att,int defStyle) {
		super(context,att,defStyle);
		mContext = context;
		init();
	}
	
	private void init(){
		BitmapFactory.Options opts = new Options();
		//opts.inJustDecodeBounds = true;
		opts.inInputShareable = true;
		opts.inPurgeable = true;
//		opts.inSampleSize = 2;
		InputStream is = getResources().openRawResource(R.drawable.sound_line);
		mBmSelectSound = BitmapFactory.decodeStream(is, null, opts);
		InputStream is1 = getResources().openRawResource(R.drawable.sound_line1);
		mBmUnSelectSound = BitmapFactory.decodeStream(is1, null, opts);
		AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		setVolume(am.getStreamVolume(AudioManager.STREAM_MUSIC));
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		// 未选择的声音颜色
		for (int i = 0; i < 15; i++) {
			canvas.drawBitmap(mBmUnSelectSound,null,new Rect(0, i*HEIGHT, (15-i)*HEIGHT/2, mBmUnSelectSound.getHeight()+i*HEIGHT),null);
		}
		int reserveIndex = 15 - index;
		// 已选择的声音颜色
		for (int i = reserveIndex; i < 15; i++) {
			canvas.drawBitmap(mBmSelectSound,null,new Rect(0, i*HEIGHT, (15-i)*(HEIGHT/2), mBmSelectSound.getHeight()+i*HEIGHT+5),null);
		}
		
		super.onDraw(canvas);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int _x = (int) (event.getY()*15/MY_HEIGHT);
		setVolume((16 - _x));
		
		return true;
	}
	
	private void setVolume(int v){
		if (v>15) {
			v = 15;
		}
		if (v<0) {
			v = 0;
		}
		if(index!=v){
			index = v;
			if(mOnVolumeChangeListener!=null){
				mOnVolumeChangeListener.volumeChange(v);
			}
		}
		
		invalidate();
	}
	
}
