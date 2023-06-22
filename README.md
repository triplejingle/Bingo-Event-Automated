# Bingo Event Plugin
A plugin to play an automated bingo for the clan.
Build specificly for the [BE Active NL] clanchat.

## Features
- Sends loot to the configurable server. 
- Send screenshot to Discord; only when an item has been crossed off.
- Dynamic configuration. 
- The configuration can be send from the server to the client.
- Partial anti cheat.

## Restriction
Only supports:
-Npcs which drop loot on the ground. Goblin, Graardor for example.
-Loot from Barrows, COX, TOB, TOA, Fishing Trawler, Wilderniss Loot Chest

## Setup for users
To use the plugin the user has to configure it correctly.
Send data to: The url to send the data to. 
Dynamic configuration url: The url to get the configuration.
Eventcode: the code of the event.
Send screenshot: Is used to enable sending screenshots to the webserver.
Discord webhook: The url to send the screenshots to.

## Setup for developers
Your own deployed backend, database and frontend to display the data.
Configuration url:
U can enter the url here to send the configuration.
The configuration consists of itemsources eg:
["COX","TOA", "Goblin"]

Send data to:
This is the url to send the data to.
It consists of an url which uses a post request with body:
{
username:string,
eventcode: string,
itemsource: string,
items: string[]
}

The result sending back should be:
{
  isMessageSet: bool,
  message:string
}
When there's a message available the message will be used to send a screenshot to Discord.

# Future
- Add caching so it won't unneccesarely send data to the server.
- Add different loot sources: hespory, cluescrolls etc
- Check for itemstacks. To prevent people stacking cluescrolls or other items beforehand.
