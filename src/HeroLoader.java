import java.sql.SQLException;
import java.util.Iterator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;


public class HeroLoader {

	private static final String heroAddress = "https://raw.github.com/kronusme/dota2-api/master/data/heroes.json";
	private static Connection con;
	
	public static void main(String[] args) {
		con = DBUtil.connectToDB();
		loadHeroes();
		DBUtil.closeDB(con);
		System.out.println("Done");
	}

	public static void loadHeroes() {
		JsonObject heroJson = Request.getJsonRequest(heroAddress);
		JsonArray heroes = heroJson.getAsJsonArray("heroes");
		Iterator<JsonElement> it = heroes.iterator();
		while (it.hasNext()) {
			JsonObject hero = it.next().getAsJsonObject();
			int id = hero.get("id").getAsInt();
			String name = hero.get("localized_name").getAsString();
			System.out.println(id + ": " + name);
			String query = "INSERT INTO hero (id, name) VALUES (?, ?)";
			try {
				PreparedStatement ps = (PreparedStatement) con.prepareStatement(query);
				ps.setInt(1, id);
				ps.setString(2,  name);
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}
