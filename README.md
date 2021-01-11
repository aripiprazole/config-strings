# config-strings

![GitHub Repo stars](https://img.shields.io/github/stars/gabrielleeg1/config-strings?color=orange&style=for-the-badge)
![GitHub issues](https://img.shields.io/github/issues/gabrielleeg1/config-strings?color=orange&style=for-the-badge)
![GitHub last commit](https://img.shields.io/github/last-commit/gabrielleeg1/config-strings?color=orange&style=for-the-badge)
![JitPack](https://img.shields.io/jitpack/v/github/gabrielleeg1/config-strings?color=orange&style=for-the-badge)

ConfigStrings is a simple programming language to be used in strings, specifically in configurations.
Need help? contact me on [twitter](https://twitter.com/gabrielleeg1) or message me on discord **Gabii#3336**.

### Content

* [Dependency](#dependency)
* Documentation (WIP)

## Dependency

In order to use config-strings you'll need the following on your buildscript

```gradle
repositories {
  maven { url 'https://jitpack.io' }
}

dependencies {
  implementation 'com.github.gabrielleeg1:config-strings:VERSION'
}
```

If you are using kotlin dsl

```kotlin
repositories {
  maven("https://jitpack.io")
}

dependencies {
  implementation("com.github.gabrielleeg1:config-strings:VERSION")
}
```
