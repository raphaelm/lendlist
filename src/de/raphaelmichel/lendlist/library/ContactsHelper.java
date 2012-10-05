package de.raphaelmichel.lendlist.library;

import java.io.InputStream;

import de.raphaelmichel.lendlist.R;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

public final class ContactsHelper {

	public static Bitmap getPhoto(Uri uri, Context ctx) {
		long contactId;
		try {
			Uri contactLookupUri = uri;
			Cursor c = ctx.getContentResolver().query(contactLookupUri,
					new String[] { ContactsContract.Contacts._ID }, null, null,
					null);
			try {
				if (c == null || c.moveToFirst() == false) {
					return null;
				}
				contactId = c.getLong(0);
			} finally {
				c.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		Uri contactUri = ContentUris.withAppendedId(
				ContactsContract.Contacts.CONTENT_URI, contactId);
		InputStream input = ContactsContract.Contacts
				.openContactPhotoInputStream(ctx.getContentResolver(),
						contactUri);

		if (input != null) {
			return BitmapFactory.decodeStream(input);
		} else {
			return BitmapFactory.decodeResource(ctx.getResources(),
					R.drawable.ic_contact_picture);
		}
	}
}
