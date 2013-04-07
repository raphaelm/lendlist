package de.raphaelmichel.lendlist.frontend;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ViewGroup;

import org.holoeverywhere.app.AlertDialog;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.raphaelmichel.lendlist.R;
import de.raphaelmichel.lendlist.storage.DataSource;

public class MainActivity extends SherlockFragmentActivity {

	private static int REQUEST_CODE_ADD = 1;

	private static int FRAGMENT_TYPE_ITEMS = 0;
	private static int FRAGMENT_TYPE_PERSONS = 1;
	private static int FRAGMENT_TYPE_CATEGORIES = 2;
	private static String[] DIRECTIONS = { "borrowed", "lent" };

	private static int[][] FRAGMENTS = new int[][] {
			new int[] { R.string.categories, 0, FRAGMENT_TYPE_CATEGORIES },
			new int[] { R.string.borrowed, 0, FRAGMENT_TYPE_ITEMS },
			new int[] { R.string.lent, 1, FRAGMENT_TYPE_ITEMS },
			new int[] { R.string.persons, 0, FRAGMENT_TYPE_PERSONS } };

	private ViewPager mViewPager;
	private MainFragmentAdapter fragmentAdapter;
	private SharedPreferences sp;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sp = PreferenceManager.getDefaultSharedPreferences(this);

		if (getIntent().getExtras() != null) {
			if (getIntent().getExtras().containsKey("notified")) {
				int[] ids = getIntent().getExtras().getIntArray("notified");
				int num = ids.length;
				for (int i = 0; i < num; i++) {
					DataSource.markNotified(this, ids[i]);
				}
			}
		}

