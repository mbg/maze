# Warwick Maze 

This repository contains a fork of the original [Warwick Maze code](https://sourceforge.net/projects/warwickmaze/) which brings several quality of life improvements such as a Gradle build file. The original project is licenced under the GPLv2 and therefore this version is too. 

## Building

To generate `warwickmaze.jar` from this, simply run `./gradlew dist` in the root directory. `./gradlew init` and `./gradlew compile` are also available. See ANT's original `build.xml` for details on those build tasks. (Note that none of these will show up if running `./gradlew tasks`.)
