rootProject.name = "ChatChannels"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            // Paper API
            library("paper-api", "io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")

            // compileOnly dependencies
            library("daisylib", "uk.firedev:DaisyLib:2.9.0-SNAPSHOT")
            library("luckperms", "net.luckperms:api:5.4")

            // implementation dependencies
            library("commandapi", "dev.jorel:commandapi-paper-shade:11.0.0")

            // paperLibrary dependencies

            // Gradle plugins
            plugin("shadow", "com.gradleup.shadow").version("9.2.2")
            plugin("plugin-yml", "de.eldoria.plugin-yml.paper").version("0.8.0")
        }
    }
}