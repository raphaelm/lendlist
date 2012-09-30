package de.raphaelmichel.lendlist;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class AddActivity extends SherlockActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Custom Font

		Typeface handwrittenFace = Typeface.createFromAsset(getAssets(),
				"fonts/belligerent.ttf");

		((TextView) findViewById(R.id.tvIJust)).setTypeface(handwrittenFace);
		((TextView) findViewById(R.id.tvBorrowed)).setTypeface(handwrittenFace);
		((TextView) findViewById(R.id.tvLend)).setTypeface(handwrittenFace);
		((TextView) findViewById(R.id.tvTo)).setTypeface(handwrittenFace);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_add, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
		case R.id.action_cancel:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
