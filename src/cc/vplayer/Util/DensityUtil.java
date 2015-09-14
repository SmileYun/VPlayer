package cc.vplayer.Util;

import android.content.Context;
import android.util.DisplayMetrics;

public class DensityUtil {
	private Context mContext;
	private DisplayMetrics dm;
	private float scale;

	public DensityUtil(Context c) {
		mContext = c;
		dm = mContext.getApplicationContext().getResources()
				.getDisplayMetrics();
		scale = dm.scaledDensity;
	}

	public float dip2px(float dipValue) {
		return (dipValue * scale) + 0.5f;
	}

	public float px2dip(float pxValue) {
		return (pxValue / scale + 0.5f);
	}

	/**
	 * ÆÁÄ»¸ß¶È
	 * 
	 * @return
	 */
	public int getScreenHeight() {
		return dm.heightPixels;
	}

	/**
	 * ÆÁÄ»¿í¶È
	 * 
	 * @return
	 */
	public int getScreenWidth() {
		return dm.widthPixels;
	}

	public String toString() {
		return dm.heightPixels + " 2w:" + dm.widthPixels;
	}
}
