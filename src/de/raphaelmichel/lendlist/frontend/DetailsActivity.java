package de.raphaelmichel.lendlist.frontend;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.WazaBe.HoloEverywhere.widget.Spinner;
import com.WazaBe.HoloEverywhere.widget.Toast;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.raphaelmichel.lendlist.R;
import de.raphaelmichel.lendlist.library.ContactsHelper;
import de.raphaelmichel.lendlist.objects.Item;
import de.raphaelmichel.lendlist.storage.DataSource;

public class DetailsActivity extends SherlockFragmentActivity {

	private static final int REQUEST_CODE_CONTACT = 3;
	// private static final int REQUEST_CODE_CAMERA = 4;
	// private static final int REQUEST_CODE_PHOTOS = 5;

	private Item item;
	// private List<MutableTriplet<Long, Uri, Bitmap>> photos = new
	// ArrayList<MutableTriplet<Long, Uri, Bitmap>>();

	private boolean changed = false;

	private EditText etThing;
	private EditText etDate;
	private EditText etUntil;
	private Spinner spDirection;
	private ToggleButton btReturned;
	private QuickContactBadge qcbPerson;
	private TextView tvPerson;
	private ImageView ibPersonEdit;
	private ImageView ibRemoveUntil;
	// private ImageButton ibAddPhoto;
	// private LinearLayout llPhotos;
	// private TextView tvLoading;

	private DialogFragment dpDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		item = DataSource.getItem(this, getIntent().getLongExtra("id", 0));

		if (getIntent().getExtras().containsKey("notified")) {
			DataSource.markNotified(this, item.getId());
		}

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
			ibPersonEdit = (ImageView) findViewById(R.id.ibPersonEdit);
			ibRemoveUntil = (ImageView) findViewById(R.id.ibRemoveUntil);

			etThing.setText(item.getThing());
			if (item.getDate() != null)
				etDate.setText(new SimpleDateFormat(
						getString(R.string.date_format)).format(item.getDate()));
			if (item.getUntil() != null)
				etUntil.setText(new SimpleDateFormat(
						getString(R.string.date_format)).format(item.getUntil()));
			tvPerson.setText(item.getPerson());

			// Date picker
			// I know how dirty this is. Feel free to create a patch with a
			// custom widget doing this better.
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
			etDate.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dpDialog = new DatePickerFragment();
					dpDialog.show(getSupportFragmentManager(), "datePicker");
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

			if (item.getContact_id() > 0) {
				qcbPerson.setVisibility(View.VISIBLE);
				Uri contactUri = ContactsContract.Contacts.getLookupUri(
						item.getContact_id(), item.getContact_lookup());
				qcbPerson.assignContactUri(contactUri);

				qcbPerson.setImageBitmap(ContactsHelper.getPhoto(contactUri,
						this));
			} else {
				qcbPerson.setVisibility(View.GONE);
			}

