package de.raphaelmichel.lendlist.frontend;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.raphaelmichel.lendlist.R;
import de.raphaelmichel.lendlist.library.ContactsHelper;

public class PersonLookupActivity extends SherlockFragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_lookup);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		ItemsFragment frItems = ((ItemsFragment) getSupportFragmentManager()
				.findFragmentById(R.id.frMain));
		frItems.setFilter("person = ?", new String[] { getIntent()
				.getStringExtra("name") });

		TextView tvName = (TextView) findViewById(R.id.tvName);
		tvName.setText(getIntent().getStringExtra("name"));

		QuickContactBadge qcbPhoto = (QuickContactBadge) findViewById(R.id.qcbPhoto);

		if (getIntent().getStringExtra("uri") != null) {
			Uri contactUri = Uri.parse(getIntent().getStringExtra("uri"));
			qcbPhoto.assignContactUri(contactUri);
			Bitmap bm = ContactsHelper.getPhoto(contactUri, this);
			qcbPhoto.setImageBitmap(bm);
		} else {
			qcbPhoto.setImageResource(R.drawable.ic_contact_picture);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_person_lookup, menu);
		return true;
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
