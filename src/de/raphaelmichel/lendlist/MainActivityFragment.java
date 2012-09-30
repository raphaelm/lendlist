package de.raphaelmichel.lendlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

public class MainActivityFragment extends SherlockFragment {

	private String query;

	static MainActivityFragment newInstance(String query) {
		MainActivityFragment f = new MainActivityFragment();

		Bundle args = new Bundle();
		args.putString("query", query);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		query = getArguments() != null ? getArguments().getString("query")
				: null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main, container, false);

		return v;
	}

}
