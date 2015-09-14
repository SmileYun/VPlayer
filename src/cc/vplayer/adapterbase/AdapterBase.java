package cc.vplayer.adapterbase;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class AdapterBase<E> extends BaseAdapter{
	
	protected ArrayList<E> mList;
	
	public AdapterBase(List<E> list){
		mList = (ArrayList<E>) list;
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
