package de.raphaelmichel.lendlist.frontend;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import de.raphaelmichel.lendlist.R;
import de.raphaelmichel.lendlist.objects.Category;
import de.raphaelmichel.lendlist.storage.DataSource;

public class CategoryLookupActivity extends ActionBarActivity {

	private Category cat;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category_lookup);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		ItemsFragment frItems = ((ItemsFragment) getSupportFragmentManager()
				.findFragmentById(R.id.frMain));
		frItems.setFilter("category = ?", new String[] { ""
				+ getIntent().getLongExtra("category", 0) });
		frItems.setOrder("returned ASC, until ASC");
		cat = DataSource.getCategory(CategoryLookupActivity.this, getIntent()
				.getLongExtra("category", 0));
		if (cat == null)
			finish();
		setTitle(cat.getName());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_category_lookup, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_edit:
			final EditText tvName = new EditText(this);
			tvName.setText(getIntent().getStringExtra("name"));
			AlertDialog.Builder promptB = new AlertDialog.Builder(this);
			promptB.setCancelable(true)
					.setTitle(R.string.category_rename)
					.setPositiveButton(R.string.accept,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									cat.setName(tvName.getText().toString());
									setTitle(tvName.getText().toString());
									DataSource.updateCategory(
											CategoryLookupActivity.this, cat);
									dialog.dismiss();
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							}).setView(tvName);

			AlertDialog prompt = promptB.create();
			prompt.setView(tvName, 10, 10, 10, 10);
			prompt.show();
			return true;
		case R.id.action_delete:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.category_delete_confirm);
			builder.setPositiveButton(R.string.category_delete,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							DataSource.deleteCategory(
									CategoryLookupActivity.this, cat.getId());
							setResult(-1);
							Toast.makeText(CategoryLookupActivity.this,
									R.string.category_delete_success,
									Toast.LENGTH_SHORT).show();
							finish();
						}
					});
			builder.setNegativeButton(android.R.string.no,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
