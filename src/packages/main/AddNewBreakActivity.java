package packages.main;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import packages.helpers.Consts;
import packages.helpers.ServiceProvider;
import packages.models.BreakModel;
import packages.models.NotificationEnum;
import packages.models.PeriodEnum;
import packages.viewmodels.BreakViewModel;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import animation.ExpandAnimation;

/**
 * @author Andrii Dzhyrma and Liliia Chuba
 * 
 *         Class that represents 'addbreak.xml' page activity
 */
public class AddNewBreakActivity extends Activity {
	// View-Model to control all breaks
	private BreakViewModel breakViewModel;
	// instance with information about current break
	private BreakModel currentBreak;
	// start and end time of break
	private Date startTime, endTime;
	// array for the multichoice days result
	private boolean[] daySelections;
	// every week days set
	private Set<Integer> everyWeekDays;
	// array for the multichoice notification result
	private boolean[] notificationSelections = new boolean[NotificationEnum.values().length];
	// notification enum set
	private EnumSet<NotificationEnum> notificationType = EnumSet.allOf(NotificationEnum.class);
	// interface elements
	private Button startDateButton, startTimeButton, endDateButton, endTimeButton, notificationButton, chooseDaysButton;
	private CheckBox periodicCheckBox, enableCheckBox, endTimeCheckBox;
	private EditText nameEditText, descriptionEditText, repeatInputValueEditText;
	private Spinner repeatSpinner, templateSpinner;
	private ExpandAnimation periodicScaleAnim;
	private ExpandAnimation endTimeScaleAnim;
	private ExpandAnimation chooseDaysScaleAnim;
	private ExpandAnimation repeatInputValueScaleAnim;

	private LinearLayout periodicLayout;
	private LinearLayout endTimeLayout;
	private LinearLayout chooseDaysLayout;
	private LinearLayout repeatInputValueLayout;

