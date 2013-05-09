package packages.helpers;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * @author Andrii Dzhyrma and Liliia Chuba
 * 
 *         Simple object reader class
 */
public class ObjectReader {
	public static <T> T loadObject(String fileName) {
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(fileName);
			in = new ObjectInputStream(fis);
			@SuppressWarnings("unchecked")
			T res = (T) in.readObject();
			fis.close();
			in.close();
			return res;
		} catch (Exception e) {
			return null;
		}
	}
}
