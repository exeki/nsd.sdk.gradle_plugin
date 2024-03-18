package ru.kazantsev.nsd.sdk.gradle_plugin

import ru.kazantsev.nsd.sdk.gradle_plugin.services.NavigatorService
import java.net.URI

abstract class Extension(private val navigator: NavigatorService) {

    fun setTargetMetaClasses (set : Set<String> ){
        navigator.fakeClassesService.targetMetaclasses = set
    }

    fun setConsoleScriptPath(path : String){
        navigator.codeRunnerService.consoleScriptPath = path
    }

    fun setInstallation(installationId: String) {
        navigator.setInstallation(installationId)
    }

    fun setInstallation(installationId: String, connectorParamsPath: String) {
        navigator.setInstallation(installationId, connectorParamsPath)
    }

    fun setInstallation(
        installationId: String,
        scheme: String,
        host: String,
        accessKey: String,
        ignoreSLL: Boolean
    ) {
        navigator.setInstallation(
            installationId,
            scheme,
            host,
            accessKey,
            ignoreSLL
        )
    }

    /**
     * Указать репозиторий, из которого происходит затягивание
     * дополнительных зависимостей для вашего проекта и сгенерированного артефакта фейковых классов.
     * Разрешены только maven репозитории
     * @param uri ссылка на репозиторий
     */
    fun setRepository(uri: URI) {
        navigator.artifactConstants.setRepository(uri)
        navigator.dependencyService.setRepository(uri)
    }

    /**
     * Указать репозиторий, из которого происходит затягивание
     * дополнительных зависимостей для вашего проекта и сгенерированного артефакта фейковых классов.
     * Разрешены только maven репозитории
     * @param uri ссылка на репозиторий
     * @param username имя пользователя
     * @param password пароль
     */
    fun setRepository(uri: URI, username: String, password: String) {
        navigator.artifactConstants.setRepository(uri, username, password)
        navigator.dependencyService.setRepository(uri, username, password)
    }


    /**
     * Указывает необходимость сгенерировать фейковые классы
     * расширение затянет метаинформацию из NSD создаст артефакт с ними.
     * Артефакт автоматически будет поключен к проекту
     */
    fun addFakeClasses(bool: Boolean = true) {
        navigator.afterEvaluateService.needAddFakeClasses = bool
    }

    fun addRepositories(bool : Boolean = true){
        navigator.afterEvaluateService.needToAddRepositories = bool
    }

    fun addDependencies(bool: Boolean = true) {
        navigator.afterEvaluateService.needToAddDependencies = bool
    }

    fun createSourceDirs(bool: Boolean = true) {
        navigator.afterEvaluateService.needToCreateSourceDirs = bool
    }

    fun createConsoleFile(bool: Boolean = true) {
        navigator.afterEvaluateService.needToCreateConsoleFile = bool
    }

    fun default(installationId: String){
        setInstallation(installationId)
        addFakeClasses()
        addDependencies()
        addRepositories()
        createConsoleFile()
        createSourceDirs()
    }

}