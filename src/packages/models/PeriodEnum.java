package packages.models;

/**
 * @author Andrii Dzhyrma and Liliia Chuba
 * 
 *         Period enum type for a break model
 */
public enum PeriodEnum {
	NONE(0), MINUTE(1), EVERY_DAY(2), WORKING_DAYS(3), MONDAY_WEDNESDAY_FRIDAY(4), TUESDAY_THURSDAY(5), EVERY_WEEK(6), EVERY_MONTH(7), EVERY_YEAR(8);

	private final int value;

	PeriodEnum(int _value) {
		value = _value;
	}

	public int getValue() {
		return value;
	}
}
