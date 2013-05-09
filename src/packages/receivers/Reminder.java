package packages.receivers;

import packages.helpers.Consts;
import packages.helpers.ServiceProvider;
import packages.helpers.SettingsProvider;
import packages.main.HaveABreakActivity;
import packages.main.R;
import packages.models.BreakModel;
import packages.models.NotificationEnum;
import packages.viewmodels.BreakViewModel;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Vibrator;

/**
 * @author Andrii Dzhyrma and Liliia Chuba
 * 
 *         Class that remind user for each break
 */
public class Reminder extends BroadcastReceiver {
	private SettingsProvider settingsProvider;
	private ServiceProvider serviceProvider;
	private BreakViewModel breakViewModel;

	@Override
	public void onReceive(Context context, Intent intent) {
		breakViewModel = new BreakViewModel(context.getFilesDir().toString(), context);
		serviceProvider = new ServiceProvider(context, breakViewModel);
		settingsProvider = new SettingsProvider(context);

		if (!settingsProvider.getSwitchOffValue())
			return;
		if (context != null) {
			BreakModel breakItem = serviceProvider.getNextBreak();
			if (breakItem == null)
				return;
			NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = new Notification(R.drawable.notification, breakItem.getName(), System.currentTimeMillis());
			if (breakItem.getNotificationType().contains(NotificationEnum.SOUND) && (!settingsProvider.getSilentModeValue())) {
				MediaPlayer mp = MediaPlayer.create(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
				if (mp != null)
					mp.start();
			}
			Intent intentTL = new Intent(context, HaveABreakActivity.class);
			notification.setLatestEventInfo(context, breakItem.getName(),
					(breakItem.getNotificationType().contains(NotificationEnum.MESSAGE) && (!settingsProvider.getSilentModeValue() || settingsProvider
							.getSilentModeTypeValue() >= 2)) ? breakItem.getDescription() : Consts.EMPTY_STRING, PendingIntent.getActivity(context, 0,
							intentTL, PendingIntent.FLAG_CANCEL_CURRENT));
			notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;

			if (breakItem.getNotificationType().contains(NotificationEnum.VIBRATION)
					&& (!settingsProvider.getSilentModeValue() || settingsProvider.getSilentModeTypeValue() == 1 || settingsProvider.getSilentModeTypeValue() == 3)) {
				Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
				if (v != null)
					v.vibrate(1500);
			}

			if (nm != null) {
				nm.notify(1, notification);
			}
		}

		serviceProvider.restartService();
	}

}
