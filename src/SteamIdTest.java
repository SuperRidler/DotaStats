import java.text.DecimalFormat;


public class SteamIdTest {


	public static void main(String[] args) {
		System.out.println(new DecimalFormat("###################").format(SteamID.bit32to64(70427419)));
		System.out.println(SteamID.bit64to32(76561198030693147l));
		System.out.println(SteamID.bit64to32(SteamID.bit32to64(70427419)));
		System.out.println(new DecimalFormat("###################").format(SteamID.bit32to64(45413440)));
		System.out.println(SteamID.bit64to32(76561198005679168l));
		System.out.println(SteamID.bit64to32(SteamID.bit32to64(45413440)));
		SteamID.updateSteamUsers();
	}

}
