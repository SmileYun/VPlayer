package cc.vplayer.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cc.vplayer.R;
import cc.vplayer.adapterbase.AdapterBase;

public class SerchContentAdapter<E> extends AdapterBase<E> {

	private Context context;

	public SerchContentAdapter(List<E> list, Context context) {
		super(list);
		this.context = context;
	}

	Holder holder;

	private class Holder {
		ImageView mv;
		TextView tv;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context.getApplicationContext())
					.inflate(R.layout.player_list_item, null);
			holder = new Holder();
			holder.tv = (TextView) convertView.findViewById(R.id.player_name);
			holder.mv = (ImageView) convertView.findViewById(R.id.player_icon);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		holder.tv.setText((CharSequence) (((HashMap<String, Object>) mList
				.get(position)).get("name")));
		holder.mv.setImageDrawable(context.getResources().getDrawable(
				(Integer) ((HashMap<String, Object>) mList.get(position))
						.get("icon")));

		return convertView;
	}

}
