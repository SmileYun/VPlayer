package cc.vplayer.adapter;

import java.util.ArrayList;

import cc.vplayer.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentAdapter<E> extends PagerAdapter{
	
	ArrayList<E> mPageList;
	LayoutInflater mLayoutInflater;
	public FragmentAdapter(Context context ,ArrayList<E> list){
		mPageList = list;
		mLayoutInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return mPageList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}


	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View v = mLayoutInflater.inflate(R.layout.viewpager_item, container, false);
		ImageView page = (ImageView) v.findViewById(R.id.vp_bg);
		page.setImageBitmap((Bitmap) mPageList.get(position)) ;
		container.addView(v);
		return v;
	}
}
