package de.raphaelmichel.lendlist.frontend;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.WazaBe.HoloEverywhere.widget.Spinner;
import com.WazaBe.HoloEverywhere.widget.Toast;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.raphaelmichel.lendlist.R;
import de.raphaelmichel.lendlist.objects.Item;
import de.raphaelmichel.lendlist.storage.DataSource;

public class DetailsActivity extends SherlockFragmentActivity {

	private static final int REQUEST_CODE_CONTACT = 3;

	private Item item;

	private EditText etThing;
	private EditText etDate;
	private EditText etUntil;
	private Spinner spDirection;
	private ToggleButton btReturned;
	private QuickContactBadge qcbPerson;
	private TextView tvPerson;
	private ImageButton ibPersonEdit;

	private DialogFragment dpDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		DataSource data = new DataSource(this);
		data.open();
		item = data.getItem(getIntent().getLongExtra("id", 0));
		data.close();

		if (item == null) {
			setContentView(R.layout.error);
			Typeface handwrittenFace = Typeface.createFromAsset(getAssets(),
					"fonts/belligerent.ttf");
			((TextView) findViewById(R.id.tvHead)).setTypeface(handwrittenFace);
			((TextView) findViewById(R.id.tvBody)).setTypeface(handwrittenFace);
			((TextView) findViewById(R.id.tvBody))
					.setText(R.string.error_unkown_item);
		} else {
			setContentView(R.layout.activity_details);
			etThing = (EditText) findViewById(R.id.etThing);
			etDate = (EditText) findViewById(R.id.etDate);
			etUntil = (EditText) findViewById(R.id.etUntil);
			spDirection = (Spinner) findViewById(R.id.spDirection);
			btReturned = (ToggleButton) findViewById(R.id.btReturned);
			qcbPerson = (QuickContactBadge) findViewById(R.id.qcbPerson);
			tvPerson = (TextView) findViewById(R.id.tvPerson);
			ibPersonEdit = (ImageButton) findViewById(R.id.ibPersonEdit);

			etThing.setText(item.getThing());
			if (item.getDate() != null)
				etDate.setText(new SimpleDateFormat(
						getString(R.string.date_format)).format(item.getDate()));
			if (item.getUntil() != null)
				etUntil.setText(new SimpleDateFormat(
						getString(R.string.date_format)).format(item.getUntil()));
			tvPerson.setText(item.getPerson());

			// Date picker
			etUntil = (EditText) findViewById(R.id.etUntil);
			etUntil.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						dpDialog = new DatePickerFragmentUntil();
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
							dpDialog = new DatePickerFragmentUntil();
							dpDialog.show(getSupportFragmentManager(),
									"datePicker");
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
			etDate = (EditText) findViewById(R.id.etDate);
			etDate.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						dpDialog = new DatePickerFragment();
						dpDialog.show(getSupportFragmentManager(), "datePicker");
					}
					return true;
				}
			});

			etDate.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						if (dpDialog == null) {
							dpDialog = new DatePickerFragment();
							dpDialog.show(getSupportFragmentManager(),
									"datePicker");
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

			List<String> list = new ArrayList<String>();
			list.add(getString(R.string.edit_text_borrowed));
			list.add(getString(R.string.edit_text_lent));
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
					R.layout.simple_spinner_item, list);
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spDirection.setAdapter(dataAdapter);

			if (item.getDirection().equals("lent"))
				spDirection.setSelection(1);
			else
				spDirection.setSelection(0);

			btReturned.setChecked(item.isReturned());

			ibPersonEdit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					edit_person();
				}
			});
			Log.i("contact",
					ContactsContract.Contacts.getLookupUri(
							item.getContact_id(), item.getContact_lookup())
							.toString());

			if (item.getContact_id() > 0) {
				qcbPerson.setVisibility(View.VISIBLE);
				Uri contactUri = ContactsContract.Contacts.getLookupUri(
						item.getContact_id(), item.getContact_lookup());
				qcbPerson.assignContactUri(contactUri);

				qcbPerson.setImageBitmap(getPhoto(contactUri));
			} else {
				qcbPerson.setVisibility(View.GONE);
			}

		}
	}

	public Bitmap getPhoto(Uri uri) {

	    long contactId;

	    try {
	        Uri contactLookupUri = uri;
	        Cursor c = getContentResolver().query(contactLookupUri,
	                new String[] { ContactsContract.Contacts._ID }, null, null,
	                null);
	        try {
	            if (c == null || c.moveToFirst() == false) {
	                return null;
	            }
	            contactId = c.getLong(0);
	        } finally {
	            c.close();
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }

	    Uri contactUri = ContentUris.withAppendedId(
	            ContactsContract.Contacts.CONTENT_URI, contactId);
	    InputStream input = ContactsContract.Contacts
	            .openContactPhotoInputStream(getContentResolver(),
	                    contactUri);

	    if (input != null) {
	        return BitmapFactory.decodeStream(input);
	    } else {
	        return BitmapFactory.decodeResource(getResources(), R.drawable.ic_contact_picture);
	    }
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

					Uri contactUri = ContactsContract.Contacts.getLookupUri(id,
							lookup);

					item.setPerson(name);
					item.setContact_id(id);
					item.setContact_lookup(lookup);
					tvPerson.setText(name);
					qcbPerson.setVisibility(View.VISIBLE);
					qcbPerson.assignContactUri(contactUri);

					qcbPerson.setImageBitmap(getPhoto(contactUri));
				}
			}
			break;
		}
	}

	public void edit_person() {
		final CharSequence[] items = {
				getString(R.string.person_choosecontacts),
				getString(R.string.person_entertext) };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.person_method);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int n) {
				if (n == 0) {
					// Choose from Contacts
					dialog.dismiss();
					Intent intent = new Intent(Intent.ACTION_PICK,
							ContactsContract.Contacts.CONTENT_URI);
					startActivityForResult(intent, REQUEST_CODE_CONTACT);
				} else if (n == 1) {
					// Enter Text
					dialog.dismiss();
					final EditText myView = new EditText(DetailsActivity.this);
					myView.setText(item.getPerson());
					myView.setTextColor(getResources().getColor(
							R.color.bright_foreground_holo_dark)); // TODO: This
																	// is way
																	// too
																	// dirty.
					AlertDialog.Builder promptB = new AlertDialog.Builder(
							DetailsActivity.this);
					promptB.setCancelable(true)
							.setTitle(R.string.person_heading)
							.setPositiveButton(R.string.accept,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											item.setPerson(myView.getText()
													.toString());
											item.setContact_id(0);
											qcbPerson.setVisibility(View.GONE);
											tvPerson.setText(myView.getText()
													.toString());
											dialog.dismiss();
										}
									})
							.setNegativeButton(R.string.cancel,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									}).setView(myView);

					AlertDialog prompt = promptB.create();
					prompt.setView(myView, 10, 10, 10, 10);
					prompt.show();

				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
			if (etUntil.getText().length() > 0) {
				Date d;
				try {
					d = getDate();
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
			etDate.setText(new SimpleDateFormat(getString(R.string.date_format))
					.format(new Date(year - 1900, month, day)));
		}

		protected Date getDate() throws ParseException {
			return new SimpleDateFormat(getString(R.string.date_format))
					.parse(etDate.getText().toString());
		}
	}

	public class DatePickerFragmentUntil extends DatePickerFragment {
		@SuppressWarnings("deprecation")
		public void onDateSet(DatePicker view, int year, int month, int day) {
			etUntil.setText(new SimpleDateFormat(
					getString(R.string.date_format)).format(new Date(
					year - 1900, month, day)));
		}

		protected Date getDate() throws ParseException {
			return new SimpleDateFormat(getString(R.string.date_format))
					.parse(etUntil.getText().toString());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_details, menu);
		return true;
	}

	public void save() {
		item.setThing(etThing.getText().toString());

		try {
			item.setUntil(new SimpleDateFormat(getString(R.string.date_format))
					.parse(etUntil.getText().toString()));
		} catch (ParseException e) {
			item.setUntil(null);
		}
		try {
			item.setDate(new SimpleDateFormat(getString(R.string.date_format))
					.parse(etDate.getText().toString()));
		} catch (ParseException e) {
			item.setDate(null);
		}
		if (spDirection.getSelectedItemPosition() == 0) {
			item.setDirection("borrowed");
		} else {
			item.setDirection("lent");
		}
		item.setReturned(btReturned.isChecked());
		DataSource data = new DataSource(this);
		data.openWritable();
		data.updateItem(item);
		data.close();

		Toast.makeText(DetailsActivity.this, R.string.save_success,
				Toast.LENGTH_SHORT).show();
	}

	public void delete() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.delete_confirm);
		builder.setPositiveButton(R.string.delete, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				DataSource data = new DataSource(DetailsActivity.this);
				data.openWritable();
				data.deleteItem(item.getId());
				data.close();
				setResult(-1);
				Toast.makeText(DetailsActivity.this, R.string.delete_success,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		builder.setNegativeButton(android.R.string.no, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_delete:
			delete();
			return true;
		case R.id.action_accept:
			save();
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
