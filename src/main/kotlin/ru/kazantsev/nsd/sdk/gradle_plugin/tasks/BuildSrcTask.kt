package ru.kazantsev.nsd.sdk.gradle_plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import ru.kazantsev.nsd.sdk.gradle_plugin.services.NavigatorService

open class BuildSrcTask : DefaultTask() {

    companion object {
        const val name = "build_src"
    }

    override fun getGroup(): String? {
        return "nsd_sdk"
    }

    override fun getDescription(): String? {
        return  "Creates files from all sources in the specified directory with code ready to be placed in NSD"
    }

    @TaskAction
    fun action() {
        NavigatorService.instance!!.codeReviserService.process()
    }
}