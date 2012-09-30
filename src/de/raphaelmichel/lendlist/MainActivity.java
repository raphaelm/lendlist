package de.raphaelmichel.lendlist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

public class MainActivity extends SherlockFragmentActivity {

	private String FRAGMENTS[][];

	private ViewPager mViewPager;
	private MainFragmentAdapter fragmentAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		FRAGMENTS = new String[][] {
				new String[] { getString(R.string.borrowed), "borrowed" },
				new String[] { getString(R.string.lend), "lend" } };

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		fragmentAdapter = new MainFragmentAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(fragmentAdapter);
		mViewPager.setCurrentItem(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return super.onCreateOptionsMenu(menu);
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
