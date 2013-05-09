package packages.viewmodels;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Pair;

import packages.helpers.BreakListComparator;
import packages.helpers.Consts;
import packages.helpers.ObjectReader;
import packages.helpers.ObjectWriter;
import packages.models.BreakModel;
import packages.models.PeriodEnum;

/**
 * @author Andrii Dzhyrma and Liliia Chuba
 * 
 *         Class that represents View-Model for the break model control
 */
public class BreakViewModel {
	/**
	 * List with all break items
	 */
	private List<BreakModel> breakList;
	/**
	 * File path for our app context folder
	 */
	private final String filePath;
	/**
	 * Current application context
	 */
	private final Context context;

	/**
	 * Construct the break View-Model
	 * 
	 * @param filePath
	 *            - file path for our application context folder
	 * @param context
	 *            - current application context
	 */
	public BreakViewModel(String filePath, Context context) {
		// initializing our private fields
		this.filePath = filePath;
		this.context = context;

		// loading all our breaks from the specified local file
		breakList = ObjectReader.loadObject(filePath + Consts.DATA_FILE_NAME);
		if (breakList == null)
			breakList = new ArrayList<BreakModel>();
	}

	/**
	 * Add new break to the existing list of breaks
	 * 
	 * @param breakItem
	 *            - new break item
	 */
	public void addBreak(BreakModel breakItem) {
		if (breakList == null)
			breakList = new ArrayList<BreakModel>();
		breakList.add(breakItem);
		// saving the break list to local file
		saveBreakList();
	}

	/**
	 * Delete break item from the existing list of breaks
	 * 
	 * @param breakItem
	 *            - deleting break item
	 */
	public boolean deleteBreak(BreakModel breakItem) {
		if (breakList == null || !breakList.contains(breakItem))
			return false;
		breakList.remove(breakItem);
		saveBreakList();
		return true;
	}

	/**
	 * Returns current loaded list with all the breaks
	 */
	public List<BreakModel> getBreakList() {
		return breakList;
	}

	/**
	 * Returns list with useful mapped information for all break at the current
	 * day
	 * 
	 * @param currentDate
	 *            - value of the day for which will be returned list
	 */
	public List<Map<String, Object>> getDayBreakList(Date currentDate) {
		// if input day is null, return null
		if (currentDate == null)
			return null;

		// initializing day break list and time formatter
		List<Map<String, Object>> dayBreakList = new ArrayList<Map<String, Object>>();
		DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);

		// for each break item trying to find all the possible times and put
		// them into list
		for (BreakModel breakItem : breakList) {
			if (breakItem == null || breakItem.getStartTime() == null)
				continue;
			if (breakItem.getEndTime() != null && breakItem.getEndTime().before(currentDate))
				continue; // this break is finished
			Date endOfTheCurrentDay = new Date(currentDate.getYear(), currentDate.getMonth(), currentDate.getDate(), 23, 59, 59);
			Date breakTime = breakItem.getStartTime();
			// if break is no periodic, checking just for starting time
			if (breakItem.getPeriodType() == PeriodEnum.NONE || breakItem.getPeriodType() == null) {
				if (breakTime.before(currentDate) || breakTime.after(endOfTheCurrentDay))
					continue; // this break is not periodic and not in this day
				Map<String, Object> breakMap = new HashMap<String, Object>();
				breakMap.put(Consts.BREAK_TITLE_KEY, " • "+timeFormat.format(breakTime) + " - " + breakItem.getName());
				breakMap.put(Consts.BREAK_TIME_KEY, breakTime);
				breakMap.put(Consts.BREAK_ITEM_KEY, breakItem);
				dayBreakList.add(breakMap);
			} else {
				// searching first start time in the input day
				while (breakTime != null && breakTime.before(currentDate))
					breakTime = getNextBreakTime(breakItem, breakTime);
				if (breakTime == null)
					continue;
				// searching all the times in the input day and storing them
				// into list until the time is in the next day
				while (breakTime != null && breakTime.before(endOfTheCurrentDay) && (breakItem.getEndTime() == null || breakTime.before(breakItem.getEndTime()))) {
					Map<String, Object> breakMap = new HashMap<String, Object>();
					breakMap.put(Consts.BREAK_TITLE_KEY, " • "+timeFormat.format(breakTime) + " - " + breakItem.getName()
							+ ((breakItem.getDescription().length() > 0) ? " (" + breakItem.getDescription() + ")" : Consts.EMPTY_STRING));
					breakMap.put(Consts.BREAK_TIME_KEY, breakTime);
					breakMap.put(Consts.BREAK_ITEM_KEY, breakItem);
					dayBreakList.add(breakMap);
					breakTime = getNextBreakTime(breakItem, breakTime);
				}
			}
		}
		Collections.sort(dayBreakList, new BreakListComparator());
		return dayBreakList;
	}

	/**
	 * Save break list to the local file
	 */
	public void saveBreakList() {
		if (breakList == null)
			breakList = new ArrayList<BreakModel>();
		ObjectWriter.writeObject(breakList, filePath + Consts.DATA_FILE_NAME);
	}
	
	public int getIndex(BreakModel breakItem) {
		if (breakList == null)
			breakList = new ArrayList<BreakModel>();
		return breakList.indexOf(breakItem);
	}
	
	public BreakModel getBreak(int index) {
		if (breakList == null || breakList.size() <= index || index < 0)
			return null;
		return breakList.get(index);
	}


	/**
	 * Returns next time for the current break item
	 * 
	 * @param breakItem
	 *            - break item for which we should find next break time
	 * @param breakTime
	 *            - current break time
	 */
	private Date getNextBreakTime(BreakModel breakItem, Date breakTime) {
		// checking for currect input data
		if (breakItem == null || breakTime == null || breakItem.getPeriodType() == PeriodEnum.NONE || breakItem.getPeriodType() == null)
			return null;

		// initializing current break time as Calendar instance (easily work
		// with)
		Calendar breakCalendarTime = new GregorianCalendar();
		breakCalendarTime.setTime(breakTime);

		// for each different period type doing corresponding changing
		switch (breakItem.getPeriodType()) {
		case MINUTE:
			breakCalendarTime.add(Calendar.MINUTE, breakItem.getPeriodInterval());
			break;
		case EVERY_DAY:
			breakCalendarTime.add(Calendar.DATE, breakItem.getPeriodInterval());
			break;
		case WORKING_DAYS:
			do
				breakCalendarTime.add(Calendar.DATE, 1);
			while (breakCalendarTime.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || breakCalendarTime.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY);
			break;
		case MONDAY_WEDNESDAY_FRIDAY:
			do
				breakCalendarTime.add(Calendar.DATE, 1);
			while (breakCalendarTime.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY && breakCalendarTime.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY
					&& breakCalendarTime.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY);
			break;
		case TUESDAY_THURSDAY:
			do
				breakCalendarTime.add(Calendar.DATE, 1);
			while (breakCalendarTime.get(Calendar.DAY_OF_WEEK) != Calendar.TUESDAY && breakCalendarTime.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY);
			break;
		case EVERY_WEEK:
			Calendar breakCalendarStartTime = new GregorianCalendar();
			breakCalendarStartTime.setTime(breakTime);
			do
				breakCalendarTime.add(Calendar.DATE, 1);
			while (!breakItem.getEveryWeekDays().contains(breakCalendarTime.get(Calendar.DAY_OF_WEEK) - 1)
					|| ((breakCalendarTime.get(Calendar.WEEK_OF_YEAR) - breakCalendarStartTime.get(Calendar.WEEK_OF_YEAR))) % (breakItem.getPeriodInterval()) != 0);
			break;
		case EVERY_MONTH:
			breakCalendarTime.add(Calendar.MONTH, breakItem.getPeriodInterval());
			break;
		case EVERY_YEAR:
			breakCalendarTime.add(Calendar.YEAR, breakItem.getPeriodInterval());
			break;
		default:
			break;
		}
		return breakCalendarTime.getTime();
	}

	/**
	 * Returns next break
	 * 
	 * @param time
	 *            - time value after what will be found next break
	 */
	public Pair<Integer, Date> getNextBreakIndex(Date time) {
		int firstBreakItem = -1;
		Date firstBreakItemStartTime = null;
		for (int i = 0; i < breakList.size(); i++) {
			BreakModel breakItem = breakList.get(i);
			if (breakItem == null || breakItem.getStartTime() == null || !breakItem.getEnable())
				continue;
			if (breakItem.getEndTime() != null && breakItem.getEndTime().before(time))
				continue; // this break is finished
			Date breakTime = breakItem.getStartTime();
			// if break is no periodic, checking just for starting time
			if (breakItem.getPeriodType() == PeriodEnum.NONE || breakItem.getPeriodType() == null) {
				if (breakTime.before(time))
					continue; // this break is not periodic and not before this
								// day
				if (firstBreakItem == -1 || breakTime.before(firstBreakItemStartTime)) {
					firstBreakItem = i;
					firstBreakItemStartTime = breakTime;
				}
			} else {
				// searching first start time in the input day
				while (breakTime != null && breakTime.before(time))
					breakTime = getNextBreakTime(breakItem, breakTime);
				if (breakTime == null)
					continue;
				// searching all the times in the input day and storing them
				// into list until the time is in the next day
				if (firstBreakItem == -1 || breakTime.before(firstBreakItemStartTime)) {
					firstBreakItem = i;
					firstBreakItemStartTime = breakTime;
				}
			}
		}
		return new Pair<Integer, Date>(firstBreakItem, firstBreakItemStartTime);
	}
}
