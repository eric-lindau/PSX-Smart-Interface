# Aerowinx PSX Smart Interface
[![Packagist](https://img.shields.io/packagist/l/doctrine/orm.svg)](LICENSE.md)
[![Packagist](https://img.shields.io/badge/version-in%20development-orange.svg)](README.md)

An add-on for Aerowinx PSX that combines inputs from multi-pilot setups that are not mechanically linked.

This add-on allows a user to differentiate hardware components as either "Captain" or "First Officer" controls, combining inputs logically while interfacing with the main PSX server. This allows physical setups without mechanically synchronized pilot and co-pilot controls to operate logically.

## Usage
**Note:** This software is an add-on for [*Aerowinx PSX*](http://www.aerowinx.com/). PSX is required and should be running for this add-on to operate properly.

### Networking
This add-on relies on TCP port **10747** to interface with the PSX server, so the server must be active with port 10747 unrestricted on the network.

### General
Currently, the *SmartInterface* folder contains an Intellij IDEA project, Java source code, and Java binaries for the latest version of the software. There is not currently a *.jar* executable to run the add-on.

## Built with
* [JInput](https://github.com/jinput/jinput) - Used to detect and interface with hardware controllers
* [Aerowinx Network](http://aerowinx.com/assets/networkers/Network%20Documentation.txt) - Used to interface with PSX over network (**documentation only**)
