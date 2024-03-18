package ru.kazantsev.nsd.sdk.gradle_plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

import ru.kazantsev.nsd.sdk.gradle_plugin.services.NavigatorService

class Plugin : Plugin<Project> {

    override fun apply(project: Project) {
        val navigator = NavigatorService(project)
        project.afterEvaluate {
            navigator.afterEvaluateService.process()
        }
    }
}