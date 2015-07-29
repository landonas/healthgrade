# healthgrade
Andorid app for viewing resuraunt's health inspection grades

This README would normally document whatever steps are necessary to get your application up and running.

What is this repository for? This repo is for our ICS 466 final project.

How do I get set up? Download a zip of the repo on the side and import the project into android studio or clone it if you're using sourcetree If there are some google maps api authorization failures, you might need to create your own google api key for your computer and replace it in the "String/google_maps_key" in the androidmanifest.xml

To test on an emulator use Genymotion. Download and install Genymotion Download the plugin for android studio Download the arm translation zip from here http://filetrip.net/dl?4SUOrdcMRv Download the google apps for 4.2 from here https://goo.im/gapps/gapps-jb-20130812-signed.zip/ Start up GenyMotion add a new device use custome phone 4.2.2 api 17 start up the vm device drag drop the arm translation zip into the vm phone click ok when it asks to flash it close and reopen the vm to reboot it drag and drop the gapps zip into the vm phone device and click ok when it asks to flash it close and reopen the vm to reboot it update the play store, go to location settings and allow should be good to go build the app in android studio run the app through existing android device and the genymotion should be under running device

To test on android device make sure android device has android debug mode on plug android phone to computer run app through connected device, it should be under running devices
