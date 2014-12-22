package de.raphaelmichel.lendlist.library;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;

public class RelativeDateBuilder {

	private Context ctx;

	private int colorNeutral;
	private int colorAllGood;
	private int colorSoon;
	private int colorOver;

	private String stringDefaultFormat = "yyyy-MM-dd";
	private int stringDaysLeft;
	private int stringDaysLeft_1;
	private int stringDaysLeft_0;
	private int stringDaysOver;
	private int stringDaysOver_1;

	public static final int DAY = 1000 * 3600 * 24;

	public class RelativeDate {
		public Date date;
		public String text;
		public int color;
	}

	public RelativeDateBuilder(Context ctx) {
		this.ctx = ctx;
	}

	public void setStrings(int defaultFormat, int daysLeft, int daysLeft_1,
			int daysLeft_0, int daysOver, int daysOver_1) {
		this.stringDefaultFormat = ctx.getString(defaultFormat);
		this.stringDaysLeft = daysLeft;
		this.stringDaysLeft_1 = daysLeft_1;
		this.stringDaysLeft_0 = daysLeft_0;
		this.stringDaysOver = daysOver;
		this.stringDaysOver_1 = daysOver_1;
	}

	public void setColors(int resid_neutral, int resid_allGood, int resid_soon,
			int resid_over) {
		colorNeutral = ctx.getResources().getColor(resid_neutral);
		colorAllGood = ctx.getResources().getColor(resid_allGood);
		colorSoon = ctx.getResources().getColor(resid_soon);
		colorOver = ctx.getResources().getColor(resid_over);
	}

	public RelativeDate build(Date date) {
		Calendar now = new GregorianCalendar();
		// Midnight
		long tsToday = new GregorianCalendar(now.get(Calendar.YEAR),
				now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
				.getTimeInMillis();

		long tsUntil = date.getTime();

		RelativeDate rd = new RelativeDate();
		rd.date = date;

		if (tsToday > tsUntil) {

			if (tsToday - tsUntil <= 1 * DAY) {
				rd.text = ctx.getString(stringDaysOver_1);
			} else {
				rd.text = ctx.getString(stringDaysOver,
						(int) ((tsToday - tsUntil) / DAY));
			}
			rd.color = colorOver;

		} else if (tsToday < tsUntil) {

			if (tsUntil - tsToday <= 1 * DAY) {
				rd.color = colorSoon;
				rd.text = ctx.getString(stringDaysLeft_1);
			} else if (tsUntil - tsToday <= 3 * DAY) {
				rd.color = colorSoon;
				rd.text = ctx.getString(stringDaysLeft,
						(int) ((tsUntil - tsToday) / DAY));
			} else if (tsUntil - tsToday <= 14 * DAY) {
				rd.color = colorAllGood;
				rd.text = ctx.getString(stringDaysLeft,
						(int) ((tsUntil - tsToday) / DAY));
			} else {
				rd.color = colorNeutral;
				rd.text = new SimpleDateFormat(stringDefaultFormat)
						.format(date);
			}

		} else {
			// tsNow == tsUntil
			rd.color = colorSoon;
			rd.text = ctx.getString(stringDaysLeft_0);
		}

		return rd;
	}

}
