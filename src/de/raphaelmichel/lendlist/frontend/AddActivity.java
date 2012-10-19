package de.raphaelmichel.lendlist.frontend;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.raphaelmichel.lendlist.R;
import de.raphaelmichel.lendlist.objects.Item;
import de.raphaelmichel.lendlist.storage.DataSource;

public class AddActivity extends SherlockFragmentActivity {

	private static final int REQUEST_CODE_CONTACT = 3;

	private String direction = "borrowed";
	private DialogFragment dpDialog;
	private EditText etUntil;
	private EditText etThing;
	private EditText etPerson;
	private TextView tvBorrowed;
	private TextView tvLent;
	private TextView tvTo;
	private ImageButton ibContact;

	private Item item = new Item();

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString("thing", etThing.getText().toString());
		savedInstanceState.putString("person", etPerson.getText().toString());
		savedInstanceState.putString("until", etUntil.getText().toString());
		savedInstanceState.putString("direction", direction);
	}

	public static String getS(Bundle b, String k) {
		String s = b.getString(k);
		if (s == null)
			return "";
		else
			return s;
	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
		case REQUEST_CODE_CONTACT:
			if (resultCode == RESULT_OK) {
				Uri contactData = data.getData();
				Cursor c = managedQuery(contactData, null, null, null, null);
				if (c.moveToFirst()) {
					String name = c
							.getString(c
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					String lookup = c
							.getString(c
									.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
					long id = c.getLong(c
							.getColumnIndex(ContactsContract.Contacts._ID));

					item.setPerson(name);
					item.setContact_id(id);
					item.setContact_lookup(lookup);
					etPerson.setText(name);
					etPerson.setEnabled(false);
					ibContact.setImageResource(R.drawable.ic_action_cancel);
				}
			}
			break;
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		etThing.setText(getS(savedInstanceState, "thing"));
		etPerson.setText(getS(savedInstanceState, "person"));
		etUntil.setText(getS(savedInstanceState, "until"));
		if (savedInstanceState.getString("direction") != null) {
			if (getIntent().getStringExtra("direction").equals("lent")) {
				tvLent.setBackgroundResource(R.drawable.dirsel_lent_highlighted);
				tvLent.setTextColor(getResources().getColorStateList(
						R.color.dirsel_lent_highlighted));
				tvBorrowed
						.setBackgroundResource(R.drawable.dirsel_borrowed_inactive);
				tvBorrowed.setTextColor(getResources().getColorStateList(
						R.color.dirsel_borrowed_inactive));
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

		if (getIntent().getStringExtra("direction") != null) {
			if (getIntent().getStringExtra("direction").equals("lent")) {
				tvLent.setBackgroundResource(R.drawable.dirsel_lent_highlighted);
				tvLent.setTextColor(getResources().getColorStateList(
						R.color.dirsel_lent_highlighted));
				tvBorrowed
						.setBackgroundResource(R.drawable.dirsel_borrowed_inactive);
				tvBorrowed.setTextColor(getResources().getColorStateList(
						R.color.dirsel_borrowed_inactive));
				tvTo.setText(R.string.add_text_to);
				direction = "lent";
			}
		}

		tvBorrowed.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!direction.equals("borrowed")) {
					tvLent.setBackgroundResource(R.drawable.dirsel_lent_inactive);
					tvLent.setTextColor(getResources().getColorStateList(
							R.color.dirsel_lent_inactive));
					tvBorrowed
							.setBackgroundResource(R.drawable.dirsel_borrowed_highlighted);
					tvBorrowed.setTextColor(getResources().getColorStateList(
							R.color.dirsel_borrowed_highlighted));
					tvTo.setText(R.string.add_text_from);
					direction = "borrowed";
				}
			}
		});

		tvLent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!direction.equals("lent")) {
					tvLent.setBackgroundResource(R.drawable.dirsel_lent_highlighted);
					tvLent.setTextColor(getResources().getColorStateList(
							R.color.dirsel_lent_highlighted));
					tvBorrowed
							.setBackgroundResource(R.drawable.dirsel_borrowed_inactive);
					tvBorrowed.setTextColor(getResources().getColorStateList(
							R.color.dirsel_borrowed_inactive));
					tvTo.setText(R.string.add_text_to);
					direction = "lent";
				}
			}
		});

		etUntil = (EditText) findViewById(R.id.etUntil);

		etUntil.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dpDialog = new DatePickerFragment();
				dpDialog.show(getSupportFragmentManager(), "datePicker");
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

		ibContact = (ImageButton) findViewById(R.id.ibContact);
		ibContact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (item.getContact_id() > 0) {
					item.setContact_id(0);
					item.setContact_lookup(null);
					etPerson.setText("");
					etPerson.setEnabled(true);
					ibContact.setImageResource(R.drawable.ic_action_contact);
				} else {
					Intent intent = new Intent(Intent.ACTION_PICK,
							ContactsContract.Contacts.CONTENT_URI);
					startActivityForResult(intent, REQUEST_CODE_CONTACT);
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
			final Calendar c = Calendar.getInstance();
			if (etUntil.getText().length() > 0) {
				Date d;
				try {
					d = new SimpleDateFormat(getString(R.string.date_format))
							.parse(etUntil.getText().toString());
					c.setTime(d);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@SuppressWarnings("deprecation")
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

	public void save() {
		item.setDirection(direction);
		item.setThing(etThing.getText().toString());
		item.setPerson(etPerson.getText().toString());
		try {
			item.setUntil(new SimpleDateFormat(getString(R.string.date_format))
					.parse(etUntil.getText().toString()));
		} catch (ParseException e) {
			item.setUntil(null);
		}
		item.setDate(new Date());

		DataSource.addItem(this, item);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_cancel:
			finish();
			return true;
		case R.id.action_accept:
			save();
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
