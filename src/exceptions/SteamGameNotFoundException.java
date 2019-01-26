package modules.steam.exceptions;

/**
 * Exception thrown if SteamGame() could not find a game for the given query.
 * @author @fergusch
 *
 */
public class SteamGameNotFoundException extends Exception {

	private static final long serialVersionUID = 876361694011503470L;
	
	public SteamGameNotFoundException(String s) {
		super(s);
	}

}
