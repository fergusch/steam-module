import java.io.IOException;
import java.net.URL;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import modules.steam.exceptions.SteamGameNotFoundException;

/**
 * An object representing a Steam app.
 * @author @fergusch
 */
public class SteamGame {

	private String appid;
	private String name;
	private String desc;
	private String headerURL;
	private String dev;
	private String publisher;
	private double price;
	private double discountedPrice;
	private int discount;
	private String currency;
	
	/**
	 * Constructs a Steam game object.
	 * @param query - query OR app ID in the form: "appid:xxxxx"
	 * @throws SteamGameNotFoundException
	 */
	public SteamGame(String query) throws SteamGameNotFoundException {
		
		@SuppressWarnings("unused")
		URL storeURL = null;
		JSONTokener tokener = null;
		JSONObject game = null;
		
		// check if "app" parameter was used
		if (query.startsWith("appid:")) {
			
			// set app id given
			this.appid = query.substring(6);
			
			try {
				
				// try to get store API data using given app id
				storeURL = new URL("https://store.steampowered.com/api/appdetails?appids=" + this.appid);
				tokener = new JSONTokener(storeURL.openStream());
				game = new JSONObject(tokener);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// check if game doesn't exist
			if (!game.getJSONObject(this.appid).getBoolean("success")) {
				
				// throw custom exception
				throw new SteamGameNotFoundException("Invalid appid given");
				
			} else { // if game does exist, extract the "data" object instead
				
				game = game.getJSONObject(this.appid).getJSONObject("data");
				
			}
			
		} else { // get app id via steam store search
			
			String searchURL = "https://store.steampowered.com/search/";
			
			Document doc = null;
			try {
				
				// grab steam search page
				doc = Jsoup.connect(searchURL)
						.data("term", query)
						.userAgent("Mozilla/5.0")
						.get();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Elements results = doc.select("a.search_result_row");
			
			// check if there were not results
			if (results.first() == null) {
				
				// throw custom exception
				throw new SteamGameNotFoundException("No search results found for query");
				
			}
			
			// set app id from search row tag, filter apps only
			this.appid = results.select("[data-ds-appid]").first().attr("data-ds-appid");
			
			try {
				
				// get store API data using given app id
				storeURL = new URL("https://store.steampowered.com/api/appdetails?appids=" + this.appid);
				tokener = new JSONTokener(storeURL.openStream());
				game = new JSONObject(tokener).getJSONObject(this.appid).getJSONObject("data");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		// set game name
		this.name = game.getString("name");
		
		// set game description (short description)
		// parse using Jsoup since this might contain HTML tags
		this.desc = Jsoup.parse(game.getString("short_description")).text();
		
		// set image URL for header image
		this.headerURL = game.getString("header_image");
		
		// set developer
		this.dev = game.getJSONArray("developers").getString(0);
		
		// set publisher
		this.publisher = game.getJSONArray("publishers").getString(0);
		
		// get pricing information object
		JSONObject priceData = game.getJSONObject("price_overview");
		
		// *** prices are converted to ints by multiplying by 100 in the api
		// *** multiplying by 0.01 converts them back
		// *** Math.round call is to eliminate division errors (like +0.000000000002)
		
		// set retail price
		this.price = Math.round((priceData.getInt("initial") * 0.01)*100.0)/100.0;
		
		// set price after discount
		this.discountedPrice = Math.round((priceData.getInt("final") * 0.01)*100.0)/100.0;
		
		// set discount percentage
		this.discount = priceData.getInt("discount_percent");
		
		// set currency
		this.currency = priceData.getString("currency");
		
	}
	
	/**
	 * Gets the Steam app ID of the game this object represents.
	 * @return Steam ID as string
	 */
	public String getAppID() {
		return this.appid;
	}
	
	/**
	 * Gets the name of the game this object represents.
	 * @return Game name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Gets the description of the game this object represents.
	 * @return Game short description
	 */
	public String getDesc() {
		return this.desc;
	}
	
	/**
	 * Gets the header image URL of the game this object represents.
	 * @return Header image URL as a string
	 */
	public String getHeaderImageURL() {
		return this.headerURL;
	}
	
	/**
	 * Gets the developer name of the game this object represents.
	 * @return Developer name as string
	 */
	public String getDeveloperName() {
		return this.dev;
	}
	
	/**
	 * Gets the publisher name of the game this object represents.
	 * @return Publisher name as string
	 */
	public String getPublisherName() {
		return this.publisher;
	}
	
	/**
	 * Gets the regular retail price of the game this object represents.
	 * @return Retail price as double
	 */
	public double getRetailPrice() {
		return this.price;
	}
	
	/**
	 * Gets the sale price of the game this object represents.
	 * If no discount is applied, this will be equivalent to the retail price.
	 * @return Sale price as double
	 */
	public double getDiscountedPrice() {
		return this.discountedPrice;
	}
	
	/**
	 * Gets the discount percentage of the game this object represents.
	 * @return discount percentage as int
	 */
	public int getDiscountPercentage() {
		return this.discount;
	}
	
	/**
	 * Gets the currency denomination (USD/GBP/EUR/etc) of the game this object represents.
	 * @return Currency type as string (USD/GBP/EUR/etc)
	 */
	public String getCurrencyType() {
		return this.currency;
	}
	
}
