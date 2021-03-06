# Aerowinx PSX Smart Interface
[![Packagist](https://img.shields.io/badge/version-1.2.6-brightgreen.svg)](SmartInterface)
[![Packagist](https://img.shields.io/packagist/l/doctrine/orm.svg)](LICENSE.md)

<p align="center">
  <img src="https://i.imgur.com/XqFml8h.jpg" height="450" align="center">\
</p>
<p align="center"><i>a simulator using PSX Smart Interface</i></p>

Currently used in the following locations for interfacing hardware with existing flight simulator software:
* Tokyo, Japan
* Chengdu, China
* Abu Dhabi, UAE
* Bahrain
* Frankfurt, Germany
* Berlin, Germany
* Brussels, Belgium
* London, England
* Fort Collins, USA
* Las Vegas, USA
* South America
* Australia

**NOTE:** Given the increased usage of this software, it is being deprecated and rewritten in a more modular and extensible fashion.

Communicates with Aerowinx PSX over a network to provide a variety of necessary features for extensive flight simulator setups:
* It combines analog inputs, such as those of separated flight controls, in multi-pilot setups that are not mechanically linked.
* It allows assignment of hardware components to controls in PSX, such as tillers & brakes, EICAS brightness dials, and weather radar buttons & rotaries, seamlessly integrating with PSX.

... all configurable with a UI that is simple & easy to use, much like that of PSX itself.

For even more information about what this add-on provides, please visit the [Wiki](https://github.com/eric-lindau/PSX-Smart-Interface/wiki).

## Usage
**Note:** This software is an add-on for [*Aerowinx PSX*](http://www.aerowinx.com/). PSX is required and should be running for this add-on to operate properly.

### General
**Run *SmartInterface-1.2.6.jar* by double clicking on it or using Java in a command line.**<br>
A window will appear showing all connected controllers that are not "ignored" in *general.cfg*.<br>
Please see the **Networking** section below if you have connection issues.

### Networking
This add-on relies on the IP address and TCP port specified in the automatically generated ***general.cfg*** file (generated in the same directory as the *.jar* file) to interface with the PSX server, so the server must be unrestricted on the network and configured to run based on the settings in the configuration file. **By default, this configuration specifies a connection to localhost:10747.**

## Built with
* [JInput](https://github.com/jinput/jinput) - Used to detect and interface with hardware controllers
* [Aerowinx Network](http://aerowinx.com/assets/networkers/Network%20Documentation.txt) - Used to interface with PSX over network (**documentation only**)

## Changelog

### 1.0
* Added flight control functionality with combined analog inputs.
* Added UI for easy user assignment of hardware components to components within PSX.
* Added network interface for communication with PSX.

### 1.1
* Drastically improved stability and efficiency.
* Improved error handling and error notifications.

### 1.2
* Added feature that saves user definitions for persistent use and allows for user configuration of more advanced settings.
* Added functionality for many more analog, button, and rotary components in PSX.
* Improved network efficiency and fixed a critical network bug that was causing PSX server to freeze/crash.
* Implemented several bugfixes and improvements.
* Included native libraries with .jar for reduced file clutter.
