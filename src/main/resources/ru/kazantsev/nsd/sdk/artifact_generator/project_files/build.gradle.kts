plugins {
    id("java-library")
    id("maven-publish")
}

group = "${targetArtifactGroup}"
version = "${targetArtifactVersion}"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    withJavadocJar()
    withSourcesJar()
}

tasks {
    javadoc{
        options.encoding = "UTF-8"
    }
    compileJava{
        options.encoding = "UTF-8"
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
            }
        }
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("${repoUri}")
        credentials {
            username = "${repoUsername}"
            password = "${repoPassword}"
        }
    }
}

dependencies {
    api("ru.kazantsev.nsd.sdk:upper_level_classes:1.0.1")
    implementation("org.jetbrains:annotations:16.0.1")
}