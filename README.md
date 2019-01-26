# steam-module

A lightweight Java wrapper for the [unofficial Steam Storefront API](https://wiki.teamfortress.com/wiki/User:RJackson/StorefrontAPI) and the [Steam Web API](https://steamcommunity.com/dev). 

## Getting started
To access the Web API for searching user data, you'll need to obtain an official API key, which you can find [instructions for here](https://steamcommunity.com/dev). If you only need to access storefront data, you can skip this step.

Once you have your Web API key, give it to the module:
```Java
SteamModule.setAPIKey("YOUR KEY HERE");
```

To search for games on the store, create a `SteamGame` object with a search query:
```Java
SteamGame game = new SteamGame("garry's mod");
```
Or you can search via the app ID:
```Java
SteamGame game = new SteamGame("appid:4000");
```
If the game cannot be found, a `SteamGameNotFoundException` will be thrown.

To search for a user profile, you can use their numeric Steam ID or custom URL. These are usually found at the end of the user's profile URL:
- Numeric ID: `https://steamcommunity.com/profiles/<id>`
- Custom URL: `https://steamcommunity.com/id/<id>`

Once you know the user's ID, create a `SteamUser` object:
```Java
SteamUser user = new SteamUser("ID HERE");
```

If the ID is invalid, a `SteamUserNotFoundException` will be thrown.

## Available methods
### Games
- `getAppID()`
- `getCurrencyType()`
- `getDesc()`
- `getDeveloperName()`
- `getDiscountedPrice()`
- `getDiscountPercentage()`
- `getHeaderImageURL()`
- `getName()`
- `getPublisherName()`
- `getRetailPrice()`

### Users
- `getAvatarURL()`
- `getCreationDate()`
- `getCustomURL()`
- `getLevel()`
- `getLocation()`
- `getName()`
- `getNumberOfFriends()`
- `getNumberOfOwnedGames()`
- `getProfileURL()`
- `getRealName()`
- `getSteamID()`
- `isPrivate()`

## Todo
- [ ] Add access to more Storefront API functions