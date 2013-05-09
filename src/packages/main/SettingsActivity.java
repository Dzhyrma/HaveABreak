package packages.main;

import packages.helpers.Consts;
import packages.helpers.SettingsProvider;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

/**
 * @author Andrii Dzhyrma and Liliia Chuba
 *
 *         Class that represents 'settings.xml' page activity
 */
public class SettingsActivity extends Activity {
	// settings provider instance
	private SettingsProvider settingsProvider;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		// initializing settings provider
		settingsProvider = new SettingsProvider(getApplicationContext());

		// initializing interface components
		final ToggleButton switchOffToggleButton = (ToggleButton) findViewById(R.id.switchOffToggleButton);
		final ToggleButton silentModeToggleButton = (ToggleButton) findViewById(R.id.silentModeToggleButton);
		final Spinner silentModeTypeSpinner = (Spinner) findViewById(R.id.silentModeTypeSpinner);
		final EditText numberOfDaysEditText = (EditText) findViewById(R.id.numberOfDaysEditText);

		// loading all needed values
		loadValues();

		// setting onCheckedChange listeners for all the switchers
		if (switchOffToggleButton != null)
			switchOffToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (settingsProvider != null) {
						settingsProvider.setSwitchOffValue(isChecked);
						settingsProvider.savePreferences();
					}
				}
			});
		if (silentModeToggleButton != null)
			silentModeToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (settingsProvider != null) {
						settingsProvider.setSilentModeValue(isChecked);
						settingsProvider.savePreferences();
					}
				}
			});
		// setting onItemSelected listener to silentModeType spinner
		if (silentModeTypeSpinner != null)
			silentModeTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					if (settingsProvider != null) {
						settingsProvider.setSilentModeTypeValue(silentModeTypeSpinner.getSelectedItemPosition());
						settingsProvider.savePreferences();
					}
				}

				public void onNothingSelected(AdapterView<?> arg0) {
					silentModeTypeSpinner.setSelection(0);
					if (settingsProvider != null) {
						settingsProvider.setSilentModeTypeValue(0);
						settingsProvider.savePreferences();
					}
				}
			});
		// setting onKey listener when user is editing numberOfDays edit-text
		if (numberOfDaysEditText != null)
			numberOfDaysEditText.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (settingsProvider != null) {
						try {
							int num = Integer.parseInt(numberOfDaysEditText.getText().toString());
							settingsProvider.setNumberOfDaysValue(num > 50 ? 50 :num);
						} catch (NumberFormatException e) {
							settingsProvider.setNumberOfDaysValue(Consts.NUMBER_OF_DAYS_DEFAULT_VALUE);
						}
						settingsProvider.savePreferences();
					}
					//TODO: cut possibility to print more then two digits
					return false;
				}
			});
	}

	/**
	 * Load values for all interface components from the settings provider instance
	 */
	private void loadValues() {
		if (settingsProvider != null) {
			ToggleButton switchOffToggleButton = (ToggleButton) findViewById(R.id.switchOffToggleButton);
			ToggleButton silentModeToggleButton = (ToggleButton) findViewById(R.id.silentModeToggleButton);
			Spinner silentModeTypeSpinner = (Spinner) findViewById(R.id.silentModeTypeSpinner);
			EditText numberOfDaysEditText = (EditText) findViewById(R.id.numberOfDaysEditText);
			if (switchOffToggleButton != null)
				switchOffToggleButton.setChecked(settingsProvider.getSwitchOffValue());
			if (silentModeToggleButton != null)
				silentModeToggleButton.setChecked(settingsProvider.getSilentModeValue());
			if (silentModeTypeSpinner != null)
				silentModeTypeSpinner.setSelection(settingsProvider.getSilentModeTypeValue());
			if (numberOfDaysEditText != null)
				numberOfDaysEditText.setText(Integer.toString(settingsProvider.getNumberOfDays()));
		}
	}

}
