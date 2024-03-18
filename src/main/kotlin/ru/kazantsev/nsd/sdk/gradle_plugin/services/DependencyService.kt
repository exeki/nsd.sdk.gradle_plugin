package ru.kazantsev.nsd.sdk.gradle_plugin.services

import java.net.URI

/**
 * Добавляет дополнительные зависимости
 */
class DependencyService(private val navigator: NavigatorService) {
    companion object {
        val DEFAULT_DEPENDENCY_IDS = setOf(
            "ru.kazantsev.nsd:json_rpc_connector:1.0.1",
            "ru.kazantsev.nsd.sdk:global_variables:1.0.0"
        )
    }

    var repositoryUri: URI = URI("https://maven.pkg.github.com/exeki/*")
    var repositoryUsername: String? = System.getenv("GITHUB_USERNAME")
    var repositoryPassword: String? = System.getenv("GITHUB_TOKEN")
    val dependencyIds: MutableSet<String>

    init {
        navigator.project.repositories.add(navigator.project.repositories.mavenLocal())
        dependencyIds = mutableSetOf()
        dependencyIds.addAll(DEFAULT_DEPENDENCY_IDS)
    }

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

    fun addDependenciesToProject() {
        println("Adding dependencies:")
        dependencyIds.forEach {
            println("   implementation $it")
            navigator.project.dependencies.add("implementation", it)
        }
    }

    fun setRepository(uri : URI){
        this.repositoryUri = uri
        this.repositoryUsername = null
        this.repositoryPassword = null
    }

    fun setRepository(uri : URI, username : String, password : String){
        this.repositoryUri = uri
        this.repositoryUsername = username
        this.repositoryPassword = password
    }

}