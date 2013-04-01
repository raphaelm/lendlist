package de.raphaelmichel.lendlist.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.Context;
import android.os.Environment;
import de.raphaelmichel.lendlist.objects.Category;
import de.raphaelmichel.lendlist.objects.CategoryList;
import de.raphaelmichel.lendlist.objects.DataDump;
import de.raphaelmichel.lendlist.objects.Item;
import de.raphaelmichel.lendlist.objects.ItemList;
import de.raphaelmichel.lendlist.storage.DataSource;

public class BackupHelper {

	public static final String SD_DIRECTORY = "LendList";

	public static File getDefaultFile() throws Exception {
		File directory = getDefaultDirectory();

		return new File(directory,
				"backup."
						+ (new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
								.format(new Date())) + ".xml");
	}

	public static File getDefaultDirectory() throws Exception {
		File extdir = Environment.getExternalStorageDirectory();
		File directory = new File(extdir, SD_DIRECTORY + "/");
		directory.mkdirs();
		return directory;
	}

	public static void importBackup(Context context, String filename)
			throws Exception {
		File source = new File(getDefaultDirectory(), filename);
		importBackup(context, source);
	}

	public static void importBackup(Context context, File backupFile)
			throws Exception {
		Serializer serializer = new Persister();
		List<Item> items;
		try {
			DataDump dump = serializer.read(DataDump.class, backupFile);
			items = dump.getItems().getItems();
			List<Category> cats = dump.getCategories().getCategories();
			DataSource.deleteAllCategories(context);
			for (Category cat : cats) {
				DataSource.addCategory(context, cat);
			}
		} catch (Exception e) {
			e.printStackTrace();
			items = serializer.read(ItemList.class, backupFile).getItems();
		}

		DataSource.deleteAll(context);
		for (Item item : items) {
			DataSource.addItem(context, item);
		}
	}

	public static void importInternalBackup(Context context, String backupFile)
			throws Exception {
		Serializer serializer = new Persister();
		FileInputStream fis = context.openFileInput(backupFile);
		List<Item> items;
		try {
			fis.close();
			fis = context.openFileInput(backupFile);
			DataDump dump = serializer.read(DataDump.class, fis);
			items = dump.getItems().getItems();
			DataSource.deleteAllCategories(context);
			List<Category> cats = dump.getCategories().getCategories();
			for (Category cat : cats) {
				DataSource.addCategory(context, cat);
			}
			fis.close();

		} catch (Exception e) {
			e.printStackTrace();
			fis.close();
			fis = context.openFileInput(backupFile);
			items = serializer.read(ItemList.class, fis).getItems();
			fis.close();
		}
		DataSource.deleteAll(context);
		for (Item item : items) {
			DataSource.addItem(context, item);
		}
	}

	public static String writeInternalBackup(Context context) throws Exception {
		Serializer serializer = new Persister();
		List<Item> items = DataSource.getAllItems(context, null, null);
		List<Category> categories = DataSource.getAllCategories(context);
		String filename = "backup."
				+ (new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
						.format(new Date())) + ".xml";
		FileOutputStream fos = context.openFileOutput(filename,
				Context.MODE_PRIVATE);
		serializer.write(new DataDump(new CategoryList(categories),
				new ItemList(items)), fos);
		fos.close();
		return filename;
	}

	public static void writeBackup(Context context, File output)
			throws Exception {
		Serializer serializer = new Persister();
		List<Item> items = DataSource.getAllItems(context, null, null);
		List<Category> categories = DataSource.getAllCategories(context);

		serializer.write(new DataDump(new CategoryList(categories),
				new ItemList(items)), output);
	}

	public static void writeBackup(Context context) throws Exception {
		writeBackup(context, getDefaultFile());
	}
}
