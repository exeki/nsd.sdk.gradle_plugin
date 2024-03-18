package ru.kazantsev.nsd.sdk.gradle_plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import ru.kazantsev.nsd.sdk.gradle_plugin.services.CodeRunnerService
import ru.kazantsev.nsd.sdk.gradle_plugin.services.NavigatorService
import java.io.File

open class SendScriptTask : DefaultTask() {

    companion object {
        const val name = "send_script"
    }

    override fun getGroup(): String? {
        return "nsd_sdk"
    }

    override fun getDescription(): String? {
        return "Sending script to NSD installation to run it"
    }

    @TaskAction
    fun action() {
        val runner = NavigatorService.instance!!.codeRunnerService
        val file = if (runner.consoleScriptPath != null) File(runner.consoleScriptPath!!)
        else File(CodeRunnerService.defaultRunningScript)
        runner.sendScript(file)
    }
}