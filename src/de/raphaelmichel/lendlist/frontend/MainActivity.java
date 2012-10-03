package de.raphaelmichel.lendlist.frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.raphaelmichel.lendlist.R;

public class MainActivity extends SherlockFragmentActivity {

	private static int REQUEST_CODE_ADD = 1;

	private static int FRAGMENT_TYPE_ITEMS = 0;
	private static int FRAGMENT_TYPE_PERSONS = 1;
	private static String[] DIRECTIONS = { "borrowed", "lent" };

	private static int[][] FRAGMENTS = new int[][] {
			new int[] { R.string.borrowed, 0, FRAGMENT_TYPE_ITEMS },
			new int[] { R.string.lent, 1, FRAGMENT_TYPE_ITEMS },
			new int[] { R.string.persons, 0, FRAGMENT_TYPE_PERSONS } };

	private ViewPager mViewPager;
	private MainFragmentAdapter fragmentAdapter;
	private SharedPreferences sp;
	private MenuItem miFilter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sp = PreferenceManager.getDefaultSharedPreferences(this);

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		fragmentAdapter = new MainFragmentAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(fragmentAdapter);
		mViewPager.setCurrentItem(sp.getInt("main_tab", 0));
		mViewPager.setOffscreenPageLimit(2);
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
			i.putExtra("direction",
					DIRECTIONS[FRAGMENTS[mViewPager.getCurrentItem()][1]]);
			startActivityForResult(i, REQUEST_CODE_ADD);
			return true;

		case R.id.action_filter:
			String filter = "direction = ?";
			
			if (sp.getBoolean("show_returned", false)) {
				sp.edit().putBoolean("show_returned", false).commit();
				item.setTitle(R.string.returned_show);
				filter = "direction = ? and returned = 0";
			} else {
				sp.edit().putBoolean("show_returned", true).commit();
				item.setTitle(R.string.returned_hide);
			}
			
			int number_of_fragments = FRAGMENTS.length;
			for(int j = 0; j < number_of_fragments; j++){
				if (FRAGMENTS[j][2] == FRAGMENT_TYPE_ITEMS) {
					String[] filterArgs = { DIRECTIONS[FRAGMENTS[j][1]] };
					((ItemsFragment) fragmentAdapter.fragments[j]).filter = filter;
					((ItemsFragment) fragmentAdapter.fragments[j]).filterArgs = filterArgs;
					((ItemsFragment) fragmentAdapter.fragments[j]).refresh();
				}
			}
			
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	public class MainFragmentAdapter extends FragmentPagerAdapter {
		public Fragment[] fragments = new Fragment[FRAGMENTS.length];

		public MainFragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getString(FRAGMENTS[position][0]);
		}

		@Override
		public Fragment getItem(int position) {
			if (fragments[position] != null) {
				return fragments[position];
			}
			if (FRAGMENTS[position][2] == FRAGMENT_TYPE_ITEMS) {
				String filter = "direction = ?";
				String[] filterArgs = { DIRECTIONS[FRAGMENTS[position][1]] };

				if (!sp.getBoolean("show_returned", false)) {
					filter = "direction = ? and returned = 0";
					filterArgs = new String[] { DIRECTIONS[FRAGMENTS[position][1]] };
				}

				fragments[position] = ItemsFragment.newInstanceFiltered(filter,
						filterArgs);
				return fragments[position];
			} else if (FRAGMENTS[position][2] == FRAGMENT_TYPE_PERSONS)
				return MainActivityPersonsFragment.newInstance();
			
			return null;
		}

		@Override
		public int getCount() {
			return FRAGMENTS.length;
		}
	}
}
