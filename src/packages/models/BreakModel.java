package packages.models;

import java.io.Serializable;
import java.util.Date;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author Andrii Dzhyrma and Liliia Chuba
 * 
 *         Class that represents break element model
 */
public class BreakModel implements Serializable {

	/**
	 * Serialization id
	 */
	private static final long serialVersionUID = -6087807912044862631L;
	/**
	 * Name of the current break
	 */
	private String name;
	/**
	 * Description of the current break
	 */
	private String description;
	/**
	 * Start time of the current break
	 */
	private Date startTime;
	/**
	 * End time of the current break
	 */
	private Date endTime;
	/**
	 * 0 - none, 1 - minute period, 2 - every day, 3 - working days, 4 - Monday,
	 * Wednesday, Friday, 5 - Tuesday, Thursday, 6 - every week, 7 - every
	 * month, 8 - every year
	 */
	private PeriodEnum periodType;
	/**
	 * Period interval of the current break
	 */
	private int periodInterval;
	/**
	 * Set of chosen days if periodic type is 'every week'
	 */
	private Set<Integer> everyWeekDays;
	/**
	 * 0 - none, 1 - sound only, 2 - vibration only, 3 - sound and vibration, 4
	 * - notification, 5 - sound and notification, 6 - vibration and
	 * notification, 7 - sound, vibration and notification
	 */
	private EnumSet<NotificationEnum> notificationType;
	/**
	 * Is current break enabled value
	 */
	private boolean enable;

	/**
	 * Construct new break instance
	 * 
	 * @param name
	 *            - Name of a new break
	 * @param description
	 *            - Description of a new break
	 * @param startTime
	 *            - Start time of a new break
	 * @param endTime
	 *            - End time of a new break
	 * @param periodType
	 *            - Period type of a new break
	 * @param periodInterval
	 *            - Period interval of a new break
	 * @param everyWeekDays
	 *            - Set of chosen days if periodic type is 'every week'
	 * @param notificationType
	 *            - Notification type of a new break
	 * @param enable
	 *            - Is new break enabled
	 */
	public BreakModel(String name, String description, Date startTime, Date endTime, PeriodEnum periodType, int periodInterval, Set<Integer> everyWeekDays,
			EnumSet<NotificationEnum> notificationType, boolean enable) {
		this.name = name;
		this.description = description;
		this.startTime = startTime;
		this.endTime = endTime;
		this.periodType = periodType;
		this.periodInterval = periodInterval;
		this.everyWeekDays = everyWeekDays;
		this.notificationType = notificationType;
		this.enable = enable;
	}

	/**
	 * Set a new name for current break
	 * 
	 * @param name
	 *            - new name value
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set a new description for current break
	 * 
	 * @param description
	 *            - new description value
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Set a new start time for current break
	 * 
	 * @param startTime
	 *            - new start time value
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	/**
	 * Set a new end time for current break
	 * 
	 * @param endTime
	 *            - new end time value
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	/**
	 * Set a new period type for current break
	 * 
	 * @param periodType
	 *            - new period type value
	 */
	public void setPeriodType(PeriodEnum periodType) {
		this.periodType = periodType;
	}

	/**
	 * Set a new period interval for current break
	 * 
	 * @param periodInterval
	 *            - new period interval value
	 */
	public void setPeriodInterval(int periodInterval) {
		this.periodInterval = periodInterval;
	}

	/**
	 * Set a new every week days set for current break
	 * 
	 * @param everyWeekDays
	 *            - new every week days set
	 */
	public void setEveryWeekDays(Set<Integer> everyWeekDays) {
		this.everyWeekDays = everyWeekDays;
	}

	/**
	 * Set a new notification type for current break
	 * 
	 * @param notificationType
	 *            - new notification type value
	 */
	public void setNotificationType(EnumSet<NotificationEnum> notificationType) {
		this.notificationType = notificationType;
	}

	/**
	 * Set a new enable state value for current break
	 * 
	 * @param enable
	 *            - new enable state value
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	/**
	 * Returns the current break name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the current break description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the current break start time
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * Returns the current break end time
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * Returns the current break period type
	 */
	public PeriodEnum getPeriodType() {
		return periodType;
	}

	/**
	 * Returns the current break period interval
	 */
	public int getPeriodInterval() {
		return periodInterval;
	}

	/**
	 * Returns the current break every week days set
	 */
	public Set<Integer> getEveryWeekDays() {
		return everyWeekDays;
	}

	/**
	 * Returns the current break notification type
	 */
	public EnumSet<NotificationEnum> getNotificationType() {
		return notificationType;
	}

	/**
	 * Returns the current break enable state
	 */
	public boolean getEnable() {
		return enable;
	}
}
