package de.raphaelmichel.lendlist.frontend;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import de.raphaelmichel.lendlist.R;
import de.raphaelmichel.lendlist.library.RelativeDateBuilder;
import de.raphaelmichel.lendlist.library.RelativeDateBuilder.RelativeDate;
import de.raphaelmichel.lendlist.objects.Item;
import de.raphaelmichel.lendlist.storage.DataSource;
import de.raphaelmichel.lendlist.storage.Database;
import de.raphaelmichel.lendlist.storage.LendlistContentProvider;

public class ItemsFragment extends Fragment implements LoaderCallbacks<Cursor> {

	private static int REQUEST_CODE_ADD = 1;
	private static int REQUEST_CODE_DETAILS = 2;

	public static final String DEFAULT_ORDER = "date DESC";

	private String filter;
	private String[] filterArgs;
	private String orderBy = DEFAULT_ORDER;
	private boolean created = false;

	private ItemListAdapter adapter;

	public static ItemsFragment newInstance(String filter, String[] filterArgs) {
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

	public void setOrder(String orderBy) {
		this.orderBy = orderBy;
		if (created) {
			getLoaderManager().restartLoader(0, null, this);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (menu.findItem(R.id.action_add) == null)
			inflater.inflate(R.menu.fragment_main, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_add
				&& !(getActivity() instanceof MainActivity)) {
			Intent i = new Intent(getActivity(), AddActivity.class);
			if (getActivity() instanceof CategoryLookupActivity)
				i.putExtra("category", Long.parseLong(filterArgs[0]));
			else if (getActivity() instanceof PersonLookupActivity) {
				i.putExtra("name", ((PersonLookupActivity) getActivity())
						.getIntent().getStringExtra("name"));
				if (getActivity().getIntent().hasExtra("person_lookup")) {
					i.putExtra("person_id", getActivity().getIntent()
							.getLongExtra("person_id", 0));
					i.putExtra("person_lookup", getActivity().getIntent()
							.getStringExtra("person_lookup"));
				}
			}
			startActivityForResult(i, REQUEST_CODE_ADD);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			filter = savedInstanceState.getString("filter");
			filterArgs = savedInstanceState.getStringArray("filterArgs");
			orderBy = savedInstanceState.getString("orderBy");
		}

		setHasOptionsMenu(true);

		getLoaderManager().initLoader(0, null, this);
		adapter = new ItemListAdapter();
		created = true;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("filter", filter);
		outState.putStringArray("filterArgs", filterArgs);
		outState.putString("orderBy", orderBy);
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

		RelativeDateBuilder rdb;

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			Item item = DataSource.cursorToItem(cursor);

			TextView tvThing = (TextView) view.findViewById(R.id.tvThing);
			tvThing.setText(item.getThing());
			TextView tvPerson = (TextView) view.findViewById(R.id.tvPerson);

			if (getActivity() instanceof PersonLookupActivity
					|| (getActivity() instanceof CategoryLookupActivity && item
							.getPerson() == null)) {
				if (item.getDirection().equals("lent")) {
					tvPerson.setText(R.string.itemlist_dir_lent);
					tvPerson.setTextColor(getResources().getColor(
							R.color.itemlist_dir_lent));
				} else {
					tvPerson.setText(R.string.itemlist_dir_borrowed);
					tvPerson.setTextColor(getResources().getColor(
							R.color.itemlist_dir_borrowed));
				}
			} else if (getActivity() instanceof CategoryLookupActivity) {
				if (item.getDirection().equals("lent")) {
					tvPerson.setText(getString(R.string.itemlist_dir_lent_to,
							item.getPerson()));
					tvPerson.setTextColor(getResources().getColor(
							R.color.itemlist_dir_lent));
				} else {
					tvPerson.setText(getString(
							R.string.itemlist_dir_borrowed_from,
							item.getPerson()));
					tvPerson.setTextColor(getResources().getColor(
							R.color.itemlist_dir_borrowed));
				}
			} else {
				tvPerson.setText(item.getPerson());
			}
			TextView tvUntil = (TextView) view.findViewById(R.id.tvUntil);
			if (item.isReturned()) {
				tvUntil.setText(R.string.itemlist_returned);
				tvUntil.setTextColor(getResources().getColor(
						R.color.itemlist_returned));
			} else if (item.getUntil() != null) {
				RelativeDate rd = rdb.build(item.getUntil());
				tvUntil.setText(rd.text);
				tvUntil.setTextColor(rd.color);
			} else {
				tvUntil.setText("");
			}
		}

		public ItemListAdapter() {
			super(getActivity(), R.layout.listitem_main, null,
					new String[] { "direction" }, null, 0);

			rdb = new RelativeDateBuilder(getActivity());
			rdb.setColors(R.color.itemlist_date_neutral,
					R.color.itemlist_date_allgood, R.color.itemlist_date_soon,
					R.color.itemlist_date_over);
			rdb.setStrings(R.string.date_format, R.string.date_days_left,
					R.string.date_days_left_1, R.string.date_days_left_0,
					R.string.date_days_over, R.string.date_days_over_1);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(),
				LendlistContentProvider.OBJECT_URI, Database.COLUMNS, filter,
				filterArgs, orderBy);
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
