import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;


public class SteamID {

	private static String address = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=";
	private static String steamIds = "steamids=";
	private static final BigInteger magicNumber = BigInteger.valueOf(76561197960265728l);
	
	/* Convert 32 bit id to a 54 bit id. */
	public static long bit32to64(int id) {
		return magicNumber.add(BigInteger.valueOf(id)).longValue();
	}
	
	/* Convert 64 bit id to a 32 bit id. */
	public static int bit64to32(long d) {
		return (BigInteger.valueOf(d).subtract(magicNumber)).intValue();
	}

	
	public static void updateSteamUsers() {
		String key = Key.loadKey();
		address += key;
		address += steamIds;
		Connection con = DBUtil.connectToDB();
		String query = "SELECT DISTINCT account_id FROM player WHERE account_id != -1 AND account_id != 0 AND account_id NOT IN (SELECT steamid32 FROM user)";
		try {
			Statement stmt = (Statement) con.prepareStatement(query);
			ResultSet rs = stmt.executeQuery(query);
			/* Get the amount of rows. */
			rs.last();
			int size = rs.getRow();
			rs.first();
			/* Set up the array. */
			int[] ids = new int[size];
			/* Add account_ids to array. */
			while (rs.next()) {
				ids[rs.getRow()-1] = rs.getInt("account_id");
			}
			rs.close();
			for (int i = 0; i < ids.length; i++) {
				String steamid64 = String.valueOf(bit32to64(ids[i]));
				String url = address + steamid64 + "&";
				JsonObject json = Request.getJsonRequest(url);
				JsonObject response = json.getAsJsonObject("response");
				JsonArray players = response.getAsJsonArray("players");
				if (players.size() > 0) {
					JsonObject player = players.get(0).getAsJsonObject();
					if (player.has("personaname")) {
						String name = player.get("personaname").getAsString();
						String insert = "INSERT INTO user (steamid64, steamid32, name) VALUES (?, ?, ?)";
						PreparedStatement pstmt = (PreparedStatement) con.prepareStatement(insert);
						pstmt.setBigDecimal(1, BigDecimal.valueOf(bit32to64(ids[i])));
						pstmt.setInt(2, ids[i]);
						pstmt.setString(3, name);
						pstmt.execute();
						pstmt.close();
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("Problem with updating steam users.");
			e.printStackTrace();
			System.exit(1);
		}
		DBUtil.closeDB(con);
	}
}
