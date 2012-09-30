package de.raphaelmichel.lendlist.frontend;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.app.DatePickerDialog;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.raphaelmichel.lendlist.R;
import de.raphaelmichel.lendlist.R.drawable;
import de.raphaelmichel.lendlist.R.id;
import de.raphaelmichel.lendlist.R.layout;
import de.raphaelmichel.lendlist.R.menu;
import de.raphaelmichel.lendlist.R.string;

public class AddActivity extends SherlockFragmentActivity {

	private String direction = "borrowed";
	private DialogFragment dpDialog;
	private EditText etUntil;
	private EditText etThing;
	private EditText etPerson;
	private TextView tvBorrowed;
	private TextView tvLent;
	private TextView tvTo;

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString("thing", etThing.getText().toString());
		savedInstanceState.putString("person", etPerson.getText().toString());
		savedInstanceState.putString("until", etUntil.getText().toString());
		savedInstanceState.putString("direction", direction);
	}

	public static String getS(Bundle b, String k){
		String s = b.getString(k);
		if(s == null)
			return "";
		else
			return s;
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		etThing.setText(getS(savedInstanceState, "thing"));
		etPerson.setText(getS(savedInstanceState, "person"));
		etUntil.setText(getS(savedInstanceState, "until"));
		if (savedInstanceState.getString("direction") != null) {
			if (getIntent().getStringExtra("direction").equals("lent")) {
				tvLent.setBackgroundResource(R.drawable.textmarker_bitmap);
				tvBorrowed.setBackgroundResource(0);
				tvTo.setText(R.string.add_text_to);
				direction = "lent";
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		etThing = (EditText) findViewById(R.id.etThing);
		etPerson = (EditText) findViewById(R.id.etPerson);

		tvBorrowed = (TextView) findViewById(R.id.tvBorrowed);
		tvLent = (TextView) findViewById(R.id.tvLent);
		tvTo = (TextView) findViewById(R.id.tvTo);

		// Custom Font
		Typeface handwrittenFace = Typeface.createFromAsset(getAssets(),
				"fonts/belligerent.ttf");
		((TextView) findViewById(R.id.tvIJust)).setTypeface(handwrittenFace);
		((TextView) findViewById(R.id.tvUntil)).setTypeface(handwrittenFace);
		tvBorrowed.setTypeface(handwrittenFace);
		tvLent.setTypeface(handwrittenFace);
		tvTo.setTypeface(handwrittenFace);

		if (getIntent().getStringExtra("direction") != null) {
			if (getIntent().getStringExtra("direction").equals("lent")) {
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

		etUntil = (EditText) findViewById(R.id.etUntil);

		etUntil.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					dpDialog = new DatePickerFragment();
					dpDialog.show(getSupportFragmentManager(), "datePicker");
				}
				return true;
			}
		});

		etUntil.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (dpDialog == null) {
						dpDialog = new DatePickerFragment();
						dpDialog.show(getSupportFragmentManager(), "datePicker");
					}
				} else {
					if (dpDialog != null) {
						if (dpDialog.isVisible())
							dpDialog.dismiss();
						dpDialog = null;
					}
				}
			}
		});

		if (savedInstanceState != null)
			onRestoreInstanceState(savedInstanceState);
	}

	public class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			etUntil.setText(new SimpleDateFormat(
					getString(R.string.date_format)).format(new Date(
					year - 1900, month, day)));
		}
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
