package cc.vplayer.db.dal;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import cc.vplayer.db.base.SQLiteDALBase;
import cc.vplayer.model.VedioInfo;

public class LocalContentDB extends SQLiteDALBase {

	public LocalContentDB(Context context) {
		super(context);
	}

	private static final String TABLE_NAME = "LocalContent";

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (MovieID integer PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ " PATH VARCHAR(128), " + " DURATION NUMERIC, " +
				// " TYPE VARCHAR(20) , " +
				" NAME VARVHAR(50) , " + " SIZE NUMERIC ," + " THUMB BITMAP_VALUES);");
	}

	@Override
	protected Object findModel(Cursor pCursor) {
		if (pCursor == null) {
			return null;
		}
		ArrayList<VedioInfo> list = new ArrayList<VedioInfo>();

		pCursor.moveToFirst();
		do {
			VedioInfo _VI = new VedioInfo();
			try {
				_VI.setId(pCursor.getLong(pCursor.getColumnIndexOrThrow("MovieID")));
				_VI.setPath(pCursor.getString(pCursor.getColumnIndexOrThrow("PATH")));
				_VI.setDuration(pCursor.getInt(pCursor.getColumnIndex("DURATION")));
				_VI.setDisplayName(pCursor.getString(pCursor.getColumnIndexOrThrow("NAME")));
				_VI.setSize(pCursor.getLong(pCursor.getColumnIndex("SIZE")));
				_VI.setThumb(pCursor.getBlob(pCursor.getColumnIndex("THUMB")));
				list.add(_VI);
			} catch (Exception e) {
				getDataBase().execSQL("drop table if exists " + TABLE_NAME + ";");
			}
		} while (pCursor.moveToNext());
		return list;
	}

	public boolean insertVedioInfo(VedioInfo v) {
		getDataBase().execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (MovieID integer PRIMARY KEY AUTOINCREMENT NOT NULL, "
				+ " PATH VARCHAR(128), " + " DURATION NUMERIC, " +
				// " TYPE VARCHAR(20) , " +
				" NAME VARVHAR(50) , " + " SIZE NUMERIC ," + " THUMB BITMAP_VALUES);");
		ContentValues _cValues = createParams(v);
		long _id = getDataBase().insert(getTableNameAndPK()[0], null, _cValues);
		v.setId(_id);
		return _id > 0;
	}

	public void deleteAllInfo() {
		try {
			getDataBase().delete(getTableNameAndPK()[0], null, null);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	private ContentValues createParams(VedioInfo v) {
		ContentValues _ContentValues = new ContentValues();
		_ContentValues.put("PATH", v.getPath());
		_ContentValues.put("DURATION", v.getDuration());
		_ContentValues.put("NAME", v.getDisplayName());
		_ContentValues.put("SIZE", v.getSize());
		
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		if (v.getThumb() != null) {
			v.getThumb().compress(Bitmap.CompressFormat.PNG, 100, os);
			_ContentValues.put("THUMB", os.toByteArray());
		}else {
			_ContentValues.put("THUMB", "");
		}
		

		return _ContentValues;
	}

	public ArrayList<VedioInfo> queryAllInfo() {
		try {
			Cursor _cursor = getDataBase().query(TABLE_NAME, null, null, null, null, null, null);
		} catch (Exception e) {
			return null;
		}

		return (ArrayList<VedioInfo>) findModel(getDataBase().query(TABLE_NAME, null, null, null, null, null, null));

	}

	@Override
	public void onUpgrade(SQLiteDatabase db) {
	}

	@Override
	protected String[] getTableNameAndPK() {
		String[] _s = new String[] { TABLE_NAME, "MovieID" };
		return _s;
	}
}