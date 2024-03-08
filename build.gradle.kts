plugins {
    kotlin("jvm") version "1.9.10"
    id("maven-publish")
    id("java-gradle-plugin")
    id("org.jetbrains.dokka") version "1.9.10"
    id("groovy")
    //`kotlin-dsl`
}

group = "ru.kazantsev.nsd.sdk.gradle_plugin"
version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()
}

open class TestTask : DefaultTask(){
    @TaskAction
    fun doLat(){

    }
}

tasks.register<TestTask>("testarg") {
    doLast {
        println(project.hasProperty("arg"))
//        if (project.hasProperty("args")) {
//            println("Our input argument with project property [" + project.properties + "]")
//        }
//        println("Our input argument with system property  [" + project.properties + "]")
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
        mavenLocal()
    }
}

dependencies {
    api("ru.kazantsev.nsd.sdk:artifact_generator:1.0.0")
    api("ru.kazantsev.nsd:basic_api_connector:1.0.0")
}

