plugins {
    kotlin("jvm") version "1.9.10"
    id("maven-publish")
    id("java-gradle-plugin")
    id("org.jetbrains.dokka") version "1.9.10"
    id("groovy")
}

group = "ru.kazantsev.nsd.sdk"
version = "1.1.3.1"

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
        create("nsd_sdk") {
            id = "nsd_sdk"
            version = project.version
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
    implementation("ru.kazantsev.nsd:basic_api_connector:1.0.1")
    implementation("ru.kazantsev.nsd.sdk:upper_level_classes:1.0.0")
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.j256.ormlite:ormlite-jdbc:6.1")
    implementation("com.h2database:h2:2.1.214")
    implementation("com.squareup:javapoet:1.13.0")
    implementation("org.jsoup:jsoup:1.16.1")
    testImplementation(kotlin("test"))
}

logging.captureStandardOutput(LogLevel.INFO)

System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info")
System.setProperty("org.slf4j.simpleLogger.logFile", "System.out")
System.setProperty("org.slf4j.simpleLogger.showDateTime", "true")
System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss.SSS")