package chronoMods.network.steam;

import chronoMods.*;
import chronoMods.network.Lobby;
import chronoMods.network.NetworkHelper;
import chronoMods.network.steam.*;
import chronoMods.ui.hud.*;
import com.codedisaster.steamworks.*;

import java.util.*;
import java.util.concurrent.*;

public class SteamLobby extends Lobby {

	public SteamID steamID;

	public String name = "";
	public String owner = "MegaCrit";
	public String mode = "Versus";
	public String ascension = "0";
	public String character = "IRONCLAD";

	public boolean heart;
	public boolean neow;
	public boolean ironman;

	public int capacity = 6;
	public int members = 0;

	public ArrayList<String> memberNames = new ArrayList();

	public SteamID ownerID;

    public static CopyOnWriteArrayList<RemotePlayer> players = new CopyOnWriteArrayList();

    // This constructor is for testing only
	public SteamLobby () {
		String[] owners = {"Chrono", "Snakebird", "Puffin", "Baalor", "Rocket", "Skyla", "Mezzo", "Zyzzy"};
		owner = owners[(int)(Math.random()*owners.length)];

		ascension = "" + (int)(Math.random()*20);

		String[] chars = {"IRONCLAD", "SILENT", "DEFECT", "WATCHER"};
		character = chars[(int)(Math.random()*chars.length)];

		for (int i = 0; i < (int)(Math.random()*8); i++) {
			memberNames.add(owners[(int)(Math.random()*owners.length)]);		
		}

        Random rd = new Random();

		heart = rd.nextBoolean();
		neow = rd.nextBoolean();
		ironman = rd.nextBoolean();
	}

	public SteamLobby (SteamID id) {
		this.steamID = id;
		TogetherManager.log("New Lobby with ID: " + id);

		try {
			name = NetworkHelper.matcher.getLobbyData(steamID, "name");
			TogetherManager.log("Lobby name: " + name);
			
			mode = NetworkHelper.matcher.getLobbyData(steamID, "mode");
			TogetherManager.log("Lobby mode: " + mode);
			
			ascension = NetworkHelper.matcher.getLobbyData(steamID, "ascension");
			TogetherManager.log("Lobby ascension: " + ascension);
			
			character = NetworkHelper.matcher.getLobbyData(steamID, "character");
			TogetherManager.log("Lobby character: " + character);

			heart = Boolean.valueOf(NetworkHelper.matcher.getLobbyData(steamID, "heart"));
			TogetherManager.log("Lobby heart: " + heart);

			neow = Boolean.valueOf(NetworkHelper.matcher.getLobbyData(steamID, "neow"));
			TogetherManager.log("Lobby neow: " + neow);

			ironman = Boolean.valueOf(NetworkHelper.matcher.getLobbyData(steamID, "ironman"));
			TogetherManager.log("Lobby ironman: " + ironman);
			
			ownerID = NetworkHelper.matcher.getLobbyOwner(this.steamID);
			TogetherManager.log("Lobby ownerID: " + ownerID);
			
			owner = NetworkHelper.matcher.getLobbyData(steamID, "owner");
			//TogetherManager.log("Lobby owner name: " + getOwnerName());
			
			memberNames = new ArrayList<String>(Arrays.asList(NetworkHelper.matcher.getLobbyData(steamID, "members").split("\t")));
			TogetherManager.log("Lobby members: " + NetworkHelper.matcher.getLobbyData(steamID, "members"));

			TogetherManager.log("Lobby member count: " + getMemberCount());
			
			// capacity = NetworkHelper.matcher.getLobbyMemberLimit(steamID);
		} catch (Exception e) {}
	}

	@Override
	public String getOwnerName() {
		try {
			this.owner = NetworkHelper.friends.getFriendPersonaName(NetworkHelper.matcher.getLobbyOwner(this.steamID));
		} catch (Exception e) {}

		return this.owner;
	}

	@Override
	public boolean isOwner() {
		return TogetherManager.currentUser.isUser(this.ownerID);
	}

	@Override
	public void updateOwner() {
		ownerID = NetworkHelper.matcher.getLobbyOwner(this.steamID);
	}

	@Override
	public int getMemberCount() {
		return this.memberNames.size();
	}

	@Override
	public CopyOnWriteArrayList<RemotePlayer> getLobbyMembers() {
		int memberCount = 1;
		try {
			memberCount = NetworkHelper.matcher.getNumLobbyMembers(this.steamID);
			TogetherManager.log("get Members in lobby: " + memberCount);
		} catch (Exception e) {}
		players.clear();
		TopPanelPlayerPanels.playerWidgets.clear();

		try {
			for (int i = 0; i < memberCount; i++) {
				RemotePlayer newPlayer = new RemotePlayer(NetworkHelper.matcher.getLobbyMemberByIndex(steamID, i));
				players.add(newPlayer);
        		TopPanelPlayerPanels.playerWidgets.add(new RemotePlayerWidget(newPlayer));
				TogetherManager.log("get Members created: " + newPlayer.userName);
			}
		} catch (Exception e) {}

		return players;
	}

	@Override
	public String getMemberNameList() {
		StringBuilder out = new StringBuilder();
		for (RemotePlayer o : players)
		{
		  out.append(o.userName);
		  out.append("\t");
		}
		return out.toString().trim();
	}

	@Override
	public int getCapacity() {
	  //TODO
		return 0;
	}

	@Override
	public String getMetadata(String key) {
		return NetworkHelper.matcher.getLobbyData(steamID, key);
	}

	@Override
	public void setMetadata(Map.Entry<String, String>... pairs) {
		for (Map.Entry<String, String> pair : pairs) {
			NetworkHelper.matcher.setLobbyData(steamID, pair.getKey(), pair.getValue());
		}
	}

}