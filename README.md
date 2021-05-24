# `protect-the-researchers`

This is a repository to hold all the code for the `protect-the-researchers`
game, written by Team Neptune (Clinton, Dylan, Cecil, Johnny).

This is a LibGDX desktop application. The repository's main purpose is to
hold the code for the Java desktop app, which displays a fullscreen set of
pictures to prompt for feedback and respond to user input. However, the
Arduino code is also contained in the `arduino` folder for archival purposes
as well.

# Implementation

The `arduino/` folder contains the Arduino code. The implementation has been
sufficiently documented in the file, but in summary, is a finite state machine
that runs infinitely for game states such as begin, run and end. The run state
is also another finite state machine to handle each "round."

The `core/` folder contains the GUI code. Again, the code is commented generously,
but the general idea is that after startup, the `Game` waits for an asynchronous
message to be delivered by the Arduino following the protocol through the serial
port and responds to it as necessary.

The `desktop/` folder contains the bootstrap for the desktop application. The
desktop application can take either a `--show-ports` argument to show the
available serial ports or 2 serial port IDs from the shown ports corresponding
to the left and right Arduinos. They can also be ports prefixed with "dummy" to
use a dummy Arduino for the purposes of debugging.

# Photos

![Game board](https://i.postimg.cc/rpRh1mwf/A8-A2322-B-3-E67-4062-9-D5-A-A1-CE4-EAE5-E65.jpg)

![Wiring diagram](https://i.postimg.cc/QdxFF4qb/Protect-The-Researchers-bb.png)

# Building

The desktop application can be built using the Gradle desktop:dist task.

``` shell
git clone https://github.com/caojohnny/protect-the-researchers.git
cd protect-the-researchers
./gradlew desktop:dist
```

The initial build must be done with the [texture packer](https://github.com/caojohnny/protect-the-researchers/blob/master/core/build.gradle#L33-L36) uncommented in order for the assets to load correctly
in the desktop app.

The Arduino code requires the Arduino program to be installed on your computer
and an Arduino Uno board. The Arduino should have the same wiring as presented
in the photos section above for the code to work.

# Credits

Built with [IntelliJ IDEA](https://www.jetbrains.com/idea/)

Uses [libGDX](https://libgdx.badlogicgames.com/),
[jSerialComm](https://fazecast.github.io/jSerialComm/),
and [Arduino](https://www.arduino.cc/)

Some assets used in this project do not belong to me.
The source for these assets can be found at 
`core/assets/GameStart/Credits/Credits.png`
