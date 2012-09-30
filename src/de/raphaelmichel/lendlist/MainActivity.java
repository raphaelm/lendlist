package de.raphaelmichel.lendlist;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends SherlockFragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
		TitleAdapter titleAdapter = new TitleAdapter(
				getSupportFragmentManager());
		mViewPager.setAdapter(titleAdapter);
		mViewPager.setCurrentItem(0);
	}

	public class TitleAdapter extends FragmentPagerAdapter {
		private String FRAGMENTS[][] = new String[][] {
				new String[] { getString(R.string.borrowed), "borrowed" },
				new String[] { getString(R.string.lend), "lend" } };

		public TitleAdapter(FragmentManager fm) {
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
