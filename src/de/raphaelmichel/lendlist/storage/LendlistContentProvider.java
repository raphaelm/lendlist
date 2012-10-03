package de.raphaelmichel.lendlist.storage;

import java.util.List;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class LendlistContentProvider extends ContentProvider {
	private Database database;

	private static final String AUTHORITY = "de.raphaelmichel.lendlist.provider";
	private static final String OBJECT_TYPE = "object";
	private static final String PERSON_TYPE = "person";

	private static final String BASE_URI = "content://" + AUTHORITY + "/";
	public static final Uri OBJECT_URI = Uri.parse(BASE_URI + OBJECT_TYPE);
	public static final Uri PERSON_URI = Uri.parse(BASE_URI + PERSON_TYPE);

	private static enum Mime {
		OBJECT_ITEM, OBJECT_DIR, PERSON_DIR
	}

	@Override
	public boolean onCreate() {
		database = new Database(getContext());
		return true;
	}

	private static final String MIME_PREFIX = "vnd.android.cursor.";
	private static final String OBJECT_MIME_POSTFIX = "/vnd." + AUTHORITY + "."
			+ OBJECT_TYPE;
	private static final String OBJECT_DIR_MIME = MIME_PREFIX + "dir"
			+ OBJECT_MIME_POSTFIX;
	private static final String OBJECT_ITEM_MIME = MIME_PREFIX + "item"
			+ OBJECT_MIME_POSTFIX;
	private static final String PERSON_DIR_MIME = MIME_PREFIX + "dir" + "/vnd."
			+ AUTHORITY + "." + PERSON_TYPE;

	private static Mime getTypeMime(Uri uri) {
		if (!AUTHORITY.equals(uri.getAuthority())) {
			return null;
		}
		List<String> segments = uri.getPathSegments();
		if (segments == null || segments.size() == 0) {
			return null;
		}

		String type = segments.get(0);
		if (OBJECT_TYPE.equals(type)) {
			switch (segments.size()) {
			case 1:
				return Mime.OBJECT_DIR;
			case 2:
				return Mime.OBJECT_ITEM;
			default:
				return null;
			}
		} else if (PERSON_TYPE.equals(type)) {
			if (segments.size() == 1) {
				return Mime.PERSON_DIR;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public String getType(Uri uri) {
		switch (getTypeMime(uri)) {
		case OBJECT_DIR:
			return OBJECT_DIR_MIME;
		case OBJECT_ITEM:
			return OBJECT_ITEM_MIME;
		case PERSON_DIR:
			return PERSON_DIR_MIME;
		default:
			return null;
		}
	}

	private int deleteInDatabase(String table, String whereClause,
			String[] whereArgs) {
		return database.getWritableDatabase().delete(table, whereClause,
				whereArgs);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int rowsAffected;
		switch (getTypeMime(uri)) {
		case OBJECT_DIR:
			rowsAffected = deleteInDatabase(Database.OBJECT_TABLE, selection,
					selectionArgs);
			break;
		case OBJECT_ITEM:
			rowsAffected = deleteInDatabase(Database.OBJECT_TABLE,
					Database.OBJECT_WHERE_ID, selectionForUri(uri));
			break;
		default:
			rowsAffected = 0;
			break;
		}

		if (rowsAffected > 0) {
			notifyUri(uri);
		}
		return rowsAffected;
	}

	private long insertIntoDatabase(String table, ContentValues values) {
		return database.getWritableDatabase()
				.insertOrThrow(table, null, values);
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri itemUri;
		switch (getTypeMime(uri)) {
		case OBJECT_DIR:
			long id = insertIntoDatabase(Database.OBJECT_TABLE, values);
			itemUri = ContentUris.withAppendedId(OBJECT_URI, id);
			break;
		default:
			itemUri = null;
			break;
		}
		if (itemUri != null) {
			notifyUri(uri);
		}
		return itemUri;
	}

	private Cursor queryDatabase(String table, String[] projection,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) {
		return database.getReadableDatabase().query(table, projection,
				selection, selectionArgs, groupBy, having, orderBy);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor cursor;
		switch (getTypeMime(uri)) {
		case OBJECT_DIR:
			cursor = queryDatabase(Database.OBJECT_TABLE, projection,
					selection, selectionArgs, null, null, null);
			break;
		case OBJECT_ITEM:
			cursor = queryDatabase(Database.OBJECT_TABLE, projection,
					Database.OBJECT_WHERE_ID, selectionForUri(uri), null, null,
					null);
			break;
		case PERSON_DIR:
			cursor = queryDatabase(Database.OBJECT_TABLE, projection,
					selection, selectionArgs, "person", null, null);
			break;
		default:
			return null;
		}
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	private int updateInDatabase(String table, ContentValues values,
			String selection, String[] selectionArgs) {
		return database.getWritableDatabase().update(table, values, selection,
				selectionArgs);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int rowsAffected;
		switch (getTypeMime(uri)) {
		case OBJECT_DIR:
			rowsAffected = updateInDatabase(Database.OBJECT_TABLE, values,
					selection, selectionArgs);
			break;
		case OBJECT_ITEM:
			rowsAffected = updateInDatabase(Database.OBJECT_TABLE, values,
					Database.OBJECT_WHERE_ID, selectionForUri(uri));
			break;
		default:
			rowsAffected = 0;
			break;
		}

		if (rowsAffected > 0) {
			notifyUri(uri);
		}
		return rowsAffected;
	}

	private void notifyUri(Uri uri) {
		getContext().getContentResolver().notifyChange(uri, null);
	}

	private String[] selectionForUri(Uri uri) {
		return new String[] { String.valueOf(ContentUris.parseId(uri)) };
	}

}
