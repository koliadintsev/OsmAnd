package com.operasoft.snowboard.database;

import java.io.File;

import net.osmand.plus.OsmandApplication;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.operasoft.snowboard.util.Config;

public class DataBaseHelper extends SQLiteOpenHelper {

//	public static final String DB_PATH = "/data/data/" + OsmandApplication.getInstance().getPackageName() + "/databases/";// "/sdcard/";
	public static final String DB_PATH = "/sdcard/";
	public static final String DB_NAME = Config.getDbName();
	private static String DB_EXTENSION = "sqlite";
	private static SQLiteDatabase myDataBase;
	private final Context myContext;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to access to the application assets and resources.
	 * 
	 * @param context
	 */
	public DataBaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}

	public boolean isTableExists(String tableName) {
		openDataBase();
		return isTableExists(myDataBase, tableName);
	}

	public boolean isTableExists(SQLiteDatabase db, String tableName) {
		Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.close();
				return true;
			}
			cursor.close();
		}
		return false;
	}

	public String isRecordExist(String columnName) {
		openDataBase();
		Cursor cursor = myDataBase.rawQuery("select * from master_setting", null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToNext();
				String version = cursor.getString(cursor.getColumnIndexOrThrow("db_version"));
				cursor.close();
				return version;
			}
			cursor.close();
		}
		return "";
	}

	public boolean isRecordExistFirstTime(String columnName) {
		openDataBase();
		Cursor cursor = myDataBase.rawQuery("select " + columnName + " from master_setting", null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.close();
				return true;
			}
			cursor.close();
		}
		return false;
	}

	public void updateTableValue(String table, String columnName, String columnValue) {
		openDataBase();
		String sqlIns = "UPDATE " + table + " SET ";
		sqlIns += "'" + columnName + "'" + " = " + "'" + columnValue + "'";
		DataBaseHelper.getDataBase().execSQL(sqlIns);
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	public boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		String myPath = DB_PATH + DB_NAME;
		try {
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		} catch (Throwable t) {
			Log.e("DataBaseHelper", "Failed to open Database " + myPath, t);
			return false;
		}

		if (checkDB != null) {
			boolean result = true;
			
			// Make sure we have a minimum of information in the database
			if (!isTableExists(checkDB, "sb_companies")) {
				result = false;
			}
			
			if (!isTableExists(checkDB, "sb_imei_companies")) {
				result = false;
			}
			
			if (!isTableExists(checkDB, "sb_users")) {
				result = false;
			}

			if (!isTableExists(checkDB, "sb_vehicles")) {
				result = false;
			}
			
			checkDB.close();
			return result;
		}

		return false;
	}

	public boolean openDataBase() throws SQLException {

		// Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		return (myDataBase != null);
	}

	public static SQLiteDatabase getDataBase() {
		if (myDataBase == null) {
			String myPath = DB_PATH + DB_NAME;
			myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		}
		return myDataBase;

	}

	@Override
	public synchronized void close() {

		if (myDataBase != null) {
			myDataBase.close();
			myDataBase = null;
		}

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	// Add your public helper methods to access and get content from the
	// database.
	// You could return cursors by doing "return myDataBase.query(....)" so it'd
	// be easy
	// to you to create adapters for your views.
	public void dbDelete() {
		String outFileName = DB_PATH + DB_NAME;
		String outFileJour = DB_PATH + DB_NAME + "journal";
		File file = new File(outFileName);
		File fileJour = new File(outFileJour);
		file.delete();
		fileJour.delete();
	}

	public String selectLastSync() {
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery("select * from master_setting", null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToNext();
				String imei = cursor.getString(cursor.getColumnIndexOrThrow("lastsync"));
				cursor.close();
				return imei;
			}
		}
		cursor.close();
		return "";
	}

	public String selectDeviceName() {
		Cursor cursor = DataBaseHelper.getDataBase().rawQuery("select * from master_setting", null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToNext();
				String imei = cursor.getString(cursor.getColumnIndexOrThrow("device_name"));
				cursor.close();
				return imei;
			}
		}
		cursor.close();
		return "";
	}
}