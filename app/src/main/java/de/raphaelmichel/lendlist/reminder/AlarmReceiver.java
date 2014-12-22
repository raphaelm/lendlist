package de.raphaelmichel.lendlist.reminder;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import de.raphaelmichel.lendlist.R;
import de.raphaelmichel.lendlist.frontend.DetailsActivity;
import de.raphaelmichel.lendlist.frontend.MainActivity;
import de.raphaelmichel.lendlist.objects.Item;
import de.raphaelmichel.lendlist.storage.DataSource;
import de.raphaelmichel.lendlist.storage.Database;
import de.raphaelmichel.lendlist.storage.LendlistContentProvider;

public class AlarmReceiver extends BroadcastReceiver {

	public static int BROADCAST_REMINDER = 2;
	public static int NOTIF_ID = 4;

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context,
				BROADCAST_REMINDER, i, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		// Try again in two hours
		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
				+ (2 * 1000 * 3600), sender);

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);

		if (sp.getBoolean("notify_enabled", false)) {

			// direction is ignored if filter is != null!
			Cursor cursor = null;
			ContentResolver resolver = context.getContentResolver();

			cursor = resolver.query(
					LendlistContentProvider.OBJECT_URI,
					Database.COLUMNS,
					"direction = 'borrowed' AND until > 1 AND returned = 0 AND notified != 1 AND until - "
							+ System.currentTimeMillis()
							+ " < "
							+ (1000 * 3600 * 24 * Integer.parseInt(sp
									.getString("notify_days", "2"))),
					new String[] {}, "until ASC");

			int num = cursor.getCount();

			if (num == 0) {
				cursor.close();
				return;
			}
			cursor.moveToFirst();
			Item firstItem = DataSource.cursorToItem(cursor);
			int[] ids = new int[num];
			int j = 0;
			while (!cursor.isAfterLast()) {
				ids[j] = cursor.getInt(0);
				cursor.moveToNext();
				j++;
			}

			cursor.close();

			NotificationCompat.Builder nb = new NotificationCompat.Builder(
					context);
			nb.setContentInfo(context.getResources().getQuantityString(
					R.plurals.notification_text, num, num));
			nb.setContentTitle(context.getResources().getString(
					R.string.app_name));
			nb.setContentText(firstItem.getThing());
			nb.setTicker(context.getResources().getQuantityString(
					R.plurals.notification_text, num, num));
			nb.setSmallIcon(R.drawable.ic_stat_notification);
			nb.setWhen(firstItem.getDate().getTime());
			nb.setNumber(num);
			nb.setSound(null);

			PendingIntent openPendingIntent = null;
			if (num == 1) {
				Intent openIntent = new Intent(context, DetailsActivity.class);
				openIntent.putExtra("id", firstItem.getId());
				openIntent.putExtra("notified", true);
				openPendingIntent = PendingIntent.getActivity(context, 0,
						openIntent, 0);
			} else {
				Intent openIntent = new Intent(context, MainActivity.class);
				openIntent.putExtra("main_tab", 0);
				openIntent.putExtra("notified", ids);
				openPendingIntent = PendingIntent.getActivity(context, 0,
						openIntent, 0);
			}
			nb.setContentIntent(openPendingIntent);
			nb.setAutoCancel(true);

			Notification notification = nb.build();
			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(NOTIF_ID, notification);
		}

	}
}