		if (findViewById(R.id.viewpager) != null) // Phones
			installViewpager();
		else if (findViewById(R.id.fragment1) != null) // Tablets
			installSidebyside();
	}

	public Fragment getFragment(int num) {
		return getSupportFragmentManager()
				.findFragmentByTag("mainpager:" + num);
	}

	public void installSidebyside() {
		FragmentManager fm = getSupportFragmentManager();

		CategoriesFragment ifCategories = (CategoriesFragment) fm
				.findFragmentByTag("mainpager:0"); // TODO: ON TABLETS
		ItemsFragment ifBorrowed = (ItemsFragment) fm
				.findFragmentByTag("mainpager:1");
		ItemsFragment ifLent = (ItemsFragment) fm
				.findFragmentByTag("mainpager:2");
		PersonsFragment ifPersons = (PersonsFragment) fm
				.findFragmentByTag("mainpager:3");

		String filter = "direction = 'borrowed'";
		if (!sp.getBoolean("show_returned", false)) {
			filter = filter + " and returned = 0";
		}
		ifBorrowed.setFilter(filter, null);
		ifBorrowed.setOrder(sp.getString("order_by",
				ItemsFragment.DEFAULT_ORDER));

		filter = "direction = 'lent'";
		if (!sp.getBoolean("show_returned", false)) {
			filter = filter + " and returned = 0";
		}
		ifLent.setFilter(filter, null);
		ifLent.setOrder(sp.getString("order_by", ItemsFragment.DEFAULT_ORDER));

		filter = null;
		if (!sp.getBoolean("show_returned", false)) {
			filter = "returned = 0";
		}
		ifPersons.setFilter(filter, null);
	}

	public void installViewpager() {
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		fragmentAdapter = new MainFragmentAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(fragmentAdapter);
		if (getIntent().getExtras() != null) {
			mViewPager.setCurrentItem(getIntent().getExtras().getInt(
					"main_tab", sp.getInt("main_tab", 1)));
		} else {
			mViewPager.setCurrentItem(sp.getInt("main_tab", 1));
		}
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setOnPageChangeListener(new SavePageListener());
	}

	public class SavePageListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int position) {
			sp.edit().putInt("main_tab", position).commit();
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);

		if (sp == null)
			sp = PreferenceManager.getDefaultSharedPreferences(this);

		if (sp.getBoolean("show_returned", false)) {
			menu.findItem(R.id.action_filter).setTitle(R.string.returned_hide);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_add:
			Intent i = new Intent(this, AddActivity.class);
			if (mViewPager != null)
				i.putExtra("direction",
						DIRECTIONS[FRAGMENTS[mViewPager.getCurrentItem()][1]]);
			startActivityForResult(i, REQUEST_CODE_ADD);
			return true;

		case R.id.action_filter:
			String filter = "direction = ?";
			String filterPersons = null;

			if (sp.getBoolean("show_returned", false)) {
				sp.edit().putBoolean("show_returned", false).commit();
				item.setTitle(R.string.returned_show);
				filter = "direction = ? and returned = 0";
				filterPersons = "returned = 0";
			} else {
				sp.edit().putBoolean("show_returned", true).commit();
				item.setTitle(R.string.returned_hide);
			}

			int number_of_fragments = FRAGMENTS.length;
			for (int j = 0; j < number_of_fragments; j++) {
				if (FRAGMENTS[j][2] == FRAGMENT_TYPE_ITEMS) {
					String[] filterArgs = { DIRECTIONS[FRAGMENTS[j][1]] };
					((ItemsFragment) getFragment(j)).setFilter(filter,
							filterArgs);
				} else if (FRAGMENTS[j][2] == FRAGMENT_TYPE_PERSONS) {
					String[] filterArgs = {};
					((PersonsFragment) getFragment(j)).setFilter(filterPersons,
							filterArgs);
				}
			}

			return true;

		case R.id.action_order:
			selectOrder();
			return true;

		case R.id.menu_export:
			Intent iExport = new Intent(this, ExportActivity.class);
			startActivity(iExport);
			return true;

		case R.id.menu_settings:
			Intent iPref = new Intent(this, PreferenceActivity.class);
			startActivity(iPref);
			return true;

		case R.id.menu_about:
			Intent iAbout = new Intent(this, AboutActivity.class);
			startActivity(iAbout);
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	public void selectOrder() {
		final CharSequence[] items = { getString(R.string.order_alphabetical),
				getString(R.string.order_date),
				getString(R.string.order_return),
				getString(R.string.order_person) };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.order);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int n) {
				String orderBy = ItemsFragment.DEFAULT_ORDER;
				switch (n) {
				case 0: // Alphabetical
					orderBy = "thing ASC";
					break;
				case 1: // Date
					orderBy = "date DESC";
					break;
				case 2: // Return
					orderBy = "returned ASC, until ASC";
					break;
				case 3: // Person
					orderBy = "person ASC";
					break;
				}
				sp.edit().putString("order_by", orderBy).commit();

				int number_of_fragments = FRAGMENTS.length;
				for (int j = 0; j < number_of_fragments; j++) {
					if (FRAGMENTS[j][2] == FRAGMENT_TYPE_ITEMS) {
						((ItemsFragment) getFragment(j)).setOrder(orderBy);
					} else if (FRAGMENTS[j][2] == FRAGMENT_TYPE_PERSONS) {
						// nothing right now
					}
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public class MainFragmentAdapter extends FragmentStatePagerAdapter {
		public Fragment[] fragments = new Fragment[FRAGMENTS.length];

		public MainFragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getString(FRAGMENTS[position][0]);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			super.destroyItem(container, position, object);
			fragments[position] = null;
		}

		@Override
		public Fragment getItem(int position) {
			if (fragments[position] != null) {
				// Cache them!
				return fragments[position];
			}

			if (FRAGMENTS[position][2] == FRAGMENT_TYPE_ITEMS) {
				String filter = "direction = ?";
				String[] filterArgs = { DIRECTIONS[FRAGMENTS[position][1]] };

				if (!sp.getBoolean("show_returned", false)) {
					filter = "direction = ? and returned = 0";
					filterArgs = new String[] { DIRECTIONS[FRAGMENTS[position][1]] };
				}

				fragments[position] = ItemsFragment.newInstance(filter,
						filterArgs);
				((ItemsFragment) fragments[position]).setOrder(sp.getString(
						"order_by", ItemsFragment.DEFAULT_ORDER));
			} else if (FRAGMENTS[position][2] == FRAGMENT_TYPE_PERSONS) {
				String filter = null;
				String[] filterArgs = null;
				if (!sp.getBoolean("show_returned", false)) {
					filter = "returned = 0";
				}
				fragments[position] = PersonsFragment.newInstance(filter,
						filterArgs);
			} else if (FRAGMENTS[position][2] == FRAGMENT_TYPE_CATEGORIES) {
				String filter = null;
				String[] filterArgs = null;
				if (!sp.getBoolean("show_returned", false)) {
					filter = "returned = 0";
				}
				fragments[position] = CategoriesFragment.newInstance(filter,
						filterArgs);
			}
			getSupportFragmentManager().beginTransaction()
					.add(fragments[position], "mainpager:" + position).commit();
			return fragments[position];
		}

		@Override
		public int getCount() {
			return FRAGMENTS.length;
		}
	}
}
