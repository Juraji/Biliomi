# Biliomi v3.11.0
Date: -

## Release Notes
* Implemented a full installer.<br>
Installing Biliomi is real easy now, see README.md for more information.
* Fixed bug in time quantity formatting where 0 time would result in weird messages.
* Moved all bits cheering notices to live editable templates.
* The Donation register now supports two live editable templates.<br>
*One for manual donations (using `!donation add ...`) and one for automatic donations (like Stream Labs donations).*<br>
*Check [the wiki](https://github.com/Juraji/Biliomi/wiki/Donations) for more information on donation register templates*
* The Bits component now supports live editable templates.<br>
*One template to post in the chat when a user cheers bits, one for when points are payed out to the cheerer and one for when points are payed to to all viewers.*<br>
*Check [the wiki](https://github.com/Juraji/Biliomi/wiki/Bits) for more information on donation register templates*
* Implement a draft for the Patreon api.<br>
*Due to a code merging error this sneaked into the main branch. What the Patreon integration will actually do is yet to be designed.*

# Biliomi v3.10.1
Date: October 8th 2017

## Release Notes
* Fix bug in updater where same versions would trigger an update proposal.
* CONFIG CHANGE: Add option in `core.yml` to disable checking for updates.
* Fix bug in Steam game sync that would lead to NullPointerExceptions

# Biliomi v3.10.0
Date: October 8th 2017

## Release Notes
* Implement semi-automated updater.<br>
Runs on boot and checks Github for new releases,
offering to automatically install if a new version has been found.
* Fix HostWatch throwing error and not recording hosts.
* Host notices will now always appear, even when the reward is set to 0.<br>
You might want to change this or change the template. Both settings can be changed using !hostwatch.
* Get rid of logger warning at shutdown.
* Rename "!steam autogameupdate" command to "!steam autogamesync".
* Add "!steam syncnow" command, when called immediately syncs the current Steam game to Twitch.
Regardless if the stream is online or offline

# Biliomi v3.9.1
Date: October 6th 2017

## Release Notes:
* Add default-config to the download archives.
* Implement Steam game sync: Have Biliomi update the game set on your channel to match the game you are playing on Steam.

# Biliomi v3.9.0
Date: October 4th 2017

## Release Notes
* Initial commit to Github of existing sources