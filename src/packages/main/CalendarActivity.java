package packages.main;

import java.util.Calendar;
import java.util.Date;

import packages.helpers.Consts;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

/**
 * @author Andrii Dzhyrma and Liliia Chuba
 * 
 *         Class that represents calendar tab activity
 */
public class CalendarActivity extends TabActivity {
	TabHost tabHost;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);

		int currentMode = Consts.CALENDAR_MODE_MONTH_VIEW;
		Date currentDay = Calendar.getInstance().getTime();
		Intent currentIntent = getIntent();
		if (currentIntent != null) {
			Bundle extras = currentIntent.getExtras();
			if (extras != null) {
				currentMode = (extras.containsKey(Consts.CALENDAR_MODE_KEY)) ? extras.getInt(Consts.CALENDAR_MODE_KEY) : Consts.CALENDAR_MODE_MONTH_VIEW;
				currentDay = extras.containsKey(Consts.CALENDAR_CURRENT_DAY_KEY) ? (Date) (extras.get(Consts.CALENDAR_CURRENT_DAY_KEY)) : Calendar
						.getInstance().getTime();
			}
		}
		tabHost = getTabHost();

		// Tab for DayVIew
		TabSpec dayViewSpec = tabHost.newTabSpec(Consts.DAY_TAB_ID);
		dayViewSpec.setIndicator(Consts.EMPTY_STRING, getResources().getDrawable(R.drawable.day_view));
		Intent dayViewIntent = new Intent(this, CalendarDayActivity.class);
		dayViewIntent.putExtra(Consts.CALENDAR_CURRENT_DAY_KEY, currentDay);
		dayViewSpec.setContent(dayViewIntent);

		// Tab for WeekView
		/*TabSpec weekViewSpec = tabHost.newTabSpec(Consts.WEEK_TAB_ID);
		weekViewSpec.setIndicator(getResources().getString(R.string.weekCalendarTab), getResources().getDrawable(R.drawable.ic_launcher));
		Intent weekViewIntent = new Intent(this, CalendarWeekActivity.class);
		weekViewSpec.setContent(weekViewIntent);*/

		// Tab for MonthVIew
		TabSpec monthViewSpec = tabHost.newTabSpec(Consts.MONTH_TAB_ID);
		monthViewSpec.setIndicator(Consts.EMPTY_STRING, getResources().getDrawable(R.drawable.monthly_view));
		Intent monthViewIntent = new Intent(this, CalendarMonthActivity.class);
		monthViewSpec.setContent(monthViewIntent);

		// Adding all TabSpec to TabHost
		tabHost.addTab(dayViewSpec);
		//tabHost.addTab(weekViewSpec);
		tabHost.addTab(monthViewSpec);

		tabHost.setCurrentTab(currentMode);
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				if (tabId == Consts.MONTH_TAB_ID)
				{
					CalendarMonthActivity calendarMonthActivity = (CalendarMonthActivity)getLocalActivityManager().getActivity(tabId);
					if (calendarMonthActivity != null)
						calendarMonthActivity.updateCalendar();
				}
			}
		});
	}
	
	public void switchToDayTab(Date day){
		CalendarDayActivity.currentDay = day;
        tabHost.setCurrentTabByTag(Consts.DAY_TAB_ID);
}

}
