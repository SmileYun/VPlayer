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
import android.view.WindowManager;

public class LightView extends View {

	private final static int HEIGHT = 20;
	public final static int MY_HEIGHT = 350;
	public final static int MY_WIDTH = 170;
	private Context mContext;
	private Bitmap mBmSelectSound;
	private Bitmap mBmUnSelectSound;
	private int index;
	private OnLightChangeListener mOnLightChangeListener;
	private int initLight = 0;
	
	
	
	public void setOnLightChangeListener(OnLightChangeListener mOnLightChangeListener) {
		this.mOnLightChangeListener = mOnLightChangeListener;
	}

	public interface OnLightChangeListener{
		public void lightChange(int index);
	}
	
	public LightView(Context context, int initLight) {
		super(context);
		mContext = context;
		this.initLight = initLight;
		init();
	}
	
	public LightView(Context context,AttributeSet att,int initLight) {
		super(context,att);
		mContext = context;
		this.initLight = initLight;
		init();
	}
	
	public LightView(Context context,AttributeSet att,int defStyle,int initLight) {
		super(context,att,defStyle);
		mContext = context;
		this.initLight = initLight;
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
		setLight(initLight);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		// 未选择的亮度颜色
		for (int i = 0; i < 15; i++) {
			canvas.drawBitmap(mBmUnSelectSound,null,new Rect(MY_WIDTH -((15-i)*(HEIGHT/2)), i*HEIGHT, MY_WIDTH, mBmUnSelectSound.getHeight()+i*HEIGHT),null);
		}
		int reserveIndex = 15 - index;
		// 已选择的亮度颜色
		for (int i = reserveIndex; i < 15; i++) {
			canvas.drawBitmap(mBmSelectSound,null,new Rect(MY_WIDTH -((15-i)*(HEIGHT/2)), i*HEIGHT, MY_WIDTH, mBmSelectSound.getHeight()+i*HEIGHT+5),null);
		}
		
		super.onDraw(canvas);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int _x = (int) (event.getY()*15/MY_HEIGHT);
		setLight((16 - _x));
		
		return true;
	}
	
	private void setLight(int v){
		if (v>15) {
			v = 15;
		}
		if (v<0) {
			v = 0;
		}
		if(index!=v){
			index = v;
			if(mOnLightChangeListener!=null){
				mOnLightChangeListener.lightChange(v);
			}
		}
		
		invalidate();
	}
	
}

