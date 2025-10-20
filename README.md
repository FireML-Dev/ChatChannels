# ChatChannels

ChatChannels is a Paper plugin that allows you to register unlimited channels for chat messages to be sorted through.

Channels can be added via config files and/or through the plugin's simple API.

---

## Dependencies

ChatChannels depends on the following plugins:
- [DaisyLib](https://ci.codemc.io/job/FireML/job/DaisyLib/) - Required

---

## Download

There is currently one source for the plugin:
- [Jenkins](https://ci.codemc.io/job/FireML/job/ChatChannels/) (Experimental)

--- 

## Developers

The plugin's API is available on the CodeMC Maven Repository.

### Gradle (Kotlin)
```kotlin
repositories {
    maven("https://repo.codemc.io/repository/FireML/")
}

dependencies {
    // This may not be the latest version. Make sure you check the latest version before continuing.
    compileOnly("uk.firedev:ChatChannels:1.0.4-SNAPSHOT")
}
```
