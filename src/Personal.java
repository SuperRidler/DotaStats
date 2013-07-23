import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Personal {

	private static final String keyFile = "key";
	private static String historyAddress = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?key=";
	private static String detailsAddress = "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/?key=";
	private static final String myId = "76561198005679168";
	private static ArrayList<Integer> matchIds;

	public static void main(String[] args) {
		loadKey();
		getMatchIds();
		loadMatches();
		System.out.println("Done");
	}

	/* Attach the API key to the URLs for use. */
	public static void loadKey() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(keyFile));
			String key = br.readLine();
			br.close();
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

	/* Puts all the match ids into an ArrayList for the predefined player id. */
	public static void getMatchIds() {
		matchIds = new ArrayList<Integer>();
		String matchId = "";
		int matchesLeft = 25;
		while (matchesLeft > 0) {
			String personalLink = historyAddress + "account_id=" + myId
					+ "&start_at_match_id=" + matchId;
			JsonObject obj = getJsonRequest(personalLink);
			JsonObject result = obj.getAsJsonObject("result");
			matchesLeft = result.get("results_remaining").getAsInt();
			JsonArray matches = result.getAsJsonArray("matches");
			Iterator<JsonElement> it = matches.iterator();
			while (it.hasNext()) {
				JsonObject iobj = it.next().getAsJsonObject();
				matchIds.add(iobj.get("match_id").getAsInt());
			}
			matchId = String.valueOf(matchIds.get(matchIds.size()-1) - 1);
		}
		System.out.println(matchIds);
		System.out.println(matchIds.size());	}

	/* Get the match details for a match id int. */
	public static JsonObject getMatchDetails(int matchId) {
		return getMatchDetails(String.valueOf(matchId));
	}
	
	/* Get the match details for a match id string. */
	public static JsonObject getMatchDetails(String matchId) {
		return getJsonRequest(detailsAddress + "match_id=" + matchId + "&");
	}
	
	/* Use the match ids to load the match data. */
	public static void loadMatches() {
		for (Integer i : matchIds) {
			JsonObject matchInfo = getMatchDetails(i);
			JsonObject resultObj = matchInfo.getAsJsonObject("result");
			JsonArray players = resultObj.getAsJsonArray("players");
			for (JsonElement player : players) {
				JsonObject playerObj = player.getAsJsonObject();
				int account_id = playerObj.get("account_id").getAsInt();
				int player_slot = playerObj.get("player_slot").getAsInt();
				int hero_id = playerObj.get("hero_id").getAsInt();
				int item_0 = playerObj.get("item_0").getAsInt();
				int item_1 = playerObj.get("item_1").getAsInt();
				int item_2 = playerObj.get("item_2").getAsInt();
				int item_3 = playerObj.get("item_3").getAsInt();
				int item_4 = playerObj.get("item_4").getAsInt();
				int item_5 = playerObj.get("item_5").getAsInt();
				int kills = playerObj.get("kills").getAsInt();
				int deaths = playerObj.get("deaths").getAsInt();
				int assists = playerObj.get("assists").getAsInt();
				int leaver_status = playerObj.get("leaver_status").getAsInt();
				int gold = playerObj.get("gold").getAsInt();
				int last_hits = playerObj.get("last_hits").getAsInt();
				int denies = playerObj.get("denies").getAsInt();
				int gold_per_min = playerObj.get("gold_per_min").getAsInt();
				int xp_per_min = playerObj.get("xp_per_min").getAsInt();
				int gold_spent = playerObj.get("gold_spent").getAsInt();
				int hero_damage = playerObj.get("hero_damage").getAsInt();
				int tower_damage = playerObj.get("tower_damage").getAsInt();
				int hero_healing = playerObj.get("hero_healing").getAsInt();
				int level = playerObj.get("level").getAsInt();
				JsonArray abilities = playerObj.getAsJsonArray("ability_upgrades");
				for (JsonElement ability_upgrade_elem : abilities) {
					JsonObject ability_upgrade = ability_upgrade_elem.getAsJsonObject();
					int ability = ability_upgrade.get("ability").getAsInt();
					int time = ability_upgrade.get("time").getAsInt();
					int level = ability_upgrade.get("level").getAsInt();
				}
			}
		}
	}
	
	/* Return a json object from the  given url. */
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
