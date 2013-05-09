package packages.models;

/**
 * @author Andrii Dzhyrma and Liliia Chuba
 * 
 *         Notification enum type for a break model
 */
public enum NotificationEnum {
	SOUND(0), VIBRATION(1), MESSAGE(2);

	private final int value;

	NotificationEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
