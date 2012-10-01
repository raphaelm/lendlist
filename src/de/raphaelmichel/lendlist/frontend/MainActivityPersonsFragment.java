package de.raphaelmichel.lendlist.frontend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import de.raphaelmichel.lendlist.R;

public class MainActivityPersonsFragment extends SherlockFragment {

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
		View v = inflater.inflate(R.layout.error, container, false);

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

}
