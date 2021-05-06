# iCueConnect-Android 
## Description
iCueConnect-Android is an Android app that allows you to control your Corsair iCUE device LEDs with the help of the [iCueConnect-API]()<br/>
**THIS README APPLIES FOR THE iCueConnect-Android APP....**<br/>
**FOR DOCUMENTATION REGARDING iCueConnect-API, CLICK THE PRIOR LINK**
>Note: This is an early development release. Some things may not work perfectly, and you may experience some bugs/glitches. Please feel free to inform me of any issues you are having via the issues tab.

## Requirements
* You will need iCue installed and running on your windows machine.
* You will need to download and configure the iCueConnect-API on your windows machine.
* You will have to have configured all your Corsair devices through iCue.
* In order for iCueConnect-Android App to communicate with [iCueConnect-API]() and set the LEDs accordingly, both devices must be connected to the internet.
* Lastly you will have to check the **Enable SDK** option in iCue's Settings.
<p align="center">
  <img src="https://help.corsair.com/hc/article_attachments/360072361252/iCUE_SDK_enabled.jpg" width="800px">
</p>

## Installation Instructions & Configuration

### Downloading  
1. Download the iCueConnect-Android App [here](). 
2. You will be receive a notification asking what to open the file with. Select **Package installer**.
3. You may receive a security warning stating that your phone is not allowed to install unknown apps from this source. Two buttons will be displayed, **Cancel** and **Settings**. Click **Settings**.
4. Toggle the option labeled **Allow from this source** so that it is ON.
5. Press the back button so that you are on the screen showing the prompt asking **Do you want to install this application**. Two buttons will be displayed, **Cancel** and **Install**. Click **Install**.

### Setting Up Pusher
iCueConnect uses Pusher for realtime communication between iCueConnect-Android App and iCueConnect-API using websockets. You do not have to know exactly what Pusher is and how it works. You just need to make an account in order for this iCueConnect to work. If you have not done so already preform the following to create a Pusher account.
1. Create a free Pusher account [here](https://dashboard.pusher.com/accounts/sign_up).
2. When asked to get started between Channels or Breams, choose **CHANNELS**.
3. When given the following input fields, enter the associated values and then press the "Create app" button.
    * Name your app: *iCueConnect*
    * Select a cluster: *Choose according to your region.*
    * Create apps for multiple environments?: Unchecked.
    * Choose your tech stack (optional): *You can ignore these.*
4. After clicking "Create app", click the link on the left side labeled "App Keys". Here you will see four values labeled 
    * *app_id*
    * *key*
    * *secret*
    * *cluster*
5. Save these values. We will need them later.

### Configuring iCueConnect on Android 
1. First make sure that you downloaded and configured the iCueConnect-API. **If you did not do this, the app will not work**.
2. Open iCueConnect on Android and click the settings icon <img src="https://icon-library.com/images/white-gear-icon/white-gear-icon-6.jpg" width="20px"> in the top right corner.
3. In the fields enter the associated values that we got from creating a Pusher account. **DO NOT ENTER THE QUOTATION MARKS, ONLY ENTER THE TEXT**
4. Click the **SAVE** button to set the values.
    > Note: If you enter an incorrect value for any of the fields, simply update the values in the fields and click the **SAVE** button again.

## How To Use
1. Revert Control
    * This reverts led control back to iCue. 
    * For example, if you set the Leds to a color from iCueConnect-Android App, then they will stay that color unless you click **Revert Control**.
        > Note: The iCueConnect-Android App also has this same functionality, but it is the <img src="https://www.iconsdb.com/icons/preview/white/refresh-2-xxl.png" width="20px"> icon.
2. Quit
    * This closes the application.

## Help
* Leds not updating according to your phones selection? Follow these trouble shooting steps.
    * Make sure that iCue is running in the background and that the **Enable SDK** option is set in the settings.
    * Sometimes iCue will need to be reopened. Try that.
    * Make sure your Pusher credentials are correct on both devices. 
        > Note: If you enter an incorrect key for the windows app, simply go into the iCueConnect folder and delete the **data.json** file, and run the iCueConnect.exe again.
    * Pusher credentials are correct, but leds are still not updating? Try running iCueConnect.exe as an administrator.
* Leds are extremely delayed?
    * Leds on occasion can become delayed, however if there is a continues long delay then try setting iCueConnect's priority to realtime or high. Don't know how to do this? Check this [tutorial](https://winaero.com/change-process-priority-windows-10/) out then!
        > Note: iCueConnect will appear in the Details tab twice in the Task Manager. Set both accordingly. 

## Version History
* 1.0
    * Initial Release

# For Developers
## Language of Choice
iCueConnect is a Python based application that utilizes the following:
* Python 3.8.5

## Utilized Libraries 
* [cuesdk: 0.6.6](https://github.com/CorsairOfficial/cue-sdk-python)
* [PyQt5: 5.15.2](https://www.qt.io/)
* [websocket-client: 0.48.0](https://github.com/websocket-client/websocket-client)
* [Pysher: 1.0.3](https://github.com/deepbrook/Pysher)
* [six: 1.15.0](https://github.com/benjaminp/six)
* [PyInstaller: 5.0.dev0](http://www.pyinstaller.org/)
    * This is what I used to develop the .exe file.
>Note: There is a bug with the Pysher library that is caused due to changes in the websocket-client library. To get around this I strictly had to use the listed version for websocket-client, Pysher and six.
