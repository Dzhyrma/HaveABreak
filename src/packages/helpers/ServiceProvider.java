package packages.helpers;

import java.util.Calendar;
import java.util.Date;

import packages.models.BreakModel;
import packages.receivers.Reminder;
import packages.viewmodels.BreakViewModel;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;

public class ServiceProvider {

	private BreakViewModel breakViewModel;
	private Context context;

	public ServiceProvider(Context context, BreakViewModel breakViewModel) {
		if (breakViewModel == null)
			this.breakViewModel = new BreakViewModel(context.getFilesDir().toString(), context);
		else
			this.breakViewModel = breakViewModel;
		this.context = context;
	}

	public BreakModel getNextBreak() {
		return ObjectReader.loadObject(context.getFilesDir().toString() + Consts.NEXT_BREAK_FILE_NAME);
	}

	/**
	 * Restart current service for this application
	 */
	public void restartService() {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, Reminder.class);
		Pair<Integer, Date> breakPairItem = breakViewModel.getNextBreakIndex(Calendar.getInstance().getTime());
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		alarmManager.cancel(pendingIntent);
		if (breakPairItem.first < 0)
			return;
		alarmManager.set(AlarmManager.RTC_WAKEUP, breakPairItem.second.getTime(), pendingIntent);
		ObjectWriter.writeObject(breakViewModel.getBreakList().get(breakPairItem.first), context.getFilesDir().toString() + Consts.NEXT_BREAK_FILE_NAME);
	}
}
