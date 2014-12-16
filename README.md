blink
=====

Server scripts and Android app to control a Wink hub on a local network

**News**
* Supports adding and removing devices.
* Uses the built in "Groups" functionality of the new .47 firmware, groups of lights respond all at once, instead of one at a time.  Current master is incompatible with older firmware.

![Devices](/../screenshots/screenshots/devices.png?raw=true "Devices")
![Devices](/../screenshots/screenshots/groups.png?raw=true "Groups")
![Devices](/../screenshots/screenshots/scenes_collapsed.png?raw=true "Scenes Collapsed")
![Devices](/../screenshots/screenshots/scenes_expanded.png?raw=true "Scenes Expanded")
![Devices](/../screenshots/screenshots/add_edit.png?raw=true "Add/Edit")

This project is fairly eary stage.  It only supports dimmable lights as of now. 

**Features:**

* Let's you change device configurations when not connected to the hub's network and syncs when connected
* Create groups of devices to control configurations simultaneously
* Create scenes of devices with specific configurations to apply all at once
* Write device/group/scene configurations to NFC tags
* Tap NFC tags to apply configurations

**Known issues:**
* It uses the 'aprontest' executable on the server, seems flakey sometimes
* If you controll the hub from more than one android device, you must refresh the hub configuration manually (refresh menu item)
* It only supports Zigbee HA Dimmable type devices so far
* After adding a device, sometimes a manual refresh is needed for it to show up

**To use:**
* copy the server/api directory to /var/www/ on your wink hub.

Directory structure should be:
```
/var/www/api/db.php
/var/www/api/attribute_types/index.php
/var/www/api/commands/index.php
/var/www/api/device_types/index.php
/var/www/api/devices/index.php
```
* open the app and configure the ip/hostname of the wink hub and the ssid of your network

