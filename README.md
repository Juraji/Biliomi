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

## "Read the docs"
Sadly I haven't had any time yet to set up proper documentation for Biliomi.<br>
While I'm working on that, here are some pointers to give you a headstart:
* Use `/help`, in Biliomi's console, to find out how to use the console.
* The `/exportsystemcommands` console commands creates an export of all system commands and all their properties.<br>
This might be a good way to get a feel of what is possible.<br>
I have done my best to make all commands as self-explanatory as possible.

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
  * README.MD `This read me`
  * LICENSE `License information`
  * database-mysql-ddl.sql `A SQL script for Biliomi's database`
  * Biliomi.ts `A TypeScript interface definition file for Biliomi's REST Api model`

### Installation
Read these instructions carefully, tldr; == No support :D

1. Make sure you have the latest version of [Java 8](https://www.java.com/en/download/) or higher installed.
1. Grab the latest version from [the releases page](https://github.com/Juraji/Biliomi/releases).
1. Open the downloaded archive and unpack the `Biliomi v3` directory to where you want to installation to reside.
1. Open the file explorer and navigate to the location in which you've unpacked the downloaded archive.
1. Copy the directory `default-config` to `config`.
1. Open up `./config/core.yml` in your favorite text editor.<br>
  a. Fill in all the settings as described.<br>
  b. Settings under `integrations:` are optional and may be left to `null` if you do not wish to use the intergrations.
1. Run Biliomi. *Biliomi will initially be set to installation mode.*<br>
  To run Biliomi start (double-click) the appropriate runner for your system:<br>
    On Windows: `start-biliomi.bat`<br>
    On Linux/MacOS: `start-biliomi.sh`
1. Follow any instructions in the console to fully set up Biliomi.
1. On successful setup Biliomi will immediately connect to Twitch and start listening for commands.
1. Wait for Biliomi to finish follower and subscriber updates and exit Biliomi by typing `/exit` in the console.
1. Once again open up `./config/core.yml` in your favorite text editor and change `updateMode` to `OFF`.
1. Now start Biliomi again and enjoy it's features.

*Note: By default Biliomi will be muted to prevent any chatter during setup and initial follower/subscriber updates.*  
*Type `!mute` in the console or chat to enable chatter.*

### Updating
1. Grab the latest version from [the releases page](https://github.com/Juraji/Biliomi/releases).
1. Shut down any currently running instance of Biliomi.
1. Unpack the downloaded archive into the final location, from which you like to run Biliomi, overwriting all files.
1. Compare all config files within `default-config` with the files in the existing `config` directory. Tedious, I know... But one can only do so much.
1. Open up `./config/core.yml` in your favorite text editor.<br>
  a. Update any setting that needs to be changed. Like added features.
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
