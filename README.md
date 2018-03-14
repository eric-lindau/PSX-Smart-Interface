# Aerowinx PSX Smart Interface
[![Packagist](https://img.shields.io/badge/version-1.2.6-brightgreen.svg)](SmartInterface)
[![Packagist](https://img.shields.io/packagist/l/doctrine/orm.svg)](LICENSE.md)

This add-on for Aerowinx PSX provides a variety of features that are necessary for those using extensive flight simulator hardware:
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