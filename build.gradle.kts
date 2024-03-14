plugins {
    kotlin("jvm") version "1.9.10"
    id("maven-publish")
    id("java-gradle-plugin")
    id("org.jetbrains.dokka") version "1.9.10"
    id("groovy")
}

group = "ru.kazantsev.nsd.sdk.gradle_plugin"
version = "1.0.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/exeki/*")
        credentials {
            username = System.getenv("GITHUB_USERNAME")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

gradlePlugin {
    plugins {
        create("nsd-sdk") {
            id = "ru.kazantsev.nsd.sdk.gradle_plugin"
            version = "1.0.0"
            implementationClass = "ru.kazantsev.nsd.sdk.gradle_plugin.Plugin"
        }
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }

    javadoc {
        dependsOn(dokkaJavadoc)
    }

    dokkaJavadoc {
        outputDirectory.set(buildDir.resolve("docs\\javadoc"))
    }

    compileJava {
        targetCompatibility = "11"
    }

    register<Jar>("javadocJar") {
        from(getByName("javadoc").outputs.files)
        archiveClassifier.set("javadoc")
    }

    register<Jar>("sourcesJar") {
        from(sourceSets.main.get().allSource)
        archiveClassifier.set("sources")
    }
}

publishing {
    publications {
        create<MavenPublication>("pluginPublication") {
            from(components["kotlin"])
            //artifact(tasks.named("jar"))
            artifact(tasks.named("javadocJar"))
            artifact(tasks.named("sourcesJar"))
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/exeki/nsd.sdk.gradle_plugin")
            credentials {
                username = System.getenv("GITHUB_USERNAME")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

dependencies {
    api("ru.kazantsev.nsd.sdk:artifact_generator:1.0.0")
}

