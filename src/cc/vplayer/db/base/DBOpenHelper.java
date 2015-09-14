package cc.vplayer.db.base;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import cc.vplayer.Util.Reflection;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper{
	private Context mContext;
	private static DBConfig sDbConfig;
	private static DBOpenHelper sDbOpenHelper;
	private Reflection mReflection;
	
	private DBOpenHelper(Context context) {
		super(context, DBConfig.getDatabaseName(), null, DBConfig.getVersion());
		mContext = context;
	}
	
	public static DBOpenHelper getInstance(Context context){
		if(sDbOpenHelper == null){
			synchronized(DBOpenHelper.class){
				if (sDbOpenHelper == null) {
					sDbOpenHelper = new DBOpenHelper(context);
					sDbConfig = DBConfig.getDbConfig(context);
				}
			}
		}
		return sDbOpenHelper;		
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		ArrayList<String> _list = DBConfig.getTables();
		mReflection = new Reflection();
		int _count = _list.size();
		for (int j = 0; j < _count ; j++) {
			try {
				SQLiteDBTable _SQLiteDataTable = (SQLiteDBTable) mReflection.newInstance(_list.get(j),
						new Object[]{mContext}, new Class[]{Context.class});
				
				_SQLiteDataTable.onCreate(db);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	public interface SQLiteDBTable{
		/**
		 * 根据SQL脚本创建数据库
		 */
		void onCreate(SQLiteDatabase db);
		void onUpgrade(SQLiteDatabase db);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
