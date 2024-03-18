package ru.kazantsev.nsd.sdk.gradle_plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import ru.kazantsev.nsd.sdk.gradle_plugin.services.NavigatorService

open class RegenerateAllFakeClassesTask : DefaultTask() {


    companion object {
        const val name = "regenerate_all_fake_classes"
    }

    override fun getGroup(): String? {
        return "nsd_sdk"
    }

    override fun getDescription(): String? {
        return "Regenerate fake classes dependency by fetching full metainfo from NSD installation"
    }

    @TaskAction
    fun action() {
        NavigatorService.instance!!.fakeClassesService.generateFullDependency()
    }

}