package packages.helpers;

import packages.main.R;
import android.view.View;
import android.widget.*;

/**
 * @author Andrii Dzhyrma and Liliia Chuba
 * 
 *         Class for binding data to daylist_item.xml element (main screen, data
 *         of breaks per day)
 */
public class DayListBinder implements SimpleAdapter.ViewBinder {
	public boolean setViewValue(View view, Object data, String textRepresentation) {
		// For simple list and for expanding list binding (break list per one
		// day)
		if (view.getId() == R.id.breakListView) {
			((ListView) view).setAdapter((SimpleAdapter) data);
			return true;
			// Binding visibility information to show or hide simple or
			// expanding list
		} else if (view.getId() == R.id.dotsTextView) {
			view.setVisibility((Integer) data);
			return true;
		}
		return false;
	}
}
