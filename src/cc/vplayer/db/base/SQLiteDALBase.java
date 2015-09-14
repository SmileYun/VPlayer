package cc.vplayer.db.base;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cc.vplayer.db.base.DBOpenHelper.SQLiteDBTable;

/**
 * 数据库操作层，不能用于在Activity中的调用以及执行业务逻辑
 * @author cc
 *
 */
public abstract class SQLiteDALBase implements SQLiteDBTable{
	private Context mContext;
	private SQLiteDatabase mSqLiteDatabase;
	
	public SQLiteDALBase(Context context){
		mContext = context;
	}
	
	/**
	 * 获取一个可写的数据库
	 * @return SQLiteDatabase
	 */
	public SQLiteDatabase getDataBase(){
		if (mSqLiteDatabase == null) {
			mSqLiteDatabase = DBOpenHelper.getInstance(mContext).getWritableDatabase();
		}
		return mSqLiteDatabase;
	}
	
	public void beginTransaction() {
		getDataBase().beginTransaction();
	}

	public void setTransactionSuccessful() {
		getDataBase().setTransactionSuccessful();
	}

	public void endTransaction() {
		mSqLiteDatabase.endTransaction();
	}
	
	/**
	 * 根据条件查询数据库中满足条件的个数
	 * @param pCondition
	 * @return
	 */
	public int getCount(String pCondition) {
		String _String[] = getTableNameAndPK();
		Cursor _Cursor = execSql("Select " + _String[1] + " From " + _String[0]
				+ " Where 1=1 " + pCondition);
		int _Count = _Cursor.getCount();
		_Cursor.close();
		return _Count;
	}

	public int getCount(String pPK, String pTableName, String pCondition) {
		Cursor _Cursor = execSql("Select " + pPK + " From " + pTableName
				+ " Where 1=1 " + pCondition);
		int _Count = _Cursor.getCount();
		_Cursor.close();
		return _Count;
	}

	protected boolean delete(String pTableName, String pCondition) {
		return getDataBase().delete(pTableName, " 1=1 " + pCondition, null) >= 0;
	}
	/**
	 * 获取表名和主键名
	 * {表名， 主键名} 
	 */
	protected abstract String[] getTableNameAndPK();
	/**
	 * 根据SQL语句得到表中用户信息
	 * @param pSqlText:String SQL脚本
	 * @return List
	 */
	protected List getList(String pSqlText) {
		Cursor _Cursor = execSql(pSqlText);
		return cursorToList(_Cursor);
	}
	/**
	 * 将 游标 转换为List 
	 * @param pCursor:Cursor
	 * @return List
	 */
	protected List cursorToList(Cursor pCursor) {
		List _List = new ArrayList();
		while (pCursor.moveToNext()) {
			Object _Object = findModel(pCursor);
			_List.add(_Object);
		}
		pCursor.close();
		return _List;
	}

	public Cursor execSql(String pSqlText) {
		return getDataBase().rawQuery(pSqlText, null);
	}

	/**
	 * 游标转换成实体类，父类调用子类方法
	 * 覆写抽象方法，将Cursor转化为实体类
	 * @param pCursor
	 * @return
	 */
	protected abstract Object findModel(Cursor pCursor);
}
