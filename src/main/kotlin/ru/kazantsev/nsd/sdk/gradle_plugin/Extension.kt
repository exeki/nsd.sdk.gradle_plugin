package ru.kazantsev.nsd.sdk.gradle_plugin

import ru.kazantsev.nsd.sdk.gradle_plugin.services.NavigatorService
import java.net.URI

abstract class Extension(private val navigator: NavigatorService) {

    /**
     * Указать перечень метаклассов которые будут затянуты и обновлены при вызове задачи regenerate_target_fake_classes
     * @param перечень метаклассов (Set)
     */
    fun setTargetMetaClasses(set: Set<String>) {
        navigator.fakeClassesService.targetMetaclasses = set
    }

    /**
     * Установить путь до выполняемого в NSD скрипта
     * @param path путь до выполняемого в NSD скрипта
     */
    fun setConsoleScriptPath(path: String) {
        navigator.codeRunnerService.consoleScriptPath = path
        navigator.sourceSetsService.consoleFilePath = path
    }

    /**
     * Указать ID инсталляции, параметры которой будет получены из файла конфигурации для использовании в SDK
     * @param ID инсталляции
     */
    fun setInstallation(installationId: String) {
        navigator.setInstallation(installationId)
    }

    /**
     * Указать ID инсталляции, параметры которой будет получены из файла конфигурации по указаннму пути для использовании в SDK
     * @param installationId ID инсталляции
     * @param connectorParamsPath путь до файла конфигурации
     */
    fun setInstallation(installationId: String, connectorParamsPath: String) {
        navigator.setInstallation(installationId, connectorParamsPath)
    }

    /**
     * Указать все переметры инсталлляции, которая будет использования в SDK
     * @param installationId ID инсталляции
     * @param scheme схема (http или https)
     * @param host хост
     * @param accessKey ключ доступа
     * @param ignoreSLL игнорировать ssl
     */
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

    /**
     * Указывает необходимость добавить репоизатории в проект
     * добавляет репоизтории:
     * exeki githib,
     * maven local,
     * maven central
     */
    fun addRepositories(bool: Boolean = true) {
        navigator.afterEvaluateService.needToAddRepositories = bool
    }

    /**
     * Добавляет зависимости, использумые в основном только внутри IDE:
     * ru.kazantsev.nsd:json_rpc_connector,
     * ru.kazantsev.nsd.sdk:global_variables
     */
    fun addDevDependencies(bool: Boolean = true) {
        navigator.afterEvaluateService.needToAddDevDependencies = bool
    }

    /**
     * Добавляет зависимисти, которые использованы в NSD
     */
    fun addAppDependencies(bool: Boolean = true) {
        navigator.afterEvaluateService.needToAddAppDependencies = bool
    }

    /**
     * Создает папки для src:
     * scr/groovy
     * scr/modules
     * scr/scripts
     */
    fun createSourceDirs(bool: Boolean = true) {
        navigator.afterEvaluateService.needToCreateSourceDirs = bool
    }

    /**
     * Создает файл для отправки скриптов в NSD.
     * Путь до файла либо по умолчанию src/groovy/console.groovy,
     * либо установленный по методу setConsoleScriptPath
     */
    fun createConsoleFile(bool: Boolean = true) {
        navigator.afterEvaluateService.needToCreateConsoleFile = bool
    }

    /**
     * Создает перечень папок внутри src папки scripts для сортировки по ним скриптам
     */
    fun createScriptPackages(bool: Boolean = true) {
        navigator.afterEvaluateService.needToCreateScriptPackages = bool
    }

    /**
     * Включает все функции в конфигурации по умолчанию
     */
    fun default(installationId: String) {
        setInstallation(installationId)
        addRepositories()
        addFakeClasses()
        addDevDependencies()
        addAppDependencies()
        createConsoleFile()
        createSourceDirs()
        createScriptPackages()
    }

    /**
     * Включает все функции в конфигурации по умолчанию, кроме тех, которым требуется инсталлляция
     */
    fun default() {
        addRepositories()
        addDevDependencies()
        addAppDependencies()
        createSourceDirs()
        createScriptPackages()
    }

}