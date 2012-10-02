package de.raphaelmichel.lendlist.frontend;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import de.raphaelmichel.lendlist.R;
import de.raphaelmichel.lendlist.objects.Person;
import de.raphaelmichel.lendlist.storage.DataSource;

public class MainActivityPersonsFragment extends SherlockFragment {

	private List<Person> persons;

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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main, container, false);

		refresh(v);

		return v;
	}

	private void refresh(View v) {
		ListView lvItems = (ListView) v.findViewById(R.id.lvItems);

		DataSource data = new DataSource(getActivity());
		data.open();
		persons = data.getPersonList();
		data.close();

		lvItems.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Intent i = new Intent(getActivity(), DetailsActivity.class);
				// i.putExtra("id", items.get(position).getId());
				// startActivityForResult(i, REQUEST_CODE_DETAILS);
			}
		});
		lvItems.setClickable(true);
		lvItems.setAdapter(new ItemListAdapter());
		lvItems.setTextFilterEnabled(false);

	}

	@Override
	public void onResume() {
		super.onResume();
		refresh();
	}

	public void refresh() {
		refresh(getView());
	}

	public Bitmap getPhoto(Uri uri) {
		long contactId;
		try {
			Uri contactLookupUri = uri;
			Cursor c = getActivity().getContentResolver().query(
					contactLookupUri,
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
				.openContactPhotoInputStream(
						getActivity().getContentResolver(), contactUri);

		if (input != null) {
			return BitmapFactory.decodeStream(input);
		} else {
			return BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_contact_picture);
		}
	}

	private class ItemListAdapter extends ArrayAdapter<Person> {
		Map<Uri, Bitmap> bitmapcache = new HashMap<Uri, Bitmap>();

		@Override
		public View getView(int position, View contentView, ViewGroup viewGroup) {
			View view = null;

			if (persons.get(position) == null) {
				LayoutInflater layoutInflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = layoutInflater.inflate(R.layout.listitem_person,
						viewGroup, false);
				return view;
			}

			Person person = persons.get(position);

			if (contentView == null) {
				LayoutInflater layoutInflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = layoutInflater.inflate(R.layout.listitem_person,
						viewGroup, false);
			} else {
				view = contentView;
			}

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
					Bitmap bm = getPhoto(contactUri);
					// Bitmap thumb = Bitmap.createScaledBitmap(bm, 96, 96,
					// false);
					bitmapcache.put(contactUri, bm);
					ivPhoto.setImageBitmap(bm);
				}
			} else {
				ivPhoto.setImageResource(R.drawable.ic_contact_picture);
			}

			return view;
		}

		public ItemListAdapter() {
			super(getActivity(), R.layout.listitem_main, persons);
		}
	}

	@Override
	public void onDestroy() {
		unbindDrawables(getView().findViewById(R.id.llMain));
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

}
