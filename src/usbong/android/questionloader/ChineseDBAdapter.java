package usbong.android.questionloader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//National Capital Region,NCR; Metro Manila
//Cordillera Administrative Region,CAR
//Ilocos Region,Region I
//Cagayan Valley,Region II
//Central Luzon,Region III
//CALABARZON,Region IV-A
//MIMAROPA,Region IV-B
//Bicol Region,Region V
//Western Visayas,Region VI
//Central Visayas,Region VII
//Eastern Visayas,Region VIII
//Zamboanga Peninsula,Region IX
//Northern Mindanao,Region X
//Davao Region,Region XI
//SOCCSKSARGEN,Region XII
//Caraga,Region XIII
//Autonomous Region in Muslim Mindanao,ARMM

public class ChineseDBAdapter {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_WORD = "WORD";
	public static final String KEY_DEF = "DEF";
	public static final String KEY_READING = "READING";

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private static final String DATABASE_NAME = "ChineseDict";
	private static final String SQLITE_TABLE = "cdict";
	private static final int DATABASE_VERSION = 1;

	private final Context mCtx;

	private static final String DATABASE_CREATE = "CREATE TABLE if not exists "
			+ SQLITE_TABLE + " (" + KEY_ROWID
			+ " integer PRIMARY KEY autoincrement," + KEY_WORD + ","
			+ KEY_DEF + "," + KEY_READING + ");";

	
	// UTILITY TABLE HELPER CLASS
	
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		// DATABASE CREATION
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		// DATABASE CHANGE
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
			onCreate(db);
		}
	}
	
	
	// LIFE CYCLE
	
	public ChineseDBAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public ChineseDBAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		
		
		return this;
	}

	public void close() {
		if (mDbHelper != null) {
			mDbHelper.close();
		}
	}

	
	
	// ACTIONS
	
	
	public long createEntry(String word, String def, String reading) {

		
		// INSERT
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_WORD, word);
		initialValues.put(KEY_DEF, def);
		initialValues.put(KEY_READING, reading);

		// parameters
		// mDb.insert(table, nullColumnHack, values);
		
		return mDb.insert(SQLITE_TABLE, null, initialValues);
	}

	public boolean deleteAll() {

		// DELETE
		// parameters
		// mDb.delete(table, whereClause, whereArgs)
		
		int doneDelete = 0;
		doneDelete = mDb.delete(SQLITE_TABLE, null, null);
		return doneDelete > 0;

	}

	public Cursor fetchDefByWord(String inputText) throws SQLException {
		Cursor mCursor = null;
		if (inputText == null || inputText.length() == 0) {
			
			mCursor = mDb.query(SQLITE_TABLE, new String[] { KEY_ROWID,
					KEY_WORD, KEY_DEF, KEY_READING }, null, null, null, null, null);

		} else {
			mCursor = mDb.query(true, SQLITE_TABLE, new String[] { KEY_ROWID,
					KEY_WORD, KEY_DEF, KEY_READING }, KEY_WORD + " like '%" + inputText
					+ "%'", null, null, null, null, null);
		}
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchAll() {

		// parameter descriptions
		// mDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy)

		Cursor mCursor = mDb.query(SQLITE_TABLE, new String[] { KEY_ROWID,
				KEY_WORD, KEY_DEF, KEY_READING }, null, null, null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	
	
	// SEEDING
	
	/*public void seed() {

		// the Context is the Activity where this is currently used
		String[] regionData = mCtx.getResources().getStringArray(R.array.regions);
		
		// get string-array, parse and store
		for (String r : regionData) {
			String[] data = r.split(",");
			createRegion(data[0], data[1]);
		}
	}*/

}