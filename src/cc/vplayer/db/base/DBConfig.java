package cc.vplayer.db.base;

import java.util.ArrayList;

import cc.vplayer.R;

import android.content.Context;

/**
 * 
 * 动态获取数据库名字
 */
public class DBConfig {
	private static DBConfig sDbConfig;
	private static Context mContext;
	private static final String DATABASE_NAME = "vplayer";
	private static final int DATABASE_VERSION = 1;
	
	private DBConfig(){}
	
	public static DBConfig getDbConfig(Context context){
		if (sDbConfig == null) {
			synchronized(DBConfig.class){
				if (sDbConfig == null) {
					sDbConfig = new DBConfig();
				}
			}
		}
		mContext = context;
		return sDbConfig;
	}
	
	/**
	 * 根据配置类，动态实例化对象
	 * @return
	 */
	public static ArrayList<String> getTables(){
		ArrayList<String> _list = new ArrayList<String>();
		
		String[] claz = mContext.getResources().getStringArray(R.array.SQLiteDALClassName);
		//获取的包名(cc.vplayer)，组成要反射的完整路径
		String pakcageName = mContext.getPackageName() + ".db.dal.";
		int _count = claz.length;
		for (int i = 0; i < _count; i++) {
			_list.add(pakcageName + claz[i]);
		}
		return _list;
	}
	
	public static String getDatabaseName() {
		return DATABASE_NAME;
	}

	public static int getVersion() {
		return DATABASE_VERSION;
	}
}
