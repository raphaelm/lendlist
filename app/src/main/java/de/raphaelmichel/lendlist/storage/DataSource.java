package de.raphaelmichel.lendlist.storage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import de.raphaelmichel.lendlist.MutableTriplet;
import de.raphaelmichel.lendlist.objects.Category;
import de.raphaelmichel.lendlist.objects.Item;
import de.raphaelmichel.lendlist.objects.Person;

public class DataSource {

	public static void addPhoto(Context context, long object, Uri uri) {
		ContentValues values = new ContentValues();
		values.put("object", object);
		values.put("uri", uri.toString());
		context.getContentResolver().insert(LendlistContentProvider.PHOTO_URI,
				values);
	}

	public static void addItem(Context context, Item item) {
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
		values.put("notified", item.isNotified());
		values.put("category", item.getCategory());
		context.getContentResolver().insert(LendlistContentProvider.OBJECT_URI,
				values);
	}

	public static void addCategory(Context context, Category cat) {
		ContentValues values = new ContentValues();
		if (cat.getId() > 0) {
			values.put("id", cat.getId());
		}
		values.put("name", cat.getName());
		context.getContentResolver().insert(
				LendlistContentProvider.CATEGORY_URI, values);
	}

	public static void markNotified(Context context, long id) {
		ContentValues values = new ContentValues();
		values.put("notified", true);
		context.getContentResolver().update(
				ContentUris.withAppendedId(LendlistContentProvider.OBJECT_URI,
						id), values, null, null);
	}

	public static void updateItem(Context context, Item item) {
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
		values.put("notified", item.isNotified());
		values.put("category", item.getCategory());
		context.getContentResolver().update(
				ContentUris.withAppendedId(LendlistContentProvider.OBJECT_URI,
						item.getId()), values, null, null);
	}

	public static void updateCategory(Context context, Category cat) {
		ContentValues values = new ContentValues();
		values.put("name", cat.getName());
		context.getContentResolver().update(
				ContentUris.withAppendedId(
						LendlistContentProvider.CATEGORY_URI, cat.getId()),
				values, null, null);
	}

	public static void deleteAll(Context context) {
		context.getContentResolver().delete(LendlistContentProvider.OBJECT_URI,
				null, null);
	}

	public static void deleteItem(Context context, long id) {
		context.getContentResolver().delete(
				ContentUris.withAppendedId(LendlistContentProvider.OBJECT_URI,
						id), null, null);
	}

	public static void deleteCategory(Context context, long id) {
		context.getContentResolver().delete(
				ContentUris.withAppendedId(
						LendlistContentProvider.CATEGORY_URI, id), null, null);
	}

	public static void deletePhoto(Context context, long id) {
		context.getContentResolver().delete(
				ContentUris.withAppendedId(LendlistContentProvider.PHOTO_URI,
						id), null, null);
	}

	public static List<MutableTriplet<Long, Uri, Bitmap>> getPhotos(
			Context context, long object) {
		List<MutableTriplet<Long, Uri, Bitmap>> photos = new ArrayList<MutableTriplet<Long, Uri, Bitmap>>();
		Cursor cursor = null;
		ContentResolver resolver = context.getContentResolver();

		cursor = resolver.query(LendlistContentProvider.PHOTO_URI,
				Database.COLUMNS_PHOTOS, "object = ?", new String[] { ""
						+ object }, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			if (cursor.getString(2) != null)
				photos.add(new MutableTriplet<Long, Uri, Bitmap>(cursor
						.getLong(0), Uri.parse(cursor.getString(2)), null));
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return photos;
	}

	public static List<Item> getAllItems(Context context, String filter,
			String[] filterArgs) {
		// direction is ignored if filter is != null!
		List<Item> items = new ArrayList<Item>();
		Cursor cursor = null;
		ContentResolver resolver = context.getContentResolver();

		if (filter == null) {
			cursor = resolver.query(LendlistContentProvider.OBJECT_URI,
					Database.COLUMNS, null, null, null);
		} else {
			cursor = resolver.query(LendlistContentProvider.OBJECT_URI,
					Database.COLUMNS, filter, filterArgs, null);
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

	public static List<Category> getAllCategories(Context context) {
		// direction is ignored if filter is != null!
		List<Category> items = new ArrayList<Category>();
		Cursor cursor = null;
		ContentResolver resolver = context.getContentResolver();

		cursor = resolver.query(LendlistContentProvider.CATEGORY_URI,
				Database.COLUMNS_CATEGORIES, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Category cat = cursorToCategory(cursor);
			items.add(cat);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return items;
	}

	public static List<Person> getPersonList(Context context, String selection,
			String[] selectionArgs) {
		List<Person> items = new ArrayList<Person>();

		String[] proj = { "person", "contact_id", "contact_lookup",
				"COUNT(thing)" };

		Cursor cursor = context.getContentResolver().query(
				LendlistContentProvider.PERSON_URI, proj, selection,
				selectionArgs, null);

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

	public static Item getItem(Context context, long id) {
		Cursor cursor = context.getContentResolver().query(
				ContentUris.withAppendedId(LendlistContentProvider.OBJECT_URI,
						id), Database.COLUMNS, null, null, null);

		Item item = null;

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			item = cursorToItem(cursor);
		}
		// Make sure to close the cursor
		cursor.close();
		return item;
	}

	public static Category getCategory(Context context, long id) {
		Cursor cursor = context.getContentResolver().query(
				ContentUris.withAppendedId(
						LendlistContentProvider.CATEGORY_URI, id),
				Database.COLUMNS_CATEGORIES, null, null, null);

		Category cat = null;

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			cat = cursorToCategory(cursor);
		}
		// Make sure to close the cursor
		cursor.close();
		return cat;
	}

	public static Item cursorToItem(Cursor cursor) {
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
		item.setNotified(cursor.getLong(9) == 1);
		item.setCategory(cursor.getLong(10));
		return item;
	}

	public static Category cursorToCategory(Cursor cursor) {
		Category cat = new Category();
		cat.setId(cursor.getLong(0));
		cat.setName(cursor.getString(1));
		cat.setCount(cursor.getInt(2));
		return cat;
	}

	public static void deleteAllCategories(Context context) {
		context.getContentResolver().delete(
				LendlistContentProvider.CATEGORY_URI, null, null);
	}
}
