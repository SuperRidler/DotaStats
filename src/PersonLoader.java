import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class PersonLoader {

	private static String historyAddress = "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?key=";
	private static String detailsAddress = "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/?key=";
	private static ArrayList<Integer> matchIds;
	private static Connection con;
	private static String id;
	private static boolean debug = false;
	private static File file = new File("log.txt");
	private static BufferedWriter log;

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Must pass argument to program: steamid");
			System.exit(1);
		} else {
			id = args[0];
		}
		if (args.length > 1 && args[1].equals("debug")) {
			debug = true;
		}
		try {
			log  = new BufferedWriter(new FileWriter(file));
			log.append("ID: "+id+"\n");
		} catch (IOException e) {
			System.out.println("Could not open log file for writing.");
			e.printStackTrace();
		}
		con = DBUtil.connectToDB();
		loadKey();
		getMatchIds();
		loadMatches();
		DBUtil.closeDB(con);
		System.out.println("Done");
	}

	private static void loadKey() {
		String key = Key.loadKey();
		historyAddress += key;
		detailsAddress += key;
	}
	
	/* Puts all the match ids into an ArrayList for the predefined player id. */
	public static void getMatchIds() {
		matchIds = new ArrayList<Integer>();
		String matchId = "";
		int matchesLeft = 25;
		while (matchesLeft > 0) {
			String personalLink = historyAddress + "account_id=" + id
					+ "&start_at_match_id=" + matchId;
			JsonObject obj = Request.getJsonRequest(personalLink);
			/* Logging. */
			try {
				log.append(personalLink+"\n");
				log.append(obj.toString()+"\n");
			} catch (IOException e) {
				System.out.println("Could not write to log file.");
				e.printStackTrace();
			}
			if (debug) System.out.println(obj);
			JsonObject result = obj.getAsJsonObject("result");
			matchesLeft = result.get("results_remaining").getAsInt();
			JsonArray matches = result.getAsJsonArray("matches");
			Iterator<JsonElement> it = matches.iterator();
			while (it.hasNext()) {
				JsonObject iobj = it.next().getAsJsonObject();
				matchIds.add(iobj.get("match_id").getAsInt());
			}
			matchId = String.valueOf(matchIds.get(matchIds.size() - 1) - 1);
		}
		System.out.println(matchIds);
		System.out.println(matchIds.size());
		/* Logging. */
		try {
			log.append(matchIds.toString()+"\n");
			log.append(matchIds.toString()+"\n");
		} catch (IOException e) {
			System.out.println("Could not write to log file.");
			e.printStackTrace();
		}
	}

	/* Get the match details for a match id int. */
	public static JsonObject getMatchDetails(int matchId) {
		return getMatchDetails(String.valueOf(matchId));
	}

	/* Get the match details for a match id string. */
	public static JsonObject getMatchDetails(String matchId) {
		return Request.getJsonRequest(detailsAddress + "match_id=" + matchId + "&");
	}

	/* Use the match ids to load the match data. */
	public static void loadMatches() {
		for (Integer i : matchIds) {
			System.out.println("Fetching Match: " + i);
			JsonObject matchInfo = getMatchDetails(i);
			JsonObject resultObj = matchInfo.getAsJsonObject("result");
			int match_id = resultObj.get("match_id").getAsInt();
			/* Check if this match has been recorded. */
			String query = "SELECT * FROM matches WHERE match_id = ?";
			PreparedStatement ps;
			ResultSet rs;
			try {
				ps = (PreparedStatement) con.prepareStatement(query);
				ps.setInt(1, match_id);
				rs = ps.executeQuery();
				if (!rs.next()) {
					loadIntoDB(resultObj);
				}
			} catch (SQLException e) {
				System.out
						.println("Failed to check database about match entry.");
				e.printStackTrace();
			}
		}
	}

	public static void loadIntoDB(JsonObject resultObj) {
		int match_id = resultObj.get("match_id").getAsInt();
		String query;
		PreparedStatement ps;
		ResultSet rs;
		JsonArray players = resultObj.getAsJsonArray("players");
		int[] playerKeys = new int[10];
		int keyPos = 0;
		for (JsonElement player : players) {
			JsonObject playerObj = player.getAsJsonObject();
			int account_id = 0;
			/* Game may contain bots. */
			if (playerObj.has("account_id")) {
				account_id = playerObj.get("account_id").getAsInt();
			}
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
			int leaver_status = 0;
			if (playerObj.has("leaver_status")) {
				leaver_status = playerObj.get("leaver_status").getAsInt();
			}
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
			int playerKey = 0;
			try {
				query = "INSERT INTO player (account_id, player_slot, hero_id, item_0, item_1, item_2, item_3, item_4, item_5, kills, deaths, assists, leaver_status, gold, last_hits, denies, gold_per_min, xp_per_min, gold_spent, hero_damage, tower_damage, hero_healing, level) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				ps = (PreparedStatement) con.prepareStatement(query,
						Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, account_id);
				ps.setInt(2, player_slot);
				ps.setInt(3, hero_id);
				ps.setInt(4, item_0);
				ps.setInt(5, item_1);
				ps.setInt(6, item_2);
				ps.setInt(7, item_3);
				ps.setInt(8, item_4);
				ps.setInt(9, item_5);
				ps.setInt(10, kills);
				ps.setInt(11, deaths);
				ps.setInt(12, assists);
				ps.setInt(13, leaver_status);
				ps.setInt(14, gold);
				ps.setInt(15, last_hits);
				ps.setInt(16, denies);
				ps.setInt(17, gold_per_min);
				ps.setInt(18, xp_per_min);
				ps.setInt(19, gold_spent);
				ps.setInt(20, hero_damage);
				ps.setInt(21, tower_damage);
				ps.setInt(22, hero_healing);
				ps.setInt(23, level);
				ps.executeUpdate();
				rs = ps.getGeneratedKeys();
				rs.next();
				playerKey = rs.getInt(1);
				playerKeys[keyPos] = playerKey;
			} catch (SQLException e) {
				System.out.println("Statement error.");
				e.printStackTrace();
			}
			if (playerObj.has("ability_upgrades")) {
				JsonArray abilities = playerObj
						.getAsJsonArray("ability_upgrades");
				for (JsonElement ability_upgrade_elem : abilities) {
					JsonObject ability_upgrade = ability_upgrade_elem
							.getAsJsonObject();
					int ability = ability_upgrade.get("ability").getAsInt();
					int time = ability_upgrade.get("time").getAsInt();
					int levell = ability_upgrade.get("level").getAsInt();
					query = "INSERT INTO ability_upgrades (id, level, time, ability) VALUES (?, ?, ?, ?)";
					try {
						ps = (PreparedStatement) con.prepareStatement(query);
						ps.setInt(1, playerKey);
						ps.setInt(2, levell);
						ps.setInt(3, time);
						ps.setInt(4, ability);
						ps.executeUpdate();
					} catch (SQLException e) {
						System.out
								.println("Could not generate ability_upgrade statement.");
						e.printStackTrace();
					}
				}
			}
			keyPos++;
		}
		boolean radiant_win = resultObj.get("radiant_win").getAsBoolean();
		int duration = resultObj.get("duration").getAsInt();
		int start_time = resultObj.get("start_time").getAsInt();
		int match_seq_num = resultObj.get("match_seq_num").getAsInt();
		int tower_status_radiant = resultObj.get("tower_status_radiant")
				.getAsInt();
		int tower_status_dire = resultObj.get("tower_status_dire").getAsInt();
		int barracks_status_radiant = resultObj.get("barracks_status_radiant")
				.getAsInt();
		int barracks_status_dire = resultObj.get("barracks_status_dire")
				.getAsInt();
		int cluster = resultObj.get("cluster").getAsInt();
		int first_blood_time = resultObj.get("first_blood_time").getAsInt();
		int lobby_type = resultObj.get("lobby_type").getAsInt();
		int human_players = resultObj.get("human_players").getAsInt();
		int leagueid = resultObj.get("leagueid").getAsInt();
		int positive_votes = resultObj.get("positive_votes").getAsInt();
		int negative_votes = resultObj.get("negative_votes").getAsInt();
		int game_mode = resultObj.get("game_mode").getAsInt();
		query = "INSERT INTO matches (radiant_win, duration, start_time, match_id, match_seq_num, tower_status_radiant, tower_status_dire, barracks_status_radiant, barracks_status_dire, cluster, first_blood_time, lobby_type, human_players, leagueid, positive_votes, negative_votes, game_mode, player_one, player_two, player_three, player_four, player_five, player_six, player_seven, player_eight, player_nine, player_ten) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			ps = (PreparedStatement) con.prepareStatement(query);
			ps.setBoolean(1, radiant_win);
			ps.setInt(2, duration);
			ps.setInt(3, start_time);
			ps.setInt(4, match_id);
			ps.setInt(5, match_seq_num);
			ps.setInt(6, tower_status_radiant);
			ps.setInt(7, tower_status_dire);
			ps.setInt(8, barracks_status_radiant);
			ps.setInt(9, barracks_status_dire);
			ps.setInt(10, cluster);
			ps.setInt(11, first_blood_time);
			ps.setInt(12, lobby_type);
			ps.setInt(13, human_players);
			ps.setInt(14, leagueid);
			ps.setInt(15, positive_votes);
			ps.setInt(16, negative_votes);
			ps.setInt(17, game_mode);
			ps.setInt(18, playerKeys[0]);
			ps.setInt(19, playerKeys[1]);
			ps.setInt(20, playerKeys[2]);
			ps.setInt(21, playerKeys[3]);
			ps.setInt(22, playerKeys[4]);
			ps.setInt(23, playerKeys[5]);
			ps.setInt(24, playerKeys[6]);
			ps.setInt(25, playerKeys[7]);
			ps.setInt(26, playerKeys[8]);
			ps.setInt(27, playerKeys[9]);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Match statement failed.");
			e.printStackTrace();
		}
	}
	
}
