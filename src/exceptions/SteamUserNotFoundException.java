package modules.steam.exceptions;

/**
 * Exception thrown if SteamUser() could not find a user for the given query.
 * @author @fergusch
 */
public class SteamUserNotFoundException extends Exception {
	
	private static final long serialVersionUID = -5330176105216380388L;

	public SteamUserNotFoundException(String s) {
		super(s);
	}
	
}