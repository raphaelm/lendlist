package de.raphaelmichel.lendlist.backup;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;

import android.content.Context;
import android.os.Environment;
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
		Serializer serializer = new Persister();
		File source = new File(getDefaultDirectory(), filename);
		List<Item> items = serializer.read(ItemList.class, source).getItems();
		DataSource.deleteAll(context);
		for (Item item : items) {
			DataSource.addItem(context, item);
		}
	}

	public static void writeBackup(Context context, File output)
			throws Exception {
		Serializer serializer = new Persister(
				new Format(
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
								+ "<?xml-stylesheet href=\"http://www.raphaelmichel.de/lendlist/stylesheet.xsl\" type=\"text/xsl\" ?>"));
		List<Item> items = DataSource.getAllItems(context, null, null);

		serializer.write(new ItemList(items), output);
	}

	public static void writeBackup(Context context) throws Exception {
		writeBackup(context, getDefaultFile());
	}
}
