package cc.vplayer.adapter;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cc.vplayer.R;
import cc.vplayer.adapterbase.AdapterBase;

public class ResultAdapter<E> extends AdapterBase<E> {

	private Context context;

	public ResultAdapter(List<E> list, Context context) {
		super(list);
		this.context = context;
	}

	Holder holder;

	private class Holder {
		ImageView mv;
		TextView tv;
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.result_listview_item, null);
			holder = new Holder();
			holder.tv = (TextView) convertView.findViewById(R.id.result_player_name);
			holder.mv = (ImageView) convertView.findViewById(R.id.result_player_icon);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
//		System.out.println("---->position:" + position);
		holder.tv.setText((CharSequence) (((HashMap<String, Object>) mList.get(position)).get("name")));
		// holder.mv.setImageDrawable(context.getResources().getDrawable(
		// (Integer) ((HashMap<String, Object>)
		// mList.get(position)).get("icon")));

		if ((Bitmap) ((HashMap<String, Object>) mList.get(position)).get("icon") != null) {
			holder.mv.setImageBitmap((Bitmap) ((HashMap<String, Object>) mList.get(position)).get("icon"));
		} else {
			holder.mv.setImageBitmap(getOptsBitmap(R.drawable.defualt));
		}

		return convertView;
	}

	/** 获取优化位图 */
	private Bitmap getOptsBitmap(int resId) {
		InputStream _in = context.getResources().openRawResource(resId);
		BitmapFactory.Options _opts = new Options();
		_opts.inInputShareable = true;
		// _opts.inSampleSize = 2;
		_opts.inPurgeable = true;
		Bitmap _map = BitmapFactory.decodeStream(_in, null, _opts);
		return _map;
	}
}
