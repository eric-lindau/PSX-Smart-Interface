# Aerowinx PSX Smart Interface
[![Packagist](https://img.shields.io/badge/version-1.1-brightgreen.svg)](SmartInterface)
[![Packagist](https://img.shields.io/badge/build-passing-brightgreen.svg)](https://github.com/eric-lindau/PSX-Smart-Interface)
[![Packagist](https://img.shields.io/packagist/l/doctrine/orm.svg)](LICENSE.md)

An add-on for Aerowinx PSX that combines inputs from multi-pilot setups that are not mechanically linked.

This add-on allows a user to differentiate hardware components as either "Captain" or "First Officer" controls, combining inputs logically while interfacing with the main PSX server. This allows physical setups without mechanically synchronized pilot and co-pilot controls to operate logically.

## Usage
**Note:** This software is an add-on for [*Aerowinx PSX*](http://www.aerowinx.com/). PSX is required and should be running for this add-on to operate properly.

### General
**Extract the *SmartInterface* folder from the .zip archive. Inside that folder, run *SmartInterface.jar* by double-clicking on it.**

**Note:** The *SmartInterface.jar* file must be in the same directory as the included *.dll* files (natives) due to library dependencies. Alternatively, if the *.dll* files are moved to a directory that is part of the Java PATH, *SmartInterface.jar* can run independently.

### Networking
This add-on relies on TCP port **10747** to interface with the PSX server, so the server must be active with port 10747 unrestricted on the network.

**Note:** In Version 1.0 this add-on must be running on the same machine as the server because it uses *localhost* as an address - this will be changed in future versions.

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

### 1.2 (not yet released)
* Added feature that saves user definitions for persistent use.
* Added functionality with many more analog, button, and rotary components.
* Improved network efficiency and fixed a critical network bug that was causing PSX server to freeze/crash.
