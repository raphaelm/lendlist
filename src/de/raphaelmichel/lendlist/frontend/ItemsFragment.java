package de.raphaelmichel.lendlist.frontend;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import de.raphaelmichel.lendlist.R;
import de.raphaelmichel.lendlist.objects.Item;
import de.raphaelmichel.lendlist.storage.DataSource;

public class ItemsFragment extends SherlockFragment {

	private static int REQUEST_CODE_DETAILS = 2;

	protected String filter;
	protected String[] filterArgs;
	protected List<Item> items;

	static ItemsFragment newInstance(String direction) {
		return newInstanceFiltered("direction = ?", new String[] { direction });
	}

	protected static ItemsFragment newInstanceFiltered(String filter,
			String[] filterArgs) {
		ItemsFragment f = new ItemsFragment();

		f.filter = filter;
		f.filterArgs = filterArgs;

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
		items = data.getAllItems(filter, filterArgs);
		data.close();

		lvItems.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(getActivity(), DetailsActivity.class);
				i.putExtra("id", items.get(position).getId());
				startActivityForResult(i, REQUEST_CODE_DETAILS);
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

	private class ItemListAdapter extends ArrayAdapter<Item> {
		@Override
		public View getView(int position, View contentView, ViewGroup viewGroup) {
			View view = null;

			if (items.get(position) == null) {
				LayoutInflater layoutInflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = layoutInflater.inflate(R.layout.listitem_main,
						viewGroup, false);
				return view;
			}

			Item item = items.get(position);

			if (contentView == null) {
				LayoutInflater layoutInflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = layoutInflater.inflate(R.layout.listitem_main,
						viewGroup, false);
			} else {
				view = contentView;
			}

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

			return view;
		}

		public ItemListAdapter() {
			super(getActivity(), R.layout.listitem_main, items);
		}
	}

}
