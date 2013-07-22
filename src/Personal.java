import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Personal {

	private static final String keyFile = "key";
	private static String historyAddress = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?key=";
	private static String detailsAddress = "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/?key=";
	private static final String myId = "76561198005679168";
	
	public static void main(String[] args) {
		loadKey();
		System.out.println("Done");
	}

	
	public static void loadKey() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(keyFile));
			String key = br.readLine();
			key += "&";
			historyAddress += key;
			detailsAddress += key;
		} catch (FileNotFoundException e) {
			System.out.println("File does not exist.");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Could not read line.");
			e.printStackTrace();
			System.exit(1);
		}
	}
}