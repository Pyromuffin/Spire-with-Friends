package chronoMods.steam;

import chronoMods.*;
import chronoMods.steam.*;
import com.codedisaster.steamworks.*;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;

public class SteamLobby {

	public SteamID steamID;

	public String name = "";
	public String mode = "Versus";
	public String ascension = "0";
	public String character = "The Ironclad";
	public int capacity = 8;

	public SteamLobby (SteamID id) {
		this.steamID = id;

		name = NetworkHelper.matcher.getLobbyData(steamID, "name");
		mode = NetworkHelper.matcher.getLobbyData(steamID, "mode");
		ascension = NetworkHelper.matcher.getLobbyData(steamID, "ascension");
		character = NetworkHelper.matcher.getLobbyData(steamID, "character");

		// capacity = NetworkHelper.matcher.getLobbyMemberLimit(steamID);
	}

	public String getOwnerName() {
		return NetworkHelper.friends.getFriendPersonaName(NetworkHelper.matcher.getLobbyOwner(this.steamID));
	}

	public int getMemberCount() {
		return NetworkHelper.matcher.getNumLobbyMembers(this.steamID);
	}
}