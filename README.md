# Dotti Testing Android app #

<i>Last update on 12/10/2015</i>

Dotti is a small 8x8 bluetooth led matrice with built in battery made by Witti

<h3>Description</h3>

Android application to manage your Dotti bluetooth device from BLE gatt interface :

* set ON/OFF (in fact setting color to 0x000000 and 0xFFFFFF(white))
* set all led to one RGB color
* modify all led color intensity
* modify color for each 64 pixel
* register your led matrice into icon slot (8 slots are available) : these slots make possible to store your led configuration
* display icon you have stored in these slots : this will display your stored led configuration

<i>Note : These icon slots are located on the bluetooth device itself, so you can store until 8 pixel mapping for icon usage</i>

Characteristics on Dotti cant be read on current device firmware. Previous state should be memorized to maintain statefull processing.

<hr/>

<h3>Build</h3>

* Build with Android Studio

* Compatible from API lvl 17+

``gradlew clean build``

<hr/>

<h3>External Lib</h3>

* Color picker by Lars Werkman : https://github.com/LarsWerkman/HoloColorPicker

<hr/>

Device specification : http://wittidesign.com/en/dotti/

![screenshot](https://raw.github.com/bertrandmartel/dotti-bluetooth-android/master/dotti1.jpg)

![screenshot](https://raw.github.com/bertrandmartel/dotti-bluetooth-android/master/dotti2.jpg)

