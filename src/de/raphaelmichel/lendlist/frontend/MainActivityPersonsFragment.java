package de.raphaelmichel.lendlist.frontend;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import de.raphaelmichel.lendlist.ContactsHelper;
import de.raphaelmichel.lendlist.R;
import de.raphaelmichel.lendlist.objects.Person;
import de.raphaelmichel.lendlist.storage.LendlistContentProvider;

public class MainActivityPersonsFragment extends SherlockFragment implements
		LoaderCallbacks<Cursor> {

	private ItemListAdapter adapter;

	static MainActivityPersonsFragment newInstance() {
		MainActivityPersonsFragment f = new MainActivityPersonsFragment();

		return f;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_main, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getLoaderManager().initLoader(0, null, this);
		adapter = new ItemListAdapter();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main, container, false);

		ListView lvItems = (ListView) v.findViewById(R.id.lvItems);

		lvItems.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Person person = cursorToPerson((Cursor) parent
						.getItemAtPosition(position));
				Intent i = new Intent(getActivity(), PersonLookupActivity.class);
				i.putExtra("name", person.getName());
				if (person.getId() > 0) {
					Uri contactUri = ContactsContract.Contacts.getLookupUri(
							person.getId(), person.getLookup());
					i.putExtra("uri", contactUri.toString());
				}
				startActivity(i);
			}
		});
		lvItems.setClickable(true);
		lvItems.setAdapter(adapter);
		lvItems.setTextFilterEnabled(false);

		return v;
	}

	private class ItemListAdapter extends SimpleCursorAdapter {
		Map<Uri, Bitmap> bitmapcache = new HashMap<Uri, Bitmap>();

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			Person person = cursorToPerson(cursor);

			TextView tvName = (TextView) view.findViewById(R.id.tvName);
			tvName.setText(person.getName());

			TextView tvCount = (TextView) view.findViewById(R.id.tvCount);
			tvCount.setText("" + person.getCount());

			ImageView ivPhoto = (ImageView) view.findViewById(R.id.ivPhoto);

			if (person.getId() > 0) {
				Uri contactUri = ContactsContract.Contacts.getLookupUri(
						person.getId(), person.getLookup());

				if (bitmapcache.containsKey(contactUri)) {
					ivPhoto.setImageBitmap(bitmapcache.get(contactUri));
				} else {
					Bitmap bm = ContactsHelper.getPhoto(contactUri,
							getActivity());
					// Bitmap thumb = Bitmap.createScaledBitmap(bm, 96, 96,
					// false);
					bitmapcache.put(contactUri, bm);
					ivPhoto.setImageBitmap(bm);
				}
			} else {
				ivPhoto.setImageResource(R.drawable.ic_contact_picture);
			}
		}

		public ItemListAdapter() {
			super(getActivity(), R.layout.listitem_person, null,
					new String[] { "_id" }, null, 0);
		}
	}

	@Override
	public void onDestroy() {
		if (getView() != null) {
			unbindDrawables(getView().findViewById(R.id.llMain));
		}
		super.onDestroy();
		System.gc();
	}

	protected void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			if (!(view instanceof AdapterView)) {
				((ViewGroup) view).removeAllViews();
			}
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(),
				LendlistContentProvider.PERSON_URI, new String[] { "person",
						"contact_id", "contact_lookup", "COUNT(thing)",
						"id AS _id" }, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}

	private Person cursorToPerson(Cursor cursor) {
		Person person = new Person();
		person.setName(cursor.getString(0));
		person.setId(cursor.getLong(1));
		person.setLookup(cursor.getString(2));
		person.setCount(cursor.getInt(3));
		return person;
	}

}
