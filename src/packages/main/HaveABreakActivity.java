package packages.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import packages.helpers.Consts;
import packages.helpers.DayListBinder;
import packages.helpers.ServiceProvider;
import packages.helpers.SettingsProvider;
import packages.viewmodels.BreakViewModel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author Andrii Dzhyrma and Liliia Chuba
 * 
 *         Class that represents 'main.xml' page activity
 */
public class HaveABreakActivity extends Activity {
	// initializing id's for menu items
	static final private int SETTINGS_MENU_ITEM = Menu.FIRST;
	static final private int ABOUT_MENU_ITEM = Menu.FIRST + 1;

	// service provider instance
	ServiceProvider serviceProvider;
	// settings provider instance
	private SettingsProvider settingsProvider;
	// View-Model to control all breaks
	private BreakViewModel breakViewModel;
	// number of days that should be viewed on main page
	private int numberOfDays = Consts.NUMBER_OF_DAYS_DEFAULT_VALUE;
	// array of dates on the main screen
	private Date[] mainScreenDays;
	private ToggleButton silentModeToggleButton;
	private ToggleButton switchOffToggleButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// initializing settings provider, service provider and View-Model for
		// breaks
		settingsProvider = new SettingsProvider(getApplicationContext());
		serviceProvider = new ServiceProvider(this, breakViewModel);
		breakViewModel = new BreakViewModel(getApplicationContext().getFilesDir().toString(), getApplicationContext());

		// reactivating service
		serviceProvider.restartService();

		// loading all values for main buttons
		loadValues();
		// loading break information
		loadBreaks();

		// getting interface component references
		final ListView dayListView = (ListView) findViewById(R.id.dayListView);
		switchOffToggleButton = (ToggleButton) findViewById(R.id.switchOffToggleButton);
		silentModeToggleButton = (ToggleButton) findViewById(R.id.silentModeToggleButton);
		final Button calendarButton = (Button) findViewById(R.id.calendarButton);
		final Button addNewsBreakButton = (Button) findViewById(R.id.addNewBreakButton);

