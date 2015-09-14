package cc.vplayer;

import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;
import cc.vplayer.db.dal.LocalContentDB;

public class TestCase extends AndroidTestCase{
	public TestCase(){}
	SQLiteOpenHelper db; 
	
	public void testCreateTAB(){
		new LocalContentDB(getContext()).queryAllInfo().toString();
	}
}
