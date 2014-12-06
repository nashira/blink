blink
=====

Server scripts and Android app to control a Wink hub on a local network


This project is fairly eary stage.  It only supports dimmable lights as of now. 
Adding devices from the app is not implemented yet.

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
* open the app and configure the ip/hostname of the wink hub and the ssid of your network
* you must have added the devices through some other means, I don't have that hooked up yet