	private boolean initialized, noChange = false;
	/**
	 * Start date set listener
	 */
	private OnDateSetListener startDateSetListener = new OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			startTime.setYear(year - Consts.MIN_YEAR_VALUE);
			startTime.setMonth(monthOfYear);
			startTime.setDate(dayOfMonth);
			updateDate(startDateButton, startTime);
			if (startTime.after(endTime)) {
				endTime = new Date(startTime.getYear(), startTime.getMonth(), startTime.getDate(), startTime.getHours(), startTime.getMinutes());
				updateDate(endDateButton, endTime);
				updateTime(endTimeButton, endTime);
			}
		}
	};
	/**
	 * Start time set listener
	 */
	private OnTimeSetListener startTimeSetListener = new OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			startTime.setHours(hourOfDay);
			startTime.setMinutes(minute);
			updateTime(startTimeButton, startTime);
			if (startTime.after(endTime)) {
				endTime.setHours(startTime.getHours());
				endTime.setMinutes(startTime.getMinutes());
				updateTime(endTimeButton, endTime);
			}
		}
	};
	/**
	 * End date set listener
	 */
	private OnDateSetListener endDateSetListener = new OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			endTime.setYear(year - Consts.MIN_YEAR_VALUE);
			endTime.setMonth(monthOfYear);
			endTime.setDate(dayOfMonth);
			if (endTime.before(startTime)) {
				showDialog(R.string.endTimeError);
				endTime = new Date(startTime.getYear(), startTime.getMonth(), startTime.getDate(), startTime.getHours(), startTime.getMinutes());
			}
			updateDate(endDateButton, endTime);
			updateTime(endTimeButton, endTime);
		}
	};
	/**
	 * End time set listener
	 */
	private OnTimeSetListener endTimeSetListener = new OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			endTime.setHours(hourOfDay);
			endTime.setMinutes(minute);
			if (endTime.before(startTime)) {
				showDialog(R.string.endTimeError);
				endTime.setHours(startTime.getHours());
				endTime.setMinutes(startTime.getMinutes());
			}
			updateTime(endTimeButton, endTime);
		}
	};

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (!initialized) {
			if (currentBreak == null || currentBreak.getPeriodType() == null || currentBreak.getPeriodType() == PeriodEnum.NONE)
				periodicLayout.getLayoutParams().height = 0;
			periodicScaleAnim = new ExpandAnimation(periodicLayout, periodicLayout.getHeight());
			periodicScaleAnim.setDuration(500);

			if (currentBreak == null || currentBreak.getEndTime() == null)
				endTimeLayout.getLayoutParams().height = 0;
			endTimeScaleAnim = new ExpandAnimation(endTimeLayout, endTimeLayout.getHeight());
			endTimeScaleAnim.setDuration(500);

			if (currentBreak == null || currentBreak.getPeriodType() != PeriodEnum.EVERY_WEEK)
				chooseDaysLayout.getLayoutParams().height = 0;
			chooseDaysScaleAnim = new ExpandAnimation(chooseDaysLayout, chooseDaysLayout.getHeight());
			chooseDaysScaleAnim.setDuration(500);

			if (currentBreak == null || currentBreak.getPeriodType() == PeriodEnum.EVERY_WEEK || currentBreak.getPeriodType() == PeriodEnum.MINUTE
					|| currentBreak.getPeriodType() == PeriodEnum.EVERY_DAY || currentBreak.getPeriodType() == PeriodEnum.EVERY_MONTH
					|| currentBreak.getPeriodType() == PeriodEnum.EVERY_YEAR)
				repeatInputValueLayout.getLayoutParams().height = 0;
			repeatInputValueScaleAnim = new ExpandAnimation(repeatInputValueLayout, repeatInputValueLayout.getHeight());
			repeatInputValueScaleAnim.setDuration(500);
			initialized = true;
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addbreak);

		initialized = false;
		// initialization of BreakViewModel instance
		breakViewModel = new BreakViewModel(getApplicationContext().getFilesDir().toString(), getApplicationContext());
		boolean fromCalendarFlag = false;

		Intent currentIntent = getIntent();
		if (currentIntent != null) {
			Bundle extras = currentIntent.getExtras();
			if (extras != null && extras.containsKey(Consts.BREAK_ITEM_KEY)) {
				currentBreak = breakViewModel.getBreak((Integer) extras.get(Consts.BREAK_ITEM_KEY));
			}
			if (extras != null && extras.containsKey(Consts.DAY_VALUE_KEY))
				fromCalendarFlag = true;
		}

		if (fromCalendarFlag) {
			startTime = CalendarDayActivity.currentDay;
			startTime.setHours(12);
			startTime.setMinutes(0);
			Calendar temp = new GregorianCalendar();
			temp.setTime(startTime);
			endTime = temp.getTime();
		} else if (currentBreak == null) {
			// initializing startTime end endTime break value as current time
			startTime = Calendar.getInstance().getTime();
			startTime.setMinutes(startTime.getMinutes() + 1);
			endTime = Calendar.getInstance().getTime();
			endTime.setMinutes(endTime.getMinutes() + 1);
		} else {
			setTitle(R.string.editBreakTitle);
			startTime = currentBreak.getStartTime();
			Calendar temp = new GregorianCalendar();
			temp.setTime(startTime);
			endTime = (currentBreak.getEndTime() == null) ? temp.getTime() : currentBreak.getEndTime();
		}

		// filling daySelections array and everyWeekDays set
		daySelections = new boolean[getResources().getStringArray(R.array.daysOfWeek).length];
		if (currentBreak == null || currentBreak.getEveryWeekDays() == null) {
			everyWeekDays = new HashSet<Integer>();
			everyWeekDays.add(startTime.getDay());
			daySelections[startTime.getDay()] = true;
		} else {
			everyWeekDays = currentBreak.getEveryWeekDays();
			for (int i = 0; i < daySelections.length; i++)
				if (everyWeekDays.contains(i))
					daySelections[i] = true;
		}

		// filling notificationSelections arrays
		if (currentBreak == null || currentBreak.getNotificationType() == null)
			Arrays.fill(notificationSelections, true);
		else {
			notificationType = EnumSet.noneOf(NotificationEnum.class);
			for (int i = 0; i < notificationSelections.length; i++)
				if (currentBreak.getNotificationType().contains(NotificationEnum.values()[i])) {
					notificationSelections[i] = true;
					notificationType.add(NotificationEnum.values()[i]);
				}
		}

		// initialization of all interface elements that we need
		final Button saveButton = (Button) findViewById(R.id.saveButton);
		periodicLayout = (LinearLayout) findViewById(R.id.periodicLayout);
		chooseDaysLayout = (LinearLayout) findViewById(R.id.chooseDaysLayout);
		repeatInputValueLayout = (LinearLayout) findViewById(R.id.repeatInputValueLayout);
		endTimeLayout = (LinearLayout) findViewById(R.id.endTimeLayout);
		repeatInputValueEditText = (EditText) findViewById(R.id.repeatInputValueEditText);
		periodicCheckBox = (CheckBox) findViewById(R.id.periodicCheckBox);
		endTimeCheckBox = (CheckBox) findViewById(R.id.endTimeCheckBox);
		enableCheckBox = (CheckBox) findViewById(R.id.enableCheckBox);
		notificationButton = (Button) findViewById(R.id.notificationButton);
		chooseDaysButton = (Button) findViewById(R.id.chooseDaysButton);
		startDateButton = (Button) findViewById(R.id.startDateButton);
		startTimeButton = (Button) findViewById(R.id.startTimeButton);
		endDateButton = (Button) findViewById(R.id.endDateButton);
		endTimeButton = (Button) findViewById(R.id.endTimeButton);
		nameEditText = (EditText) findViewById(R.id.nameEditText);
		descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
		repeatSpinner = (Spinner) findViewById(R.id.repeatSpinner);
		templateSpinner = (Spinner) findViewById(R.id.templateSpinner);

		// button click listener for all data and time pickers
		OnClickListener onTimeButtonClick = new OnClickListener() {
			public void onClick(View v) {
				showDialog(v.getId());
			}
		};

		// setting save button onClick listener
		if (saveButton != null)
			saveButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (breakViewModel != null) {
						// getting all the information from the input components
						String name = (nameEditText != null) ? nameEditText.getText().toString() : Consts.EMPTY_STRING;
						if (name.length() == 0) {
							showDialog(R.string.nameEmptyError);
							return;
						}
						String description = (descriptionEditText != null) ? descriptionEditText.getText().toString() : Consts.EMPTY_STRING;
						PeriodEnum periodType = (periodicCheckBox != null && periodicCheckBox.isChecked()) ? PeriodEnum.values()[repeatSpinner
								.getSelectedItemPosition() + 1] : PeriodEnum.NONE;
						if (periodType == PeriodEnum.EVERY_WEEK && (everyWeekDays == null || everyWeekDays.isEmpty())) {
							showDialog(R.string.chosenDaysError);
							return;
						}
						boolean enable = enableCheckBox != null && enableCheckBox.isChecked();
						Date newEndTime = (endTimeCheckBox != null && endTimeCheckBox.isChecked()) ? endTime : null;
						int periodValue;
						try {
							periodValue = (repeatInputValueEditText != null) ? Integer.parseInt(repeatInputValueEditText.getText().toString()) : 0;
						} catch (NumberFormatException e) {
							periodValue = 1;
						}
						startTime.setSeconds(0);
						if (currentBreak == null) {
							currentBreak = new BreakModel(name, description, startTime, newEndTime, periodType, periodValue, everyWeekDays, notificationType,
									enable);
							breakViewModel.addBreak(currentBreak);
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.addNewBreakConfirmation), Toast.LENGTH_LONG).show();

						} else {
							currentBreak.setName(name);
							currentBreak.setDescription(description);
							currentBreak.setStartTime(startTime);
							currentBreak.setEndTime(newEndTime);
							currentBreak.setPeriodType(periodType);
							currentBreak.setPeriodInterval(periodValue);
							currentBreak.setEveryWeekDays(everyWeekDays);
							currentBreak.setNotificationType(notificationType);
							currentBreak.setEnable(enable);
							breakViewModel.saveBreakList();
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.breakEditingConfirmation), Toast.LENGTH_LONG).show();
						}
						new ServiceProvider(getApplicationContext(), null).restartService();
						finish();
					}
				}
			});
		// checking if we need to show periodic info or not
		if (periodicCheckBox != null) {
			periodicCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					LinearLayout periodicLayout = (LinearLayout) findViewById(R.id.periodicLayout);
					if (periodicLayout != null && initialized) {
						if (isChecked)
							periodicLayout.startAnimation(periodicScaleAnim.expand());
						else
							periodicLayout.startAnimation(periodicScaleAnim.collapse());
					}
				}

			});
		}
		// checking if we need to show end time info or not
		if (endTimeCheckBox != null) {
			endTimeCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					LinearLayout endTimeLayout = (LinearLayout) findViewById(R.id.endTimeLayout);
					if (endTimeLayout != null && initialized) {
						if (isChecked)
							endTimeLayout.startAnimation(endTimeScaleAnim.expand());
						else
							endTimeLayout.startAnimation(endTimeScaleAnim.collapse());
					}
				}
			});
		}

		// setting to all the data and time pickers the same onClick listener
		// instance
		if (startDateButton != null)
			startDateButton.setOnClickListener(onTimeButtonClick);
		if (startTimeButton != null)
			startTimeButton.setOnClickListener(onTimeButtonClick);
		if (endDateButton != null)
			endDateButton.setOnClickListener(onTimeButtonClick);
		if (endTimeButton != null)
			endTimeButton.setOnClickListener(onTimeButtonClick);
		if (notificationButton != null) {
			notificationButton.setText(getNotificationText());
			notificationButton.setOnClickListener(onTimeButtonClick);
		}
		if (chooseDaysButton != null) {
			chooseDaysButton.setText(getChosenDaysText());
			chooseDaysButton.setOnClickListener(onTimeButtonClick);
		}

		if (currentBreak != null) {
			if (currentBreak.getName() != null && nameEditText != null)
				nameEditText.setText(currentBreak.getName());
			if (currentBreak.getDescription() != null && descriptionEditText != null)
				descriptionEditText.setText(currentBreak.getDescription());
			if (currentBreak.getPeriodType() != null && currentBreak.getPeriodType() != PeriodEnum.NONE) {
				periodicCheckBox.setChecked(true);
				repeatSpinner.setSelection(currentBreak.getPeriodType().getValue() - 1);
				noChange = true;
			}
			if (currentBreak.getEndTime() != null) {
				endTimeCheckBox.setChecked(true);
			}
			repeatInputValueEditText.setText(Integer.toString(currentBreak.getPeriodInterval()));
			enableCheckBox.setChecked(currentBreak.getEnable());
		}

		if (templateSpinner != null) {
			templateSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					Calendar currentTime1 = Calendar.getInstance();
					currentTime1.setTime(startTime);
					Calendar currentTime2 = Calendar.getInstance();
					currentTime2.setTime(startTime);
					String[] templates = getResources().getStringArray(R.array.templates);
					String[] templateNames = getResources().getStringArray(R.array.templateNames);
					switch (arg2) {
					case 0:
						break;
					case 1:
						currentTime1.set(Calendar.HOUR_OF_DAY, 12);
						currentTime1.set(Calendar.MINUTE, 0);
						currentTime2.set(Calendar.HOUR_OF_DAY, 18);
						currentTime2.set(Calendar.MINUTE, 0);
						fillFields(currentTime1.getTime(), currentTime2.getTime(), templateNames[arg2], templates[arg2], true, 0, 60);
						break;
					case 2:
						currentTime1.set(Calendar.HOUR_OF_DAY, 14);
						currentTime1.set(Calendar.MINUTE, 0);
						currentTime2.set(Calendar.HOUR_OF_DAY, 18);
						currentTime2.set(Calendar.MINUTE, 0);
						fillFields(currentTime1.getTime(), null, templateNames[arg2], templates[arg2], true, 1, 1);
						break;
					case 3:
						currentTime1.set(Calendar.HOUR_OF_DAY, 18);
						currentTime1.set(Calendar.MINUTE, 0);
						currentTime2.set(Calendar.HOUR_OF_DAY, 18);
						currentTime2.set(Calendar.MINUTE, 0);
						fillFields(currentTime1.getTime(), null, templateNames[arg2], templates[arg2], true, 1, 1);
						break;
					case 4:
						currentTime1.set(Calendar.HOUR_OF_DAY, 19);
						currentTime1.set(Calendar.MINUTE, 0);
						currentTime2.set(Calendar.HOUR_OF_DAY, 18);
						currentTime2.set(Calendar.MINUTE, 0);
						fillFields(currentTime1.getTime(), null, templateNames[arg2], templates[arg2], true, 3, 1);
						break;
					case 5:
						currentTime1.set(Calendar.HOUR_OF_DAY, 8);
						currentTime1.set(Calendar.MINUTE, 0);
						currentTime2.set(Calendar.HOUR_OF_DAY, 18);
						currentTime2.set(Calendar.MINUTE, 0);
						fillFields(currentTime1.getTime(), null, templateNames[arg2], templates[arg2], true, 0, 720);
						break;
					default:

						break;
					}
				}

				public void onNothingSelected(AdapterView<?> arg0) {
					templateSpinner.setSelection(0);
				}

				public void fillFields(Date startTime2, Date endTime2, String name, String description, boolean isPeriodic, int perioicdIndex, int periodicValue) {
					startTime = startTime2;
					if (endTime2 != null) {
						endTime = endTime2;
						endTimeCheckBox.setChecked(true);
					} else
						endTimeCheckBox.setChecked(false);
					nameEditText.setText(name);
					descriptionEditText.setText(description);
					periodicCheckBox.setChecked(isPeriodic);
					noChange = true;
					repeatSpinner.setSelection(perioicdIndex);
					repeatInputValueEditText.setText(Integer.toString(periodicValue));
					updateDate(startDateButton, startTime);
					updateTime(startTimeButton, startTime);
					updateDate(endDateButton, endTime);
					updateTime(endTimeButton, endTime);
				}
			});
		}

		// checking the different cases for periodic break type and showing the
		// correspondent info
		if (repeatSpinner != null) {
			repeatSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					PeriodEnum type = PeriodEnum.values()[repeatSpinner.getSelectedItemPosition() + 1];
					switch (type) {
					case MINUTE:
						setRepeatPeriodValues(R.string.minutePeriod, Consts.DEFAULT_MINUT_PERIOD);
						setChooseDaysLayoutVisibility(false);
						break;
					case EVERY_DAY:
						setRepeatPeriodValues(R.string.dayPeriod, Consts.DEFAULT_DAY_PERIOD);
						setChooseDaysLayoutVisibility(false);
						break;
					case EVERY_WEEK:
						setRepeatPeriodValues(R.string.weekPeriod, Consts.DEFAULT_WEEK_PERIOD);
						setChooseDaysLayoutVisibility(true);
						break;
					case EVERY_MONTH:
						setRepeatPeriodValues(R.string.monthPeriod, Consts.DEFAULT_MONTH_PERIOD);
						setChooseDaysLayoutVisibility(false);
						break;
					case EVERY_YEAR:
						setRepeatPeriodValues(R.string.yearPeriod, Consts.DEFAULT_YEAR_PERIOD);
						setChooseDaysLayoutVisibility(false);
						break;
					default:
						setRepeatInputValueLayoutVisibility(false);
						setChooseDaysLayoutVisibility(false);
						break;
					}
				}

				public void onNothingSelected(AdapterView<?> arg0) {
					repeatSpinner.setSelection(0);
				}

				/**
				 * @param visible
				 *            - if choose day layout should be visible
				 */
				private void setChooseDaysLayoutVisibility(boolean visible) {
					if (chooseDaysLayout != null)
						if (visible)
							chooseDaysLayout.startAnimation(chooseDaysScaleAnim.expand());
						else
							chooseDaysLayout.startAnimation(chooseDaysScaleAnim.collapse());
					// *****************************************************************************************//
				}

				/**
				 * @param visible
				 *            - if repeat input value layout should be visible
				 */
				private void setRepeatInputValueLayoutVisibility(boolean visible) {
					if (repeatInputValueLayout != null)
						if (visible)
							repeatInputValueLayout.startAnimation(repeatInputValueScaleAnim.expand());
						else
							repeatInputValueLayout.startAnimation(repeatInputValueScaleAnim.collapse());
				}

				/**
				 * @param id
				 *            - string id for repeat period text value
				 * @param defaultMinutPeriod
				 *            - period interval
				 */
				private void setRepeatPeriodValues(int id, String defaultMinutPeriod) {
					setRepeatInputValueLayoutVisibility(true);
					TextView repeatInputValueTextView = (TextView) findViewById(R.id.repeatInputValueTextView);
					if (repeatInputValueTextView != null)
						repeatInputValueTextView.setText(id);
					if (repeatInputValueEditText != null && !noChange)
						repeatInputValueEditText.setText(defaultMinutPeriod);
					noChange = false;
				}
			});
		}
		// updating text representation on buttons
		updateDate(startDateButton, startTime);
		updateTime(startTimeButton, startTime);
		updateDate(endDateButton, endTime);
		updateTime(endTimeButton, endTime);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case R.id.startDateButton:
			return new DatePickerDialog(this, startDateSetListener, startTime.getYear() + Consts.MIN_YEAR_VALUE, startTime.getMonth(), startTime.getDate());
		case R.id.startTimeButton:
			return new TimePickerDialog(this, startTimeSetListener, startTime.getHours(), startTime.getMinutes(),
					android.text.format.DateFormat.is24HourFormat(this));
		case R.id.endDateButton:
			return new DatePickerDialog(this, endDateSetListener, endTime.getYear() + Consts.MIN_YEAR_VALUE, endTime.getMonth(), endTime.getDate());
		case R.id.endTimeButton:
			return new TimePickerDialog(this, endTimeSetListener, endTime.getHours(), endTime.getMinutes(), android.text.format.DateFormat.is24HourFormat(this));
		case R.id.notificationButton:
			return new AlertDialog.Builder(this).setTitle(R.string.notificationType)
					.setMultiChoiceItems(R.array.notificationType, notificationSelections, new DialogInterface.OnMultiChoiceClickListener() {
						public void onClick(DialogInterface dialog, int which, boolean isChecked) {
							if (notificationSelections != null && notificationSelections.length > which) {
								notificationSelections[which] = isChecked;
							}
							notificationType = EnumSet.noneOf(NotificationEnum.class);
							for (int i = 0; i < notificationSelections.length; i++)
								if (i < NotificationEnum.values().length && notificationSelections[i])
									notificationType.add(NotificationEnum.values()[i]);
						}
					}).setPositiveButton(Consts.OK, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (notificationButton != null)
								notificationButton.setText(getNotificationText());
						}
					}).create();
		case R.id.chooseDaysButton:
			return new AlertDialog.Builder(this).setTitle(R.string.chooseDays)
					.setMultiChoiceItems(R.array.daysOfWeek, daySelections, new DialogInterface.OnMultiChoiceClickListener() {
						public void onClick(DialogInterface dialog, int which, boolean isChecked) {
							if (daySelections != null && daySelections.length > which) {
								daySelections[which] = isChecked;
							}
							everyWeekDays = new HashSet<Integer>();
							for (int i = 0; i < daySelections.length; i++)
								if (daySelections[i])
									everyWeekDays.add(i);
						}
					}).setPositiveButton(Consts.OK, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (everyWeekDays == null || everyWeekDays.isEmpty()) {
								showDialog(R.string.chosenDaysError);
							}
							if (chooseDaysButton != null)
								chooseDaysButton.setText(getChosenDaysText());
						}
					}).create();
		default:
			return new AlertDialog.Builder(this).setTitle(R.string.attentionTitle).setMessage(id).setPositiveButton(Consts.OK, null).create();
		}
	}

	/**
	 * Update date text on the input button
	 * 
	 * @param button
	 *            - input button
	 * @param date
	 *            - value that we should print on the button
	 */
	private void updateDate(Button button, Date date) {
		if (button != null) {
			DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
			button.setText(dateFormat.format(date));
		}
	}

	/**
	 * Update time text on the input button
	 * 
	 * @param button
	 *            - sender
	 * @param time
	 *            - value that we should print on the button
	 */
	private void updateTime(Button button, Date time) {
		if (button != null) {
			DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getApplicationContext());
			button.setText(timeFormat.format(time));
		}
	}

	/**
	 * Returns selected notification items as a string
	 */
	private String getNotificationText() {
		String[] notificationTypes = getResources().getStringArray(R.array.notificationType);
		if (notificationType == null || notificationTypes == null || notificationType.isEmpty())
			return getResources().getString(R.string.none);
		String notificationText = Consts.EMPTY_STRING;
		for (int i = 0; i < notificationTypes.length; i++)
			if (notificationType.contains(NotificationEnum.values()[i]))
				notificationText += notificationTypes[i] + Consts.ITEM_SEPARATOR;
		return (notificationText.length() >= Consts.ITEM_SEPARATOR.length()) ? notificationText.substring(0,
				notificationText.length() - Consts.ITEM_SEPARATOR.length()) : notificationText;
	}

	/**
	 * Returns selected chosen days for every week period
	 */
	private String getChosenDaysText() {
		String[] daysOfWeek = getResources().getStringArray(R.array.daysOfWeek);
		if (daySelections == null || daysOfWeek == null || everyWeekDays.isEmpty())
			return getResources().getString(R.string.none);
		String chooseDaysText = Consts.EMPTY_STRING;
		for (int i = 0; i < daySelections.length; i++)
			if (daysOfWeek.length > i && daySelections[i])
				chooseDaysText += daysOfWeek[i] + Consts.ITEM_SEPARATOR;
		return (chooseDaysText.length() >= Consts.ITEM_SEPARATOR.length()) ? chooseDaysText.substring(0,
				chooseDaysText.length() - Consts.ITEM_SEPARATOR.length()) : chooseDaysText;
	}
}
