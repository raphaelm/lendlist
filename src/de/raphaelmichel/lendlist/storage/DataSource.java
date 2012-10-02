package de.raphaelmichel.lendlist.storage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import de.raphaelmichel.lendlist.objects.Item;
import de.raphaelmichel.lendlist.objects.Person;

public class DataSource {
	private SQLiteDatabase database;
	private Database dbHelper;

	public DataSource(Context context) {
		dbHelper = new Database(context);
	}

	public void openWritable() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void open() throws SQLException {
		database = dbHelper.getReadableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void addItem(Item item) {
		ContentValues values = new ContentValues();
		values.put("direction", item.getDirection());
		values.put("thing", item.getThing());
		values.put("person", item.getPerson());
		values.put("contact_id", item.getContact_id());
		values.put("contact_lookup", item.getContact_lookup());
		values.put("until", (item.getUntil() != null ? item.getUntil()
				.getTime() : 0));
		values.put("date", (item.getDate() != null ? item.getDate().getTime()
				: 0));
		values.put("returned", item.isReturned());
		database.insert("objects", null, values);
	}

	public void updateItem(Item item) {
		ContentValues values = new ContentValues();
		values.put("direction", item.getDirection());
		values.put("thing", item.getThing());
		values.put("person", item.getPerson());
		values.put("contact_id", item.getContact_id());
		values.put("contact_lookup", item.getContact_lookup());
		values.put("until", (item.getUntil() != null ? item.getUntil()
				.getTime() : 0));
		values.put("date", (item.getDate() != null ? item.getDate().getTime()
				: 0));
		values.put("returned", item.isReturned());
		String[] selA = { item.getId() + "" };
		database.update("objects", values, "id = ?", selA);
	}

	public void deleteItem(long id) {
		String[] selA = { "" + id };
		database.delete("objects", "id = ?", selA);
	}

	public List<Item> getAllItems(String direction, String filter,
			String[] filterArgs) {
		// direction is ignored if filter is != null!
		List<Item> items = new ArrayList<Item>();
		String[] selA = { direction };
		Cursor cursor = null;

		if (filter == null) {
			if (direction == null) {
				cursor = database.query("objects", Database.COLUMNS, null,
						null, null, null, null);
			} else {
				cursor = database.query("objects", Database.COLUMNS,
						"direction = ?", selA, null, null, null);
			}
		} else {
			cursor = database.query("objects", Database.COLUMNS, filter, filterArgs,
					null, null, null);
		}

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Item item = cursorToItem(cursor);
			items.add(item);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return items;
	}

	public List<Person> getPersonList() {
		List<Person> items = new ArrayList<Person>();

		String[] proj = { "person", "contact_id", "contact_lookup",
				"COUNT(thing)" };

		Cursor cursor = database.query("objects", proj, null, null, "person",
				null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Person p = new Person();
			p.setName(cursor.getString(0));
			p.setId(cursor.getLong(1));
			p.setLookup(cursor.getString(2));
			p.setCount(cursor.getInt(3));
			items.add(p);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return items;
	}

	public Item getItem(long id) {
		String[] selA = { "" + id };
		Cursor cursor = database.query("objects", Database.COLUMNS, "id = ?",
				selA, null, null, null);

		Item item = null;

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			item = cursorToItem(cursor);
		}
		// Make sure to close the cursor
		cursor.close();
		return item;
	}

	private Item cursorToItem(Cursor cursor) {
		Item item = new Item();
		item.setId(cursor.getLong(0));
		item.setDirection(cursor.getString(1));
		item.setThing(cursor.getString(2));
		item.setPerson(cursor.getString(3));
		item.setContact_id(cursor.getLong(4));
		item.setUntil(cursor.getLong(5) > 0 ? new Date(cursor.getLong(5))
				: null);
		item.setDate(cursor.getLong(6) > 0 ? new Date(cursor.getLong(6)) : null);
		item.setReturned(cursor.getLong(7) == 1);
		item.setContact_lookup(cursor.getString(8));
		return item;
	}
}
