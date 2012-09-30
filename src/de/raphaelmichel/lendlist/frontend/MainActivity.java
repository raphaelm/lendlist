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

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.raphaelmichel.lendlist.R;

public class MainActivity extends SherlockFragmentActivity {

	private static int REQUEST_CODE_ADD = 1;

	private String FRAGMENTS[][];

	private ViewPager mViewPager;
	private MainFragmentAdapter fragmentAdapter;
	private SharedPreferences sp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sp = PreferenceManager.getDefaultSharedPreferences(this);

		FRAGMENTS = new String[][] {
				new String[] { getString(R.string.borrowed), "borrowed" },
				new String[] { getString(R.string.lent), "lent" } };

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		fragmentAdapter = new MainFragmentAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(fragmentAdapter);
		mViewPager.setCurrentItem(sp.getInt("main_tab", 0));
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
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_add) {
			Intent i = new Intent(this, AddActivity.class);
			i.putExtra("direction", FRAGMENTS[mViewPager.getCurrentItem()][1]);
			startActivityForResult(i, REQUEST_CODE_ADD);
		}
		return super.onOptionsItemSelected(item);
	}

	public class MainFragmentAdapter extends FragmentPagerAdapter {
		
		public MainFragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return FRAGMENTS[position][0];
		}

		@Override
		public Fragment getItem(int position) {
			return MainActivityFragment.newInstance(FRAGMENTS[position][1]);
		}

		@Override
		public int getCount() {
			return FRAGMENTS.length;
		}
	}
}
