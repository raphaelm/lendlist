package de.raphaelmichel.lendlist.frontend;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import de.raphaelmichel.lendlist.R;

public class PreferenceActivity extends android.preference.PreferenceActivity {
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		addPreferencesFromResource(R.xml.main_preferences);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
