rootProject.name = "ChatChannels"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            // Paper API
            library("paper-api", "io.papermc.paper:paper-api:26.1.1.build.+")

            // compileOnly dependencies
            library("daisylib", "uk.firedev:DaisyLib:3.1-SNAPSHOT")
            library("luckperms", "net.luckperms:api:5.4")

            // implementation dependencies

            // paperLibrary dependencies

            // Gradle plugins
            plugin("shadow", "com.gradleup.shadow").version("9.2.2")
            plugin("plugin-yml", "de.eldoria.plugin-yml.paper").version("0.9.0")
        }
    }
}