package de.raphaelmichel.lendlist.frontend;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import de.raphaelmichel.lendlist.R;
import de.raphaelmichel.lendlist.objects.Category;
import de.raphaelmichel.lendlist.storage.DataSource;
import de.raphaelmichel.lendlist.storage.Database;
import de.raphaelmichel.lendlist.storage.LendlistContentProvider;

public class CategoriesFragment extends Fragment implements
		LoaderCallbacks<Cursor> {

	public static final String SORT_ORDER = "name ASC";

	private ItemListAdapter adapter;

	private String filter;
	private String[] filterArgs;
	private boolean created = false;

	static CategoriesFragment newInstance(String filter, String[] filterArgs) {
		CategoriesFragment f = new CategoriesFragment();
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
		if (menu.findItem(R.id.action_add_category) == null)
			inflater.inflate(R.menu.fragment_category, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		if (savedInstanceState != null) {
			filter = savedInstanceState.getString("filter");
			filterArgs = savedInstanceState.getStringArray("filterArgs");
		}

		getLoaderManager().initLoader(0, null, this);
		adapter = new ItemListAdapter();
		created = true;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("filter", filter);
		outState.putStringArray("filterArgs", filterArgs);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main, container, false);

		ListView lvItems = (ListView) v.findViewById(R.id.lvItems);

		lvItems.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Category cat = DataSource.cursorToCategory((Cursor) parent
						.getItemAtPosition(position));
				Intent i = new Intent(getActivity(),
						CategoryLookupActivity.class);
				i.putExtra("name", cat.getName());
				i.putExtra("category", cat.getId());
				startActivity(i);
			}
		});
		lvItems.setClickable(true);
		lvItems.setAdapter(adapter);
		lvItems.setTextFilterEnabled(false);

		return v;
	}

	private class ItemListAdapter extends SimpleCursorAdapter {
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			Category cat = DataSource.cursorToCategory(cursor);

			TextView tvName = (TextView) view.findViewById(R.id.tvName);
			tvName.setText(cat.getName());
			TextView tvCount = (TextView) view.findViewById(R.id.tvCount);
			tvCount.setText(String.valueOf(cat.getCount()));
		}

		public ItemListAdapter() {
			super(getActivity(), R.layout.listitem_category, null,
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
		created = false;
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
				LendlistContentProvider.CATEGORY_URI,
				Database.COLUMNS_CATEGORIES, null, null, SORT_ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_add_category:
			final EditText tvName = new EditText(getActivity());
			AlertDialog.Builder promptB = new AlertDialog.Builder(getActivity());
			promptB.setCancelable(true)
					.setTitle(R.string.add_category)
					.setPositiveButton(R.string.accept,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Category cat = new Category();
									cat.setName(tvName.getText().toString());
									DataSource.addCategory(getActivity(), cat);
									dialog.dismiss();
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							}).setView(tvName);

			AlertDialog prompt = promptB.create();
			prompt.setView(tvName, 10, 10, 10, 10);
			prompt.show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
