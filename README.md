# Biliomi v3
### Another fully featured Twitch chat bot

## About
I tried many generic and Twitch bots out there, but none of them fully satisfied my needs.
And since I am a programmer with needs I decided to build my own.

**Features**
* Viewer loyalty:
  * Customizable Points system
  * View time tracking
  * Follower/Subscriber statistics
  * Bits to points
* RESTful Api
* Customizable text-based commands
* Spotify integration
* Twitter integration
* Steam integration
* Stream Labs integration
* Raid register (manual)
* Donation register (manual or powered by Stream Labs)
* Automated and customizable viewer greetings
* Customizable Chat moderator
* Basic raffle system
* Chat quotes register
* Chat games
  * Adventures *Also known as Heists*
  * The all-knowing Eightball
  * Investments *Yeah that's new*
  * Creative ways to die (!kill)
  * Russian Roulette
  * Slot Machine
  * Tamagotchis

Pull requests are always welcome, tho I must tell you I'm very picky.<br>
Sidenote on pull requests: Your IDE might support editor config, or maybe there's a plugin.
Use it, it's half of your work done for you

## Release schedule
* Minor versions, mostly including new features:<br>
Every week on sunday if changes have been made.
* Micro versions, mostly bugfixes and security fixes:<br>
As soon as they are available and tested.

## "Read the docs"
Biliomi will sometimes refer to "the docs". Find them [here](https://github.com/Juraji/Biliomi/wiki)!

A WADL for Biliomi's REST Api is available under `http://localhost:30000/application.wadl` when Biliomi is running.

## Setting Up
### Download Contents
* ./
  * Biliomi v3/ `Installation root`
    * lib `Libraries used by Biliomi`
    * l10n `Language files, feel free to edit`
    * biliomi-x.x.x.jar `The main Java executable`
    * start-biliomi.sh `Start script for linux/MacOS`
    * start-biliomi.bat `Start script for Windows`
  * RELEASES.MD `A list of all previous releases and release notes`
  * README.MD `This read me`
  * LICENSE `License information`
  * database-mysql-ddl.sql `A SQL script for Biliomi's database`
  * Biliomi.ts `A TypeScript interface definition file for Biliomi's REST Api model`

### Installation
Read these instructions carefully, tldr; == No support :D

Prerequisites:
* Your Twitch channel.
* A Twitch account for the bot.<br>
Since Biliomi will have to be able to chat and moderate in your chat it needs an account the be able to do so.

Installation (Using the built in installer)

1. Make sure you have the latest version of [Java 8](https://www.java.com/en/download/) or higher installed.
1. Grab the latest version from [the releases page](https://github.com/Juraji/Biliomi/releases).
1. Open the downloaded archive and unpack the `Biliomi v3` directory to where you want to installation to reside.
1. Open the file explorer and navigate to the location in which you've unpacked the downloaded archive.
1. Run Biliomi. *Biliomi will initially be set to installation mode.*<br>
  To run Biliomi start (double-click) the appropriate runner for your system:<br>
    On Windows: `start-biliomi.bat`<br>
    On Linux/MacOS: `start-biliomi.sh` 
1. Follow any instructions in the console to fully set up Biliomi.
1. When the first time installation was successful Biliomi will have saved your settings and automatically shut down.
1. Restart Biliomi
1. Biliomi will now setup the database and ask you a few more questions to complete the installation.<br>
On successful setup Biliomi will immediately connect to Twitch and start listening for commands.
1. Wait for Biliomi to finish follower and subscriber updates and exit Biliomi by typing `/exit` in the console.
1. Once again open up `./config/core.yml` in your favorite text editor and change `updateMode` to `OFF`.
1. Now start Biliomi again and enjoy it's features.

*Note: By default Biliomi will be muted to prevent any chatter during setup and initial follower/subscriber updates.*  
*Type `!mute` in the console or chat to enable chatter.*

### Updating
Biliomi now has an automated update. The update takes care of step 1 to 3 for you.<br>
*Note: The automatic updater will overwrite your l10n files. If you've made any changes to them they will be lost.*
*You might want to make a backup of the `./l10n` directory*

1. Grab the latest version from [the releases page](https://github.com/Juraji/Biliomi/releases).
1. Shut down any currently running instance of Biliomi.
1. Unpack the downloaded archive into the final location, from which you like to run Biliomi, overwriting all files.
1. Compare all config files within `default-config` with the files in the existing `config` directory.<br>
Tedious, I know... But one can only do so much. The release notes will always contain a note about changed settings.
1. Open up `./config/core.yml` in your favorite text editor.<br>
  a. Update any setting that needs to be changed. Like added features.<br>
  b. Set `updateMode` to `UPDATE`.
1. Start Biliomi again.
1. Keep an eye out, Biliomi might have some questions for you.
1. Wait for Biliomi to fully start
1. Shut down Biliomi
1. Once again open up `./config/core.yml` in your favorite text editor and change `updateMode` to `OFF`.
1. Now start Biliomi again and enjoy it's features.

### Localization
Localization files are available [here](https://github.com/Juraji/Biliomi-L10N).

1. Download the localization pack.
1. Shut down any currently running instance of Biliomi.
1. Replace the `l10n` directory, with the installation root, with the preferred `l10n` directory from the language pack.
1. Now start Biliomi again and enjoy.

## Usage
### Posting commands
Biliomi is a command driven bot. This means that every and all interaction is done via posting commands in the chat or the console.

Commands always start with an exclamation mark (!).

When a command requires arguments you can find out which ones and how by simply posting the base command.<br>
For instance: When you post `!announcement` in the chat, Biliomi will reply with `Usage: !announcement [add|remove|interval|minmsgs] [more...]`.

### The Console
When Biliomi is done booting it will start listening for input in the console.

> known Bugs:
> * Any input always requires [enter] to be pressed.
> * Biliomi's logging can interrupt users typing a command, requiring users to reenter the command.

To find out what commands the console supports, simply type `/help` into the console and press [enter].

Commandline usage:
* Use console: `/[command] [any required parameters]`
* Post commands: `![command] [any required parameters]`
* Send whispers (using bot account): `@[target username] [message...]`
* post message in chat (using bot account): `>[message...]`
