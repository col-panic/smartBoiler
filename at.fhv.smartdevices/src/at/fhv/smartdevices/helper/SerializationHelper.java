/**
 * Helper class provides static methods to deserialize and serialize simple objects
 */
package at.fhv.smartdevices.helper;

import java.io.File;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * @author kepe
 * 
 */
public class SerializationHelper {

	/**
	 * Tries to deserialize the data stored in the file to create an instance of
	 * type T and return that.
	 * 
	 * @param instance
	 *            The instance to overwrite - is returned if deserialization is
	 *            not possible
	 * @param filename
	 *            The file of serialized data
	 * @return the instance of type T resulting from deserialization, or the
	 *         passed instance otherwise.
	 */
	public static <T> T deserialize(T instance, String filename) {
		Serializer serializer = new Persister();
		File file = new File(filename);
		if (file.exists()) {
			try {
				return serializer.read(instance, file);
			} catch (Exception e) {
				return instance;
			}
		}
		return instance;
	}

	public static <T> Boolean serialize(T instance, String filename) {
		Serializer serializer = new Persister();
		File file = new File(filename);
		try {
			serializer.write(instance, file);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
