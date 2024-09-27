package ru.kazantsev.nsd.sdk.gradle_plugin.services

import java.net.URI

/**
 * Добавляет дополнительные зависимости
 */
class DependencyService(private val navigator: NavigatorService) {
    companion object {
        val DEV_DEPENDENCY_IDS = setOf(
            "ru.kazantsev.nsd:json_rpc_connector:1.1",
            "ru.kazantsev.nsd.sdk:global_variables:1.0.1"
        )
        val APP_DEPENDENCY_IDS = setOf(
            "org.springframework:spring-web:5.3.16",
            "org.apache.poi:poi-ooxml:3.17",
            "org.codehaus.groovy.modules.http-builder:http-builder:0.7.1",
            "org.slf4j:slf4j-api:2.0.6",
            "com.google.code.gson:gson:2.8.9",
            "com.google.guava:guava:11.0",
            "org.gwtproject:gwt-user:2.10.0",
            "javax.activation:javax.activation-api:1.2.0",
            //"com.aspose:aspose-words:19.4",
            "org.codehaus.groovy.modules.http-builder:http-builder:0.7.1"
        )
    }

    var repositoryUri: URI = URI("https://maven.pkg.github.com/exeki/*")
    var repositoryUsername: String? = System.getenv("GITHUB_USERNAME")
    var repositoryPassword: String? = System.getenv("GITHUB_TOKEN")

    fun addRepositoriesToProject() {
        println("Adding repositories:")
        val customRepo = navigator.project.repositories.maven {}
        val mavenLocalRepo = navigator.project.repositories.mavenLocal()
        val mavenCentralRepo = navigator.project.repositories.mavenCentral()
        customRepo.url = repositoryUri
        if (repositoryUsername != null) {
            customRepo.credentials.username = repositoryUsername
            customRepo.credentials.password = repositoryPassword
        }
        println("   maven $repositoryUri")
        navigator.project.repositories.add(customRepo)
        println("   maven local")
        navigator.project.repositories.add(mavenLocalRepo)
        println("   maven central")
        navigator.project.repositories.add(mavenCentralRepo)
    }

    fun addDevDependenciesToProject() {
        println("Adding dev dependencies:")
        DEV_DEPENDENCY_IDS.forEach {
            println("   implementation $it")
            navigator.project.dependencies.add("implementation", it)
        }
    }

    fun addAppDependenciesToProject() {
        println("Adding app dependencies:")
        APP_DEPENDENCY_IDS.forEach {
            println("   implementation $it")
            navigator.project.dependencies.add("implementation", it)
        }
    }

    fun setRepository(uri: URI) {
        this.repositoryUri = uri
        this.repositoryUsername = null
        this.repositoryPassword = null
    }

    fun setRepository(uri: URI, username: String, password: String) {
        this.repositoryUri = uri
        this.repositoryUsername = username
        this.repositoryPassword = password
    }

}
