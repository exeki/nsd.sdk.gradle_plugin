package ru.kazantsev.nsd.sdk.gradle_plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

import ru.kazantsev.nsd.sdk.gradle_plugin.services.SingletonNavigatorService

class Plugin : Plugin<Project> {
    override fun apply(project: Project) {
        SingletonNavigatorService.init(project)
    }
}