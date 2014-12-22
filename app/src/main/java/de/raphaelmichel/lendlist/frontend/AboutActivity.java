package de.raphaelmichel.lendlist.frontend;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;
import de.raphaelmichel.lendlist.R;

public class AboutActivity extends ActionBarActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		TextView tvAbout = (TextView) findViewById(R.id.tvAbout);
		TextView tvVersion = (TextView) findViewById(R.id.tvVersion);

		try {
			tvVersion.setText(Html
					.fromHtml(getString(R.string.app_name)
							+ " "
							+ (getPackageManager().getPackageInfo(
									getPackageName(), 0).versionName)));
			tvAbout.setText(Html.fromHtml(readHtml()));
			tvAbout.setMovementMethod(LinkMovementMethod.getInstance());
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
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

	private String readHtml() {
		InputStream raw = getResources().openRawResource(R.raw.about);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		int i;
		try {
			i = raw.read();
			while (i != -1) {
				byteArrayOutputStream.write(i);
				i = raw.read();
			}
			raw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return byteArrayOutputStream.toString();
	}
}
