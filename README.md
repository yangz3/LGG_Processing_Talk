# LGG_Processing_Talk
LGG watch processing communication

This is a group of three programs to initiate the communication between the LG G watch and the macbook with BLE support.
On the watch side there is an android program that uses the processing UI builder and backed by the myBLEmanager class which does all communications in the BLE level including setting it up as a peripheral and reading and writing data.
This is a application developed for the smart watch directly and not wrapped with any phone apps the so only way to install this on the watch is by using the developer SDK.

On the mac side there are two programs, one is written java using the processing APIs to be used as the GUI and the other is written in objective-c while forms a bridge between the two other applications,
it connects to the watch using BLE and processing using sockets and forward messages between those two as Java can't have control to Mac's BLE control.

This is a simple example to show how the communication works, first the Java program gets the coordinates of the mouse on it's applet,
sends it as two bytes over the socket to the objective-c program which forwards the data to the LG G watch which then draws a  blue dot at the corresponding position.

To make this work, start the app on the watch and the Java program first, and touch the watch screen to make it red, then start the objective-c program and click on the make request button, it should try and connect to the watch,
once the watch is connected it's background should be yellow and you can move the mouse in the Java applet and see the blue circle move corresponding to the mouse.
