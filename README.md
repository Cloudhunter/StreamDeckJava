# StreamDeckJava
An unofficial API to facilitate interaction with the [Elgato Stream Deck](https://www.elgato.com/de/gaming/stream-deck)

# Requirements
Requires the hid4java-0.5.0-modified jar in the libs folder - it has been modified to include the latest hidapi which fixes Windows 10 support.

hid4java requires [JNA 4.2.2](https://mvnrepository.com/artifact/net.java.dev.jna/jna/4.2.2) - gradle will download it automatically and there is an (untested) pom.xml which should do the same.

# Examples
Examples are located in the example folder to show you how to write images, random colours, and how to listen for key presses.

# Builds
I'm looking into uploading it to a Maven repository somewhere - I'm working on getting a website up for some direct jar links, and I will update this when I can :)

# Caveats
I've only tested it on Windows - in theory it should work on Linux and Mac Os X due to the hid4java library, but this is completely untested. Let me know!

# Credits
It is loosely based on the excellent [StreamDeckSharp](https://github.com/OpenStreamDeck/StreamDeckSharp) library - but ported to Java. Many thanks to Christian Wischenbart for that project :)

# Contact
The best way to reach me is twitter - [@Cloudhunter](https://twitter.com/Cloudhunter) - but feel free to create an issue if you have a problem!