package ru.kazantsev.nsd.sdk.gradle_plugin.services

import ru.kazantsev.nsd.sdk.gradle_plugin.tasks.BuildSrcTask
import ru.kazantsev.nsd.sdk.gradle_plugin.tasks.RegenerateAllFakeClassesTask
import ru.kazantsev.nsd.sdk.gradle_plugin.tasks.RegenerateTargetFakeClassesTask
import ru.kazantsev.nsd.sdk.gradle_plugin.tasks.SendScriptTask

/**
 * Вызывает процедуры после инициализации плагина и введенных значений
 */
class AfterEvaluateService(private val navigator: NavigatorService) {
    var needAddFakeClasses = false
    var needToAddRepositories = false
    var needToAddDevDependencies = false
    var needToAddAppDependencies = false
    var needToCreateSourceDirs = false
    var needToCreateConsoleFile = false
    var needToCreateScriptPackages = false
    fun process() {
        println()
        println("NSD SDK")
        if (needAddFakeClasses) {
            println()
            println("FAKE CLASSES")
            navigator.fakeClassesService.init()
            navigator.project.tasks.register(
                BuildSrcTask.name,
                BuildSrcTask::class.java
            )
            navigator.project.tasks.register(
                RegenerateAllFakeClassesTask.name,
                RegenerateAllFakeClassesTask::class.java
            )
            navigator.project.tasks.register(
                RegenerateTargetFakeClassesTask.name,
                RegenerateTargetFakeClassesTask::class.java
            )
        }

        if (navigator.installationIsSpecified) {
            navigator.project.tasks.register(
                SendScriptTask.name,
                SendScriptTask::class.java
            )
        }

        if(needToAddRepositories || needToAddDevDependencies || needToAddAppDependencies) {
            println()
            println("DEPENDENCY MANAGER")
            if(needToAddRepositories) navigator.dependencyService.addRepositoriesToProject()
            if(needToAddDevDependencies) navigator.dependencyService.addDevDependenciesToProject()
            if(needToAddAppDependencies) navigator.dependencyService.addAppDependenciesToProject()
        }

        if(needToCreateSourceDirs || needToCreateConsoleFile || needToCreateScriptPackages) {
            println()
            println("SOURCE SETS")
            if(needToCreateSourceDirs) navigator.sourceSetsService.createSourceSetsFolders()
            if(needToCreateConsoleFile) navigator.sourceSetsService.createConsoleFile()
            if(needToCreateScriptPackages) navigator.sourceSetsService.createScriptPackages()
        }

    }
}