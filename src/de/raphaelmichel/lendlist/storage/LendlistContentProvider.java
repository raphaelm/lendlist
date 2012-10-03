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

	public static final Uri OBJECT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + OBJECT_TYPE);

	private static enum Mime {
		OBJECT_ITEM, OBJECT_DIR
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
			notifyUri(itemUri);
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
		switch (getTypeMime(uri)) {
		case OBJECT_DIR:
			return queryDatabase(Database.OBJECT_TABLE, projection, selection,
					selectionArgs, null, null, null);
		case OBJECT_ITEM:
			return queryDatabase(Database.OBJECT_TABLE, projection,
					Database.OBJECT_WHERE_ID, selectionForUri(uri), null, null,
					null);
		default:
			return null;
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	private void notifyUri(Uri uri) {
		getContext().getContentResolver().notifyChange(uri, null);
	}

	private String[] selectionForUri(Uri uri) {
		return new String[] { String.valueOf(ContentUris.parseId(uri)) };
	}

}
