import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class Request {

	/* Return a json object from the given url. */
	public static JsonObject getJsonRequest(String url) {
		System.out.println(url);
		JsonObject obj = new JsonObject();
		try {
			URL requestAddress = new URL(url);
			URLConnection uc = requestAddress.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					uc.getInputStream()));
			String inputLine = "";
			String totalInput = "";
			while ((inputLine = in.readLine()) != null) {
				totalInput += inputLine;
			}
			in.close();
			obj = new JsonParser().parse(totalInput).getAsJsonObject();
		} catch (MalformedURLException e) {
			System.out.println("Could not create URL object.");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return obj;
	}

}
