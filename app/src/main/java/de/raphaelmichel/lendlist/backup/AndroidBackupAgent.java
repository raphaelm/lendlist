package de.raphaelmichel.lendlist.backup;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;
import de.raphaelmichel.lendlist.storage.Database;

public class AndroidBackupAgent extends BackupAgentHelper {

	// A key to uniquely identify the set of backup data
	static final String DATABASE_BACKUP_KEY = "database";

	// Allocate a helper and add it to the backup agent
	@Override
	public void onCreate() {
		FileBackupHelper helper = new FileBackupHelper(this, "../databases/"
				+ Database.DATABASE_NAME);
		// TODO: This is ugly.
		// 1.) FileBackupHelper is not intended to handle big files
		// 2.) The "../databases/" is ugly.
		// 3.) It's not at all thread-safe
		addHelper(DATABASE_BACKUP_KEY, helper);
	}
}