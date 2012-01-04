package nps.anddev.alramFunc;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.util.Log;


public class AlarmStorege {
	public Map<Integer , Vector<AlarmObject>> m_GroupStoreMap;
	public ArrayList<AlarmObject> m_lstAlarmDevingGroup;
	
	public static final String KEY_TITLE = "title";
	public static final String KEY_BODY = "body";
	public static final String KEY_ROWID = "_id";
	
	public static final String TAG = "AlarmSotrage";
	private SQLiteDatabase m_DB;
	
	private static final String DATABASE_CREATE 
		= "create table alarms (_id integer primary key autoincrement, " 
				+ "title text not null, body text not null);";
	
	private static final String DATABASE_NAME = "data";
	private static final String DATABASE_TABLE = "AlarmsStore";
	private static final int DATABASE_VERSION = 1;
	
	private DatabaseHelper m_DbHelper;
	private final Context m_Context;
	
	private static class DatabaseHelper extends SQLiteOpenHelper{
		public DatabaseHelper(Context context) {
			// TODO Auto-generated constructor stub
			super(context,DATABASE_NAME,null,DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db){
			db.execSQL(DATABASE_CREATE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			Log.i("AlarmStoreTag","Upgrading database from version " + oldVersion + 
					" to " + newVersion + ",which will destroy all old data");
			db.execSQL("DROP TABLE IF EXIST AlarmStore");
			onCreate(db);
		}
	}
	
	public AlarmStorege(Context aContext){
		this.m_Context = aContext;
	}
	
	//0.2 버전용 DB 객체 생성자
	public AlarmStorege(Context aContext, ArrayList<AlarmObject> aList){
		this.m_Context = aContext;
		m_lstAlarmDevingGroup = aList;
	}
	
	//0.8버전용 DB 객체 생성자
	public AlarmStorege(Context aContext, Map<Integer , Vector<AlarmObject>> aMaps){
		this.m_Context = aContext;
		m_GroupStoreMap = aMaps;
	}
	
	public AlarmStorege open() throws SQLException{
		m_DbHelper = new DatabaseHelper(m_Context);
		m_DB = m_DbHelper.getWritableDatabase();
		
		return this;
	}
	
	public void close(){
		m_DbHelper.close();
	}
	
	public long createAlarm(AlarmObject aObject){
		ContentValues initialValue = new ContentValues();
		
		//initialValue.put(KEY_BODY, )
		
		return m_DB.insert(DATABASE_TABLE, null, initialValue);
	}
}
