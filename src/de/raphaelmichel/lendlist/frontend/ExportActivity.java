package de.raphaelmichel.lendlist.frontend;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import de.raphaelmichel.lendlist.R;
import de.raphaelmichel.lendlist.objects.Item;
import de.raphaelmichel.lendlist.objects.ItemList;
import de.raphaelmichel.lendlist.storage.DataSource;

public class ExportActivity extends SherlockActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_export);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Button btExportSd = (Button) findViewById(R.id.btExportSd);
		btExportSd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String state = Environment.getExternalStorageState();

				if (Environment.MEDIA_MOUNTED.equals(state)) {
					// We can read and write the media
					File dir = Environment.getExternalStorageDirectory();
					File ourdir = new File(dir, "LendList/");
					ourdir.mkdirs();
					File ourfile = new File(ourdir, "backup."
							+ (new SimpleDateFormat(

							"yyyy-MM-dd-HH-mm-ss").format(new Date())) + ".xml");

					Serializer serializer = new Persister();
					try {
						List<Item> items = DataSource.getAllItems(
								ExportActivity.this, null, null);
						serializer.write(new ItemList(items), ourfile);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_export, menu);
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
