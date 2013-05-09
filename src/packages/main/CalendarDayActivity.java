package packages.main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import packages.helpers.Consts;
import packages.helpers.ServiceProvider;
import packages.models.BreakModel;
import packages.viewmodels.BreakViewModel;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Andrii Dzhyrma and Liliia Chuba
 * 
 */
public class CalendarDayActivity extends Activity {
	public static Date currentDay;

	private List<Map<String, Object>> dayBreakList;
	private BreakViewModel breakViewModel;
	private ListView breakListView;
	private Button currentDayButton, nextDayButton, prevDayButton;
	private OnDateSetListener dateSetListener = new OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			currentDay.setYear(year - Consts.MIN_YEAR_VALUE);
			currentDay.setMonth(monthOfYear);
			currentDay.setDate(dayOfMonth);
			updateButtonText();
			loadBreaks();
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar_day_view);

		breakViewModel = new BreakViewModel(getApplicationContext().getFilesDir().toString(), getApplicationContext());

		Calendar currentDate = Calendar.getInstance();
		currentDay = new Date(currentDate.get(Calendar.YEAR) - Consts.MIN_YEAR_VALUE, currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));

		Intent currentIntent = getIntent();
		if (currentIntent != null) {
			Bundle extras = currentIntent.getExtras();
			if (extras != null && extras.containsKey(Consts.CALENDAR_CURRENT_DAY_KEY)) {
				currentDay = (Date) (extras.get(Consts.CALENDAR_CURRENT_DAY_KEY));
				currentDay = new Date(currentDay.getYear(), currentDay.getMonth(), currentDay.getDate());
			}
		}

		currentDayButton = (Button) findViewById(R.id.currentDay);
		nextDayButton = (Button) findViewById(R.id.nextDay);
		prevDayButton = (Button) findViewById(R.id.prevDay);

		if (currentDayButton != null) {
			currentDayButton.setText(DateFormat.format(Consts.DAY_AND_MONTH_FORMAT, currentDay));
			currentDayButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					showDialog(R.id.currentDay);
				}
			});
		}
		if (nextDayButton != null) {
			nextDayButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					currentDay.setDate(currentDay.getDate() + 1);
					updateButtonText();
					loadBreaks();
				}
			});
		}
		if (prevDayButton != null) {
			prevDayButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					currentDay.setDate(currentDay.getDate() - 1);
					updateButtonText();
					loadBreaks();
				}
			});
		}

		breakListView = (ListView) findViewById(R.id.breakListView);
		if (breakListView != null)
			breakListView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					editBreak(arg2);
				}
			});

		final Button addNewsBreakButton = (Button) findViewById(R.id.addNewBreakButton);
		if (addNewsBreakButton != null)
			addNewsBreakButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent addNewBreakIntent = new Intent(v.getContext(), AddNewBreakActivity.class);
					addNewBreakIntent.putExtra(Consts.DAY_VALUE_KEY, true);
					startActivity(addNewBreakIntent);
				}
			});
		registerForContextMenu(breakListView);

		loadBreaks();
	}

	private void updateButtonText() {
		if (currentDayButton != null) {
			currentDayButton.setText(DateFormat.format(Consts.DAY_MONTH_YEAR_FORMAT, currentDay));
		}
	}

	private void loadBreaks() {
		breakListView = (ListView) findViewById(R.id.breakListView);
		if (breakListView == null)
			return;
		dayBreakList = breakViewModel.getDayBreakList(currentDay);
		if (dayBreakList == null)
			dayBreakList = new ArrayList<Map<String, Object>>();
		SimpleAdapter dayBreakAdapter = new SimpleAdapter(this, dayBreakList, R.layout.breaklist_item, new String[] { Consts.BREAK_TITLE_KEY },
				new int[] { R.id.breakTitle });
		breakListView.setAdapter(dayBreakAdapter);
		TextView noBreakTextView = (TextView) findViewById(R.id.noBreakTextView);
		if (noBreakTextView != null)
			if (dayBreakList.size() == 0)
				noBreakTextView.setVisibility(View.VISIBLE);
			else
				noBreakTextView.setVisibility(View.GONE);
	}

	private void editBreak(int breakIndex) {
		if (dayBreakList != null && breakIndex < dayBreakList.size()) {
			Intent addNewBreakIntent = new Intent(getApplicationContext(), AddNewBreakActivity.class);
			addNewBreakIntent.putExtra(Consts.BREAK_ITEM_KEY, breakViewModel.getIndex((BreakModel)dayBreakList.get(breakIndex).get(Consts.BREAK_ITEM_KEY)));
			startActivity(addNewBreakIntent);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case R.id.currentDay:
			return new DatePickerDialog(this, dateSetListener, currentDay.getYear() + Consts.MIN_YEAR_VALUE, currentDay.getMonth(), currentDay.getDate());
		}
		return null;
	}

	@Override
	protected void onResume() {
		breakViewModel = new BreakViewModel(getApplicationContext().getFilesDir().toString(), getApplicationContext());
		updateButtonText();
		loadBreaks();
		super.onResume();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.breakListView) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			if (dayBreakList != null && info.position < dayBreakList.size()) {
				BreakModel breakItem = (BreakModel) dayBreakList.get(info.position).get(Consts.BREAK_ITEM_KEY);
				menu.setHeaderTitle(breakItem.getName());
				String[] menuItems = getResources().getStringArray(R.array.breakContextMenu);
				for (int i = 0; i < menuItems.length; i++) {
					menu.add(Menu.NONE, i, i, menuItems[i]);
				}
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case (Consts.MENU_ITEM_EDIT):
			editBreak(info.position);
			break;
		case (Consts.MENU_ITEM_DELETE):
			if (breakViewModel != null && dayBreakList != null && info.position < dayBreakList.size()) {
				BreakModel breakItem = (BreakModel) dayBreakList.get(info.position).get(Consts.BREAK_ITEM_KEY);
				if (breakViewModel.deleteBreak(breakItem)) {
					Toast.makeText(getApplicationContext(),
							String.format(getResources().getString(R.string.breakDeletedConfiramtionFormat), breakItem.getName()), Toast.LENGTH_LONG).show();
					loadBreaks();
					new ServiceProvider(getApplicationContext(), breakViewModel).restartService();
				} else
					Toast.makeText(getApplicationContext(), String.format(getResources().getString(R.string.breakDeletedErrorFormat), breakItem.getName()),
							Toast.LENGTH_LONG).show();

			}
			break;
		default:
			break;
		}
		return true;
	}

}
