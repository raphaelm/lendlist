package de.raphaelmichel.lendlist.frontend;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import de.raphaelmichel.lendlist.R;
import de.raphaelmichel.lendlist.objects.Item;
import de.raphaelmichel.lendlist.storage.DataSource;
import de.raphaelmichel.lendlist.storage.Database;
import de.raphaelmichel.lendlist.storage.LendlistContentProvider;

public class ItemsFragment extends SherlockFragment implements
		LoaderCallbacks<Cursor> {

	private static int REQUEST_CODE_DETAILS = 2;

	private String filter;
	private String[] filterArgs;
	private boolean created = false;

	private ItemListAdapter adapter;

	public static ItemsFragment newInstance(String direction) {
		ItemsFragment f = new ItemsFragment();
		f.setFilter("direction = ?", new String[] { direction });
		return f;
	}

	public static ItemsFragment newInstanceFiltered(String filter,
			String[] filterArgs) {
		ItemsFragment f = new ItemsFragment();
		f.setFilter(filter, filterArgs);
		return f;
	}

	public void setFilter(String filter, String[] filterArgs) {
		this.filter = filter;
		this.filterArgs = filterArgs;
		if (created) {
			getLoaderManager().restartLoader(0, null, this);
		}
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
		created = true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		created = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main, container, false);

		ListView list = (ListView) v.findViewById(R.id.lvItems);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(getActivity(), DetailsActivity.class);
				i.putExtra("id", id);
				startActivityForResult(i, REQUEST_CODE_DETAILS);
			}
		});
		list.setClickable(true);
		list.setTextFilterEnabled(false);

		return v;
	}

	private class ItemListAdapter extends SimpleCursorAdapter {
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			Item item = DataSource.cursorToItem(cursor);

			TextView tvThing = (TextView) view.findViewById(R.id.tvThing);
			tvThing.setText(item.getThing());
			TextView tvPerson = (TextView) view.findViewById(R.id.tvPerson);
			tvPerson.setText(item.getPerson());

			TextView tvUntil = (TextView) view.findViewById(R.id.tvUntil);
			if (item.getUntil() != null)
				tvUntil.setText(getString(R.string.until,
						new SimpleDateFormat(getString(R.string.date_format))
								.format(item.getUntil())));
			else {
				if (item.getDate() != null)
					tvUntil.setText(getString(R.string.since,
							new SimpleDateFormat(
									getString(R.string.date_format))
									.format(item.getDate())));
			}
		}

		public ItemListAdapter() {
			super(getActivity(), R.layout.listitem_main, null,
					new String[] { "direction" }, null, 0);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(),
				LendlistContentProvider.OBJECT_URI, Database.COLUMNS, filter,
				filterArgs, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}

}
