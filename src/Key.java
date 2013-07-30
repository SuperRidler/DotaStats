import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Key {
	
	private static final String keyFile = "key";

	/* Attach the API key to the URLs for use. */
	public static String loadKey() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(keyFile));
			String key = br.readLine();
			br.close();
			key += "&";
			return key;
		} catch (FileNotFoundException e) {
			System.out.println("Key File does not exist.");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Could not read key file line.");
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

}
