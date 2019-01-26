import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import modules.steam.exceptions.SteamUserNotFoundException;

/**
 * An object representing a Steam user.
 * @author @fergusch
 */
public class SteamUser {
	
	private String steamID64;
	private String customURL;
	private boolean isPrivate;
	private String creationDate;
	private String name;
	private String realName;
	private String location;
	private String profileURL;
	private String avatarURL;
	
	private int level;
	
	private int friends;
	private Integer games;
	
	/**
	 * Constructs a Steam user object.
	 * @param query - profile ID or profile URL
	 * @throws SteamUserNotFoundException 
	 */
	public SteamUser(String query) throws SteamUserNotFoundException {
		
		// get Steam Web API Key
		String apiKey = SteamModule.getAPIKey();
		
		// set User-Agent string
		System.setProperty("http.agent", "Mozilla/5.0");
		
		// get steamid.io search page
		String searchURL = "https://steamid.io/lookup/" + query;
		
		Document doc = null;
		try {
			
			// search with given query
			doc = Jsoup.connect(searchURL)
			    .data("input", query)
			    .userAgent("Mozilla/5.0")
			    .post();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// check if the profile was not found
		if (doc.select("dd.value").first() == null) {
			
			// throw custom exception
			throw new SteamUserNotFoundException("Invalid profile URL or Steam ID given");
			
		}
		
		// grab 64-bit steam ID from search results
		this.steamID64 = doc.select("dd.value").get(2).text();
		
		String profileURL = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" 
				+ apiKey + "&steamids=" + this.steamID64;
		
		// connect to steam API
		URL purl = null;
		JSONTokener ptokener = null;
		JSONObject profile = null;
		try {
			
			// get Steam API json data
			purl = new URL(profileURL);
			ptokener = new JSONTokener(purl.openStream());
			profile = new JSONObject(ptokener).getJSONObject("response").getJSONArray("players").getJSONObject(0);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// get custom profile URL
		this.profileURL = profile.getString("profileurl");
		
		// get profile name
		this.name = (profile.has("personaname")) ? profile.getString("personaname") : null;
		
		// check if profile is private
		this.isPrivate = (profile.getInt("communityvisibilitystate") == 2);
		
		// get avatar
		this.avatarURL = profile.getString("avatarfull");
		
		// get account creation date
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
		this.creationDate = (profile.has("timecreated")) ? sdf.format(new Date(profile.getLong("timecreated") * 1000L)) : null;
		
		// get real name, set to null if not set
		this.realName = (profile.has("realname")) ? profile.getString("realname") : null;
		
		// get location
		this.location = (profile.has("loccountrycode")) ? profile.getString("loccountrycode") : null;
		
		// only do the rest if the profile is public
		if (!this.isPrivate) {
			
			String levelURL = "http://api.steampowered.com/IPlayerService/GetSteamLevel/v1/?key=" 
								+ apiKey + "&steamid=" + this.steamID64;
			
			URL lurl = null;
			JSONTokener ltokener = null;
			try {
				
				// get Steam API json data
				lurl = new URL(levelURL);
				ltokener = new JSONTokener(lurl.openStream());
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// get player level
			this.level = new JSONObject(ltokener).getJSONObject("response").getInt("player_level");
			
			// build URL to get friends object
			String friendsURL = "http://api.steampowered.com/ISteamUser/GetFriendList/v0001/?key=" 
								+ apiKey + "&steamid=" + this.steamID64 + "&relationship=friend";
			
			// connect to steam API
			URL furl = null;
			JSONTokener ftokener = null;
			try {
				
				// get Steam API json data
				furl = new URL(friendsURL);
				ftokener = new JSONTokener(furl.openStream());
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			// set number of friends
			this.friends = new JSONObject(ftokener).getJSONObject("friendslist").getJSONArray("friends").length();
			
			// build URL to get games object
			String gamesURL = "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=" 
					+ apiKey + "&steamid=" + this.steamID64;
			
			// connect to steam API
			URL gurl = null;
			JSONTokener gtokener = null;
			try {
				
				// get Steam API json data
				gurl = new URL(gamesURL);
				gtokener = new JSONTokener(gurl.openStream());
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			JSONObject gamesObj = new JSONObject(gtokener).getJSONObject("response");
			
			// set number of owned games
			this.games = (gamesObj.has("game_count")) ? gamesObj.getInt("game_count") : null;
			
		} else {
			
			this.level = 0;
			this.friends = 0;
			this.games = 0;
			
		}
		
	}
	
	/**
	 * Returns the 64-bit Steam ID of the current user.
	 * @return steamID64
	 */
	public String getSteamID() {
		return this.steamID64;
	}
	
	/**
	 * Returns the custom URL of the current user or {@code null}.
	 * @return
	 */
	public String getCustomURL() {
		return this.customURL;
	}
	
	/**
	 * Returns {@code true} if the current user's profile
	 * is private, or {@code false} otherwise.
	 * @return boolean
	 */
	public boolean isPrivate() {
		return this.isPrivate;
	}
	
	/**
	 * Returns the current user's account creation date
	 * in MMMM dd, yyyy format.
	 * @return account creation date
	 */
	public String getCreationDate() {
		return this.creationDate;
	}
	
	/**
	 * Returns the current user's screen name.
	 * @return String containing the user's name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the "real name" on the user's profile,
	 * or {@code null} if it is not set.
	 * @return real name or {@code null}
	 */
	public String getRealName() {
		return this.realName;
	}
	
	/**
	 * Returns the location on the user's profile.
	 * @return location
	 */
	public String getLocation() {
		return this.location;
	}
	
	/**
	 * Returns the URL of the user's profile.
	 * @return profile URL
	 */
	public String getProfileURL() {
		return this.profileURL;
	}
	
	/**
	 * Returns the user's level.
	 * @return - level (int)
	 */
	public int getLevel() {
		return this.level;
	}
	
	/**
	 * Returns the user's avatar URL.
	 * @return - avatar URL
	 */
	public String getAvatarURL() {
		return this.avatarURL;
	}
	
	/**
	 * Returns the number of friends the user has.
	 * @return - number of friends
	 */
	public int getNumberOfFriends() {
		return this.friends;
	}
	
	/**
	 * Returns the number of games owned by the user.
	 * @return - number of games
	 */
	public Integer getNumberOfOwnedGames() {
		return this.games;
	}

}
