import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.shadow)
    alias(libs.plugins.plugin.yml)
}

repositories {
    // For testing experimental DaisyLib features
    //mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/FireML/")
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.daisylib)

    implementation(libs.commandapi)
}

group = "uk.firedev"
version = properties["project-version"] as String
description = "A library that allows external plugins to register custom chat channels"
java.sourceCompatibility = JavaVersion.VERSION_21

paper {
    name = project.name
    version = project.version.toString()
    main = "uk.firedev.chatchannels.ChatChannels"
    apiVersion = "1.21.10"
    author = "FireML"
    description = project.description.toString()

    loader = "uk.firedev.chatchannels.LibraryLoader"
    generateLibrariesJson = true

    serverDependencies {
            register("DaisyLib") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }

    permissions {
        register("chatchannels.command") {
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("chatchannel.global") {
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("chatchannel.local") {
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
    }
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.codemc.io/repository/FireML/")

            val mavenUsername = System.getenv("JENKINS_USERNAME")
            val mavenPassword = System.getenv("JENKINS_PASSWORD")

            if (mavenUsername != null && mavenPassword != null) {
                credentials {
                    username = mavenUsername
                    password = mavenPassword
                }
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = rootProject.name
            version = project.version.toString()

            from(components["java"])
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveBaseName.set(project.name)
        archiveVersion.set(project.version.toString())
        archiveClassifier.set("")

        relocate("dev.jorel.commandapi", "uk.firedev.chatchannels.libs.commandapi")
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    generatePaperPluginDescription {
        useGoogleMavenCentralProxy()
    }
}
