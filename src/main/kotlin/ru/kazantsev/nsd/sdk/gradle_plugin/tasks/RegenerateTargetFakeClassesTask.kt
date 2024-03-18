package ru.kazantsev.nsd.sdk.gradle_plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import ru.kazantsev.nsd.sdk.gradle_plugin.services.NavigatorService

open class RegenerateTargetFakeClassesTask : DefaultTask() {

    companion object {
        const val name = "regenerate_target_fake_classes"
    }

    override fun getGroup(): String? {
        return "nsd_sdk"
    }

    override fun getDescription(): String? {
        return "Regenerate some fake classes in dependency by target fetching metainfo from NSD installation"
    }

    @TaskAction
    fun action() {
        val navigator = NavigatorService.instance!!
        val target = navigator.fakeClassesService.targetMetaclasses
        if (target.isEmpty()) throw RuntimeException("Please specify the target classes in the sdk extension")
        navigator.fakeClassesService.generateTargetClasses(target)
    }
}