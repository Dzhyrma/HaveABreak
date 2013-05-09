package packages.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * @author Andrii Dzhyrma and Liliia Chuba
 * 
 *         Class for saving and loading settings from the application
 *         SharedPerferences class
 */
public class SettingsProvider {
	// Switch off switcher parameter
	private static final String SWITCH_OFF_KEY = "SWITCH_OFF";
	private static final boolean SWITCH_OFF_DEFAULT_VALUE = true;
	// Silent mode switcher parameter
	private static final String SILENT_MODE_KEY = "SILENT_MODE";
	private static final boolean SILENT_MODE_DEFAULT_VALUE = false;
	// Silent mode type parameter
	private static final String SILENT_MODE_TYPE_KEY = "SILENT_MODE_TYPE";
	private static final int SILENT_MODE_TYPE_DEFAULT_VALUE = 0;
	// Number of days parameter
	private static final String NUMBER_OF_DAYS_KEY = "NUMBER_OF_DAYS";
	private static final int NUMBER_OF_DAYS_DEFAULT_VALUE = Consts.NUMBER_OF_DAYS_DEFAULT_VALUE;

	// Preferences object
	private final SharedPreferences preferences;
	// Preferences editor
	private Editor editor;

	/**
	 * @param key
	 *            - key value of the boolean type parameter
	 * @param defaultValue
	 *            - default boolean type value
	 * @return return boolean type value that stored in the preferences
	 */
	private boolean getBoolean(String key, boolean defaultValue) {
		if (preferences != null)
			return preferences.getBoolean(key, defaultValue);
		else
			return defaultValue;
	}

	/**
	 * @param key
	 *            - key value of the integer type parameter
	 * @param defaultValue
	 *            - default integer type value
	 * @return return integer type value that stored in the preferences
	 */
	private int getInt(String key, int defaultValue) {
		if (preferences != null)
			return preferences.getInt(key, defaultValue);
		else
			return defaultValue;
	}

	/**
	 * @param key
	 *            - key value of the boolean type parameter
	 * @param value
	 *            - boolean type value that should be stored
	 */
	private void setBoolean(String key, boolean value) {
		if (editor != null) {
			editor.putBoolean(key, value);
		}
	}

	/**
	 * @param key
	 *            - key value of the integer type parameter
	 * @param value
	 *            - integer type value that should be stored
	 */
	private void setInt(String key, int value) {
		if (editor != null) {
			editor.putInt(key, value);
		}
	}

	/**
	 * @param context
	 *            - application context that is using for getting preferences
	 *            object
	 */
	public SettingsProvider(Context context) {
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		if (preferences != null)
			editor = preferences.edit();
	}

	/**
	 * @return switch off value
	 */
	public boolean getSwitchOffValue() {
		return getBoolean(SWITCH_OFF_KEY, SWITCH_OFF_DEFAULT_VALUE);
	}

	/**
	 * @param value
	 *            - switch off value
	 */
	public void setSwitchOffValue(boolean value) {
		setBoolean(SWITCH_OFF_KEY, value);
	}

	/**
	 * @return silent mode value
	 */
	public boolean getSilentModeValue() {
		return getBoolean(SILENT_MODE_KEY, SILENT_MODE_DEFAULT_VALUE);
	}

	/**
	 * @param value
	 *            - silent mode value
	 */
	public void setSilentModeValue(boolean value) {
		setBoolean(SILENT_MODE_KEY, value);
	}

	/**
	 * @return silent mode type value
	 */
	public int getSilentModeTypeValue() {
		return getInt(SILENT_MODE_TYPE_KEY, SILENT_MODE_TYPE_DEFAULT_VALUE);
	}

	/**
	 * @param value
	 *            - silent mode type value
	 */
	public void setSilentModeTypeValue(int value) {
		setInt(SILENT_MODE_TYPE_KEY, value);
	}

	/**
	 * @return number of days that should be viewed on main page
	 */
	public int getNumberOfDays() {
		return getInt(NUMBER_OF_DAYS_KEY, NUMBER_OF_DAYS_DEFAULT_VALUE);
	}

	/**
	 * @param value
	 *            - number of days that should be viewed on main page
	 */
	public void setNumberOfDaysValue(int value) {
		setInt(NUMBER_OF_DAYS_KEY, value);
	}

	/**
	 * Save preferences method
	 */
	public void savePreferences() {
		// committing current editor
		if (editor != null)
			editor.commit();
		// creating new editor instance
		if (preferences != null)
			editor = preferences.edit();
	}
}
