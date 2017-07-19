# Aerowinx PSX Smart Interface
[![Packagist](https://img.shields.io/badge/version-1.2.3-brightgreen.svg)](SmartInterface)
[![Packagist](https://img.shields.io/badge/build-passing-brightgreen.svg)](https://github.com/eric-lindau/PSX-Smart-Interface)
[![Packagist](https://img.shields.io/packagist/l/doctrine/orm.svg)](LICENSE.md)

This add-on for Aerowinx PSX provides a variety of features that are necessary for those that have extensive flight simulator hardware:
* It provides a way to combine analog inputs, such as those of flight controls, in multi-pilot setups that are not mechanically linked.
* It allows a user to differentiate hardware components as either "Captain" or "First Officer" controls, creating a clear separation between controls while integrating their inputs.
* It allows assignment of certain components to controls such as tilt & gain dials, brightness dials, and weather radar buttons, seamlessly integrating with PSX.

... all configurable with a UI that is easy to use, much like that of PSX itself.

For even more information about what this add-on provides, please visit the [Wiki](wiki).

## Usage
**Note:** This software is an add-on for [*Aerowinx PSX*](http://www.aerowinx.com/). PSX is required and should be running for this add-on to operate properly.

### General
**Extract the *SmartInterface* folder from the .zip archive. Inside that folder, run *SmartInterface.jar* by double-clicking on it.**

**Note:** The *SmartInterface.jar* file must be in the same directory as the included *.dll* files (natives) due to library dependencies. Alternatively, if the *.dll* files are moved to a directory that is part of the Java PATH, *SmartInterface.jar* can run independently.

### Networking
This add-on relies on the IP address and TCP port specified in the automatically generated **general.cfg** to interface with the PSX server, so the server must be unrestricted on the network and configured to run based on the configuration file. **By default, this configuration specifies a connection to > localhost:10747.**

## Built with
* [JInput](https://github.com/jinput/jinput) - Used to detect and interface with hardware controllers
* [Aerowinx Network](http://aerowinx.com/assets/networkers/Network%20Documentation.txt) - Used to interface with PSX over network (**documentation only**)

## Changelog

### 1.0
* Added flight control functionality with combined analog inputs.
* Added UI for easy user assignment of hardware components to components within PSX.
* Added network interface for add-on to communicate with PSX.

### 1.1
* Improved stability and efficiency of add-on with CPU threading and fewer ticks per second.
* Improved error catching and error notifications for easier problem solving.

### 1.2
* Added feature that saves user definitions for persistent use as well as allows for user configuration of advanced settings.
* Added functionality with many more analog, button, and rotary components.
* Improved network efficiency and fixed a critical network bug that was causing PSX server to freeze/crash.
* Fixed a variety of bugs and issues
