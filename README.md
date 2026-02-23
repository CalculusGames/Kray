# Kray

> Kotlin/Native game engine built on raylib and raygui

## Overview

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.10-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
![GitHub License](https://img.shields.io/github/license/CalculusGames/raylibkt)

![badge-mac](http://img.shields.io/badge/platform-macos-cccccc.svg?style=flat)
![badge-linux-x64](http://img.shields.io/badge/platform-linux--x64-2D3F6C.svg?style=flat)
![badge-windows](http://img.shields.io/badge/platform-windows-4D76CD.svg?style=flat)

Kray is a Kotlin/Native game engine built on top of raylib and raygui. It provides a simple and idiomatic Kotlin API for creating games and graphical applications.

[raylib](https://github.com/raysan5/raylib) is a free and open source library to enjoy videogame programming in C.

[raygui](https://github.com/raysan5/raygui) is a simple and easy-to-use immediate-mode-gui library based on raylib.

This repository also provides Kotlin/Native bindings and wrappers for raylib and raygui allowing you to use them in Kotlin/Native projects and
make games or graphical applications in Kotlin.

## Installation

```kts
plugins {
    kotlin("multiplatform") version "[KOTLIN_VERSION]"
}

dependencies {
    implementation("xyz.calcugames:raylibkt:[VERSION]")
}
```

## Contributing

Contributions are welcome! Feel free to open an issue or submit a pull request.
