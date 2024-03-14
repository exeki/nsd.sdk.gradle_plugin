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
    repositories {
        mavenLocal()
    }
}

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

dependencies {
    api("ru.kazantsev.nsd.sdk:global_variables:1.0.0")
    implementation("org.jetbrains:annotations:16.0.1")
}
