package packages.helpers;

import java.util.Comparator;
import java.util.Date;
import java.util.Map;

/**
 * @author Andrii Dzhyrma and Liliia Chuba
 * 
 *         Class that helps to sort break items in a list
 */
public class BreakListComparator implements Comparator<Map<String, Object>> {

	public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
		if (arg0 == null || arg1 == null)
			return 0;
		Date time0 = (Date) arg0.get(Consts.BREAK_TIME_KEY);
		Date time1 = (Date) arg1.get(Consts.BREAK_TIME_KEY);
		if (time0 == null || time1 == null)
			return 0;
		return time0.after(time1) ? 1 : -1;
	}

}
