# Biliomi v3.11.0-SNAPSHOT
Date: -

## Release Notes
* Fix bug in updater where same versions would trigger an update proposal.

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