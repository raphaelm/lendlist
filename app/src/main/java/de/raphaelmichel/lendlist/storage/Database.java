package de.raphaelmichel.lendlist.storage;

import de.raphaelmichel.lendlist.R;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "objects.db";
	public static final int DATABASE_VERSION = 13; // REPLACE ONUPGRADE IF YOU
													// CHANGE THIS

	public static final String[] COLUMNS = { "id AS _id", "direction", "thing",
			"person", "contact_id", "until", "date", "returned",
			"contact_lookup", "notified", "category" };

	public static final String[] COLUMNS_PHOTOS = { "id AS _id", "object",
			"uri" };

	public static final String[] COLUMNS_CATEGORIES = { "id AS _id", "name",
			"(SELECT COUNT(*) FROM objects WHERE objects.category=categories.id) as count" };

	public static final String OBJECT_TABLE = "objects";
	public static final String OBJECT_WHERE_ID = "id = ?";
	public static final String OBJECT_WHERE_CAT = "category = ?";
	public static final String CATEGORY_TABLE = "categories";
	public static final String CATEGORY_WHERE_ID = "id = ?";
	public static final String PHOTO_TABLE = "photos";
	public static final String PHOTO_WHERE_ID = "id = ?";
	public static final String PHOTO_WHERE_OBJECT = "object = ?";

	private Context context;

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table "
				+ "objects ( id integer primary key autoincrement,"
				+ " direction text," + " thing text," + " person text,"
				+ " contact_id integer," + " until integer," + " date integer,"
				+ " returned integer," + " contact_lookup text,"
				+ " notified integer" + ", category integer);");
		db.execSQL("create table photos ( id integer primary key autoincrement,"
				+ "object integer," + " uri text" + ");");
		db.execSQL("create table categories ( id integer primary key autoincrement,"
				+ "name text);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO: Provide something here if you change the database version
		if ((oldVersion < 10 && newVersion == 10)
				|| (oldVersion < 11 && newVersion == 11)) {
			try {
				db.execSQL("ALTER TABLE objects ADD COLUMN notified numeric");
			} catch (SQLiteException e) {
				return;
			}
		}
		if (oldVersion < 12) {
			db.execSQL("create table categories ( id integer primary key autoincrement,"
					+ "name text);");
			db.execSQL("ALTER TABLE objects ADD COLUMN category numeric");
		}
		if (oldVersion < 13) {
			ContentValues values = new ContentValues();
			values.put("name", context.getString(R.string.category_default_0));
			db.insert("categories", null, values);
			values = new ContentValues();
			values.put("name", context.getString(R.string.category_default_1));
			db.insert("categories", null, values);
			values = new ContentValues();
			values.put("name", context.getString(R.string.category_default_2));
			db.insert("categories", null, values);
		}
		// if (db.isReadOnly())
		// db = getWritableDatabase();
		//
		// String backupFile = null;
		// try {
		// backupFile = BackupHelper.writeInternalBackup(context);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// db.execSQL("DROP TABLE IF EXISTS objects");
		// db.execSQL("DROP TABLE IF EXISTS photos");
		// onCreate(db);
		// try {
		// BackupHelper.importInternalBackup(context, backupFile);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	public Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

}
