package de.raphaelmichel.lendlist.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "objects.db";
	public static final int DATABASE_VERSION = 6; // REPLACE ONUPGRADE IF YOU
													// CHANGE THIS

	private static final String DATABASE_CREATE = "create table "
			+ "objects ( id integer primary key autoincrement,"
			+ " direction text," + " thing text," + " person text,"
			+ " contact_id integer," + " until integer," + " date integer,"
			+ " returned integer," + " contact_lookup text" + ");" 
			+ "create table photos ( id integer primary key autoincrement,"
			+ "object integer," + " uri text"+ ");";
	public static final String[] COLUMNS = { "id AS _id", "direction", "thing",
			"person", "contact_id", "until", "date", "returned",
			"contact_lookup" };
	public static final String OBJECT_TABLE = "objects";
	public static final String OBJECT_WHERE_ID = "id = ?";
	public static final String PHOTO_TABLE = "photos";
	public static final String PHOTO_WHERE_ID = "id = ?";
	public static final String PHOTO_WHERE_OBJECT = "object = ?";

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(Database.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS objects");
		onCreate(db);
	}

	public Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

}