		if (dayListView != null)
			dayListView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					if (mainScreenDays != null && mainScreenDays.length > arg2) {
						Intent calendarIntent = new Intent(arg1.getContext(), CalendarActivity.class);
						calendarIntent.putExtra(Consts.CALENDAR_MODE_KEY, Consts.CALENDAR_MODE_DAY_VIEW);
						calendarIntent.putExtra(Consts.CALENDAR_CURRENT_DAY_KEY, mainScreenDays[arg2]);
						startActivity(calendarIntent);
					}
				}
			});
		// setting onCheckedChecnge listeners for all the switchers
		if (switchOffToggleButton != null) {
			switchOffToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (settingsProvider != null) {
						settingsProvider.setSwitchOffValue(isChecked);
						settingsProvider.savePreferences();
						Toast.makeText(getApplicationContext(),
								getResources().getString(isChecked ? R.string.applicationSwitchedOnNotification: R.string.applicationSwitchedOffNotification), Toast.LENGTH_SHORT)
								.show();
						switchOffToggleButton.setBackgroundResource(isChecked ? R.drawable.on : R.drawable.off);
					}

				}
			});
			switchOffToggleButton.setBackgroundResource(settingsProvider.getSwitchOffValue() ? R.drawable.on : R.drawable.off);
		}
		if (silentModeToggleButton != null) {
			silentModeToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (settingsProvider != null) {
						settingsProvider.setSilentModeValue(isChecked);
						settingsProvider.savePreferences();
						Toast.makeText(getApplicationContext(),
								getResources().getString(isChecked ? R.string.silendModeOnNotification : R.string.silendModeOffNotification), Toast.LENGTH_SHORT)
								.show();
						silentModeToggleButton.setBackgroundResource(isChecked ? R.drawable.silenceon : R.drawable.silenceoff);
					}

				}
			});
			silentModeToggleButton.setBackgroundResource(settingsProvider.getSilentModeValue() ? R.drawable.silenceon : R.drawable.silenceoff);
		}
		// setting onClick listeners for all the buttons
		if (calendarButton != null)
			calendarButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent calendarIntent = new Intent(v.getContext(), CalendarActivity.class);
					calendarIntent.putExtra(Consts.CALENDAR_MODE_KEY, Consts.CALENDAR_MODE_MONTH_VIEW);
					startActivity(calendarIntent);
				}
			});
		if (addNewsBreakButton != null)
			addNewsBreakButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent addNewBreakIntent = new Intent(v.getContext(), AddNewBreakActivity.class);
					startActivity(addNewBreakIntent);
				}
			});
	}

	@Override
	protected void onResume() {
		// after resuming we need to update information on the main page
		breakViewModel = new BreakViewModel(getApplicationContext().getFilesDir().toString(), getApplicationContext());
		serviceProvider = new ServiceProvider(getApplicationContext(), breakViewModel);
		serviceProvider.restartService();
		loadValues();
		loadBreaks();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// initializing menu items for option menu
		MenuItem itemSettings = menu.add(0, SETTINGS_MENU_ITEM, Menu.NONE, R.string.settings);
		MenuItem itemRem = menu.add(0, ABOUT_MENU_ITEM, Menu.NONE, R.string.about);
		itemSettings.setIcon(android.R.drawable.ic_menu_preferences);
		itemRem.setIcon(android.R.drawable.ic_menu_info_details);
		itemSettings.setShortcut('0', 's');
		itemRem.setShortcut('1', 'a');
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		// checking what the menu item was pressed and changing to correspondent
		// activity
		switch (item.getItemId()) {
		case (SETTINGS_MENU_ITEM): {
			Intent calendarIntent = new Intent(this, SettingsActivity.class);
			startActivity(calendarIntent);
			return true;
		}
		case (ABOUT_MENU_ITEM): {
			Intent calendarIntent = new Intent(this, AboutActivity.class);
			startActivity(calendarIntent);
			return true;
		}
		default:
			break;
		}
		return false;
	}

	/**
	 * Load all the breaks from file that located in application folder
	 */
	private void loadBreaks() {
		// getting day list interface component reference
		final ListView dayListView = (ListView) findViewById(R.id.dayListView);
		if (dayListView == null)
			return;

		// initializing list that should be binded to interface element
		List<Map<String, Object>> dayList = new ArrayList<Map<String, Object>>();
		// getting current date
		Date currentDate = Calendar.getInstance().getTime();
		// setting the simple date formatter
		SimpleDateFormat dayAndMonthFormat = new SimpleDateFormat(Consts.DAY_AND_MONTH_FORMAT);

		mainScreenDays = new Date[numberOfDays];

		// for each day that we should represent we are building simple adapter
		// with all needed break information
		for (int i = 0; i < numberOfDays; i++, currentDate = new Date(currentDate.getYear(), currentDate.getMonth(), currentDate.getDate() + 1, 0, 0, 0)) {
			mainScreenDays[i] = currentDate;
			Map<String, Object> day = new HashMap<String, Object>();

			// using getDrayBreakList function to get list of all breaks in the
			// currentDate
			List<Map<String, Object>> dayBreakList = breakViewModel.getDayBreakList(currentDate);
			if (dayBreakList == null)
				dayBreakList = new ArrayList<Map<String, Object>>();

			// converting all the information to adaptive view
			day.put(Consts.DAY_VALUE_KEY, dayAndMonthFormat.format(currentDate.getTime()));
			if (dayBreakList.size() == 0)
				day.put(Consts.BREAK_TITLE1_KEY, getResources().getString((i==0) ?R.string.noMoreBreakMessage: R.string.noBreakMessage  ));
			if (dayBreakList.size() > 0)
				day.put(Consts.BREAK_TITLE1_KEY, dayBreakList.get(0).get(Consts.BREAK_TITLE_KEY));
			if (dayBreakList.size() > 1)
				day.put(Consts.BREAK_TITLE2_KEY, dayBreakList.get(1).get(Consts.BREAK_TITLE_KEY));
			if (dayBreakList.size() > 2)
				day.put(Consts.BREAK_TITLE3_KEY, dayBreakList.get(2).get(Consts.BREAK_TITLE_KEY));
			if (dayBreakList.size() > 3) {
				day.put(Consts.DOTS_TEXT_VIEW_KEY, View.VISIBLE);
			} else {
				day.put(Consts.DOTS_TEXT_VIEW_KEY, View.GONE);
			}

			// adding day break information to our list
			dayList.add(day);
		}

		// creating simple adapter for the day list interface element
		SimpleAdapter adapter = new SimpleAdapter(this, dayList, R.layout.daylist_item, new String[] { Consts.DAY_VALUE_KEY, Consts.BREAK_TITLE1_KEY,
				Consts.BREAK_TITLE2_KEY, Consts.BREAK_TITLE3_KEY, Consts.DOTS_TEXT_VIEW_KEY }, new int[] { R.id.dateTextView, R.id.breakTitle1,
				R.id.breakTitle2, R.id.breakTitle3, R.id.dotsTextView });
		adapter.setViewBinder(new DayListBinder());
		dayListView.setAdapter(adapter);
	}

	/**
	 * Load values for 'Switch off' and 'Silent mode' switchers
	 */
	private void loadValues() {
		if (settingsProvider != null) {
			numberOfDays = settingsProvider.getNumberOfDays();
			ToggleButton switchOffToggleButton = (ToggleButton) findViewById(R.id.switchOffToggleButton);
			ToggleButton silentModeToggleButton = (ToggleButton) findViewById(R.id.silentModeToggleButton);
			if (switchOffToggleButton != null)
				switchOffToggleButton.setChecked(settingsProvider.getSwitchOffValue());
			if (silentModeToggleButton != null)
				silentModeToggleButton.setChecked(settingsProvider.getSilentModeValue());
		}
	}
}