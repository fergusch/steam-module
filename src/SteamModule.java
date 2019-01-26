/**
 * Class for managing the Steam module's api key.
 * @author @fergusch
 */
public class SteamModule {
	
	/**
	 * The API key for this module to use.
	 */
	private static String apikey;
	
	/**
	 * Set the module's API key.
	 * @param key - Steam API key
	 */
	public static void setAPIKey(String key) {
		apikey = key;
	}
	
	/**
	 * Get the module's API key.
	 * @return api key
	 */
	public static String getAPIKey() {
		return apikey;
	}

}
