package de.raphaelmichel.lendlist.frontend;

import java.io.File;
import java.io.FilenameFilter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import de.raphaelmichel.lendlist.R;
import de.raphaelmichel.lendlist.backup.BackupHelper;

public class ExportActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_export);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		Button btExportSd = (Button) findViewById(R.id.btExportSd);
		btExportSd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				export();
			}
		});
		Button btImportSd = (Button) findViewById(R.id.btImportSd);
		btImportSd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				importChooseFile();
			}
		});
	}

	private String[] loadFileList() {
		try {
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String filename) {
					return filename.contains(".xml");
				}
			};
			return BackupHelper.getDefaultDirectory().list(filter);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void importChooseFile() {
		final String[] fileList = loadFileList();
		if (fileList == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.import_failed);
			builder.setPositiveButton(R.string.accept,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					});
			AlertDialog dialog = builder.create();
			dialog.show();
			return;
		}

		AlertDialog.Builder fcBuilder = new AlertDialog.Builder(this);
		fcBuilder.setTitle(R.string.import_choose);
		fcBuilder.setItems(fileList, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				importConfirm(fileList[which]);
				dialog.dismiss();
			}
		});
		AlertDialog fcDialog = fcBuilder.create();
		fcDialog.show();
	}

	private void importConfirm(final String filename) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.import_confirm);
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.setPositiveButton(R.string.accept,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						importDo(filename);
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void importDo(final String filename) {
		try {
			BackupHelper.importBackup(this, filename);
			Toast.makeText(this, R.string.import_success, Toast.LENGTH_SHORT)
					.show();
		} catch (Exception e) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.import_failed);
			builder.setPositiveButton(R.string.accept,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					});
			AlertDialog dialog = builder.create();
			dialog.show();
			e.printStackTrace();
		}
	}

	private void export() {
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			try {
				BackupHelper.writeBackup(this, BackupHelper.getDefaultFile());
				Toast.makeText(this, R.string.export_success,
						Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				e.printStackTrace();
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.export_failed);
				builder.setPositiveButton(R.string.accept,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.export_failed_sd);
			builder.setPositiveButton(R.string.accept,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					});
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_export, menu);
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
