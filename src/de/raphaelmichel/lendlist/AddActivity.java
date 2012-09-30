package de.raphaelmichel.lendlist;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class AddActivity extends SherlockActivity {

	private String direction = "borrowed";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Custom Font

		Typeface handwrittenFace = Typeface.createFromAsset(getAssets(),
				"fonts/belligerent.ttf");

		final TextView tvBorrowed = (TextView) findViewById(R.id.tvBorrowed);
		final TextView tvLent = (TextView) findViewById(R.id.tvLent);
		final TextView tvTo = (TextView) findViewById(R.id.tvTo);
		((TextView) findViewById(R.id.tvIJust)).setTypeface(handwrittenFace);
		tvBorrowed.setTypeface(handwrittenFace);
		tvLent.setTypeface(handwrittenFace);
		tvTo.setTypeface(handwrittenFace);

		if(getIntent().getStringExtra("direction") != null){
			if(getIntent().getStringExtra("direction").equals("lent")){
				tvLent.setBackgroundResource(R.drawable.textmarker_bitmap);
				tvBorrowed.setBackgroundResource(0);
				tvTo.setText(R.string.add_text_to);
				direction = "lent";
			}
		}
		
		tvBorrowed.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!direction.equals("borrowed")) {
					v.setBackgroundResource(R.drawable.textmarker_bitmap);
					tvLent.setBackgroundResource(0);
					tvTo.setText(R.string.add_text_from);
					direction = "borrowed";
				}
			}
		});
		tvLent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!direction.equals("lent")) {
					v.setBackgroundResource(R.drawable.textmarker_bitmap);
					tvBorrowed.setBackgroundResource(0);
					tvTo.setText(R.string.add_text_to);
					direction = "lent";
				}
			}
		});
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