			ibRemoveUntil.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					etUntil.setText("");
					changed = true;
				}
			});
			//
			// llPhotos = (LinearLayout) findViewById(R.id.llPhotos);
			//
			// ibAddPhoto = (ImageButton) findViewById(R.id.ibAddPhoto);
			// ibAddPhoto.setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// addphoto();
			// }
			// });

			// tvPerson.setOnClickListener(new View.OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// save(); // TODO: Is this what the user wants us to do?
			// Intent i = new Intent(DetailsActivity.this,
			// PersonLookupActivity.class);
			// i.putExtra("name", item.getPerson());
			// if (item.getContact_id() > 0) {
			// Uri contactUri = ContactsContract.Contacts
			// .getLookupUri(item.getContact_id(),
			// item.getContact_lookup());
			// i.putExtra("uri", contactUri.toString());
			// }
			// startActivity(i);
			// }
			// });

			// loadphotos();

		}
	}

	// public void savephoto(Uri uri) {
	// DataSource.addPhoto(this, item.getId(), uri);
	// }
	//
	// public void loadphotos() {
	// llPhotos.removeAllViews();
	// photos = DataSource.getPhotos(this, item.getId());
	// if (photos.size() > 0) {
	// tvLoading = new TextView(this);
	// tvLoading.setText(R.string.loading);
	// llPhotos.addView(tvLoading);
	// new LoadPhotoTask().execute(this, item.getId());
	// }
	// }
	//
	// public class LoadPhotoTask extends AsyncTask<Object, Object, Object> {
	// private DetailsActivity ctx;
	// private long object;
	//
	// @Override
	// protected Object doInBackground(Object... params) {
	// ctx = (DetailsActivity) params[0];
	// object = (Long) params[1];
	//
	// for (MutableTriplet<Long, Uri, Bitmap> photo : photos) {
	// File f = new File(photo.second.getPath());
	// if (f != null) {
	// Display display = getWindowManager().getDefaultDisplay();
	// int dstW = display.getWidth();
	// int dstH = 0;
	// Bitmap bm = BitmapFactory.decodeFile(f.getAbsolutePath());
	//
	// float srcW = bm.getWidth();
	// float srcH = bm.getHeight();
	// dstH = (int) (srcH / srcW * (float) dstW);
	// // Pre-scale
	// photo.third = Bitmap.createScaledBitmap(bm, dstW, dstH,
	// false);
	// }
	// }
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(Object result) {
	// llPhotos.removeAllViews();
	// for (final MutableTriplet<Long, Uri, Bitmap> row : photos) {
	// ImageView ivNew = new ImageView(ctx);
	// ivNew.setScaleType(ImageView.ScaleType.FIT_XY);
	// ivNew.setAdjustViewBounds(true);
	// ivNew.setImageBitmap(row.third);
	// ivNew.setOnLongClickListener(new OnLongClickListener() {
	// @Override
	// public boolean onLongClick(View v) {
	// contextMenuPicture(row.first, row.second);
	// return false;
	// }
	// });
	// llPhotos.addView(ivNew);
	// }
	// }
	//
	// }
	//
	// private void contextMenuPicture(final long id, final Uri uri) {
	// final CharSequence[] items = { getString(R.string.photo_share),
	// getString(R.string.photo_delete) };
	//
	// AlertDialog.Builder builder = new AlertDialog.Builder(this);
	// builder.setTitle(R.string.person_method);
	// builder.setItems(items, new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int n) {
	// dialog.dismiss();
	// if (n == 0) {
	// // Share
	// Intent share = new Intent(Intent.ACTION_SEND);
	// share.setType("image/jpeg");
	// share.putExtra(Intent.EXTRA_STREAM, uri);
	// startActivity(Intent.createChooser(share, "Share Image"));
	// } else if (n == 1) {
	// // Delete
	// deletePicture(id);
	// }
	// }
	// });
	// AlertDialog alert = builder.create();
	// alert.show();
	// }

	@Override
	protected void onDestroy() {
		// unbindDrawables(llPhotos);
		System.gc();
		super.onDestroy();
	}

	protected void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			if (!(view instanceof AdapterView)) {
				((ViewGroup) view).removeAllViews();
			}
		}
	}

	//
	// public void addphoto() {
	// PackageManager pm = getPackageManager();
	//
	// if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
	// final CharSequence[] items = { getString(R.string.photo_choose),
	// getString(R.string.photo_take) };
	//
	// AlertDialog.Builder builder = new AlertDialog.Builder(this);
	// builder.setTitle(R.string.person_method);
	// builder.setItems(items, new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int n) {
	// dialog.dismiss();
	// if (n == 0) {
	// // Choose
	// Intent iPick = new Intent(
	// Intent.ACTION_PICK,
	// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	// startActivityForResult(iPick, REQUEST_CODE_PHOTOS);
	// } else if (n == 1) {
	// // Take photo
	// Intent iCam = new Intent(
	// MediaStore.ACTION_IMAGE_CAPTURE);
	// File image = new File(
	// getExternalFilesDir(Environment.DIRECTORY_PICTURES),
	// "lendlist_"
	// + new SimpleDateFormat(
	// "yyyyMMdd_HHmmss")
	// .format(new Date()) + ".jpg");
	// Uri imageUri = Uri.fromFile(image);
	// PreferenceManager
	// .getDefaultSharedPreferences(
	// DetailsActivity.this)
	// .edit()
	// .putString("last_photo_path",
	// imageUri.toString()).commit();
	// iCam.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
	// startActivityForResult(iCam, REQUEST_CODE_CAMERA);
	// }
	// }
	// });
	// AlertDialog alert = builder.create();
	// alert.show();
	// } else {
	// Intent iPick = new Intent(
	// Intent.ACTION_PICK,
	// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	// startActivityForResult(iPick, REQUEST_CODE_PHOTOS);
	// }
	// }

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
		// case REQUEST_CODE_CAMERA:
		// if (resultCode == RESULT_OK) {
		// savephoto(Uri.parse(PreferenceManager
		// .getDefaultSharedPreferences(this).getString(
		// "last_photo_path", null)));
		// }
		// break;
		// case REQUEST_CODE_PHOTOS:
		// if (resultCode == RESULT_OK) {
		// Uri selectedImage = data.getData();
		// String[] filePathColumn = { MediaStore.Images.Media.DATA };
		// Cursor cursor = getContentResolver().query(selectedImage,
		// filePathColumn, null, null, null);
		// cursor.moveToFirst();
		//
		// int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		// String filePath = cursor.getString(columnIndex);
		// cursor.close();
		// savephoto(Uri.parse(filePath));
		// }
		// break;
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

					qcbPerson.setImageBitmap(ContactsHelper.getPhoto(
							contactUri, this));
					changed = true;
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
											changed = true;
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
			changed = true;
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
			changed = true;
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
		String thing = etThing.getText().toString();
		if (!thing.equals(item.getThing())) {
			changed = true;
			item.setThing(etThing.getText().toString());
		}

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

		if ((spDirection.getSelectedItemPosition() == 1 && item.getDirection()
				.equals("borrowed"))) {
			changed = true;
			item.setDirection("lent");
		} else if (spDirection.getSelectedItemPosition() == 0
				&& item.getDirection().equals("lent")) {
			changed = true;
			item.setDirection("borrowed");
		}

		if (btReturned.isChecked() != item.isReturned()) {
			item.setReturned(btReturned.isChecked());
			changed = true;
		}

		DataSource.updateItem(this, item);

		if (changed)
			Toast.makeText(DetailsActivity.this, R.string.save_success,
					Toast.LENGTH_SHORT).show();
	}

	//
	// public void deletePicture(final long id) {
	// AlertDialog.Builder builder = new AlertDialog.Builder(this);
	// builder.setMessage(R.string.photo_delete_confirm);
	// builder.setPositiveButton(R.string.delete, new OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// DataSource.deletePhoto(DetailsActivity.this, id);
	// loadphotos();
	// }
	// });
	// builder.setNegativeButton(android.R.string.no, new OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.cancel();
	// }
	// });
	// builder.show();
	// }

	public void delete() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.delete_confirm);
		builder.setPositiveButton(R.string.delete, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				DataSource.deleteItem(DetailsActivity.this, item.getId());
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
	public boolean onOptionsItemSelected(MenuItem mi) {
		switch (mi.getItemId()) {
		case android.R.id.home:
			save();
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_calendar:
			if (item.getUntil() != null) {
				Intent intent = new Intent(Intent.ACTION_EDIT);
				intent.setType("vnd.android.cursor.item/event");
				intent.putExtra("beginTime", item.getUntil().getTime());
				intent.putExtra("allDay", true);
				intent.putExtra(
						"title",
						getString(R.string.calendar_return, item.getThing(),
								item.getPerson()));
				startActivity(intent);
			} else {
				Toast.makeText(DetailsActivity.this,
						R.string.error_no_return_date, Toast.LENGTH_SHORT)
						.show();
			}
			return true;

		case R.id.action_delete:
			delete();
			return true;
		case R.id.action_accept:
			save();
			finish();
			return true;
		}
		return super.onOptionsItemSelected(mi);
	}

	@Override
	public void onBackPressed() {
		save();
		super.onBackPressed();
	}

}
