package ru.kazantsev.nsd.sdk.gradle_plugin.extensions

import org.gradle.api.Project
import ru.kazantsev.nsd.basic_api_connector.Connector
import ru.kazantsev.nsd.basic_api_connector.ConnectorParams
import ru.kazantsev.nsd.sdk.gradle_plugin.services.SingletonNavigatorService
import java.io.File

class CodeRunnerExtension(private val project: Project) {
    private val scriptFile: File
    var installationId: String? = null
    var connectorParams : ConnectorParams? = null

    companion object {
        private const val defaultRunningScript = "src/main/groovy/console.groovy"
    }
    init {
        val projectDir = project.projectDir.path
        this.scriptFile = File("$projectDir\\$defaultRunningScript")
        project.task("send_script") {
            it.group = "nsd_sdk"
            it.description = "Sending script to NSD installation to run it"
            it.doLast {
                sendScript(scriptFile)
            }
        }
    }
    fun setInstallation(
        installationId: String
    ) {
        this.installationId = installationId
        this.connectorParams = ConnectorParams.byConfigFile(installationId)
    }
    fun setInstallation(
        installationId: String,
        scheme: String,
        host: String,
        accessKey: String,
        ignoreSLL: Boolean
    ) {
        this.installationId = installationId
        this.connectorParams = ConnectorParams(
            installationId,
            scheme,
            host,
            accessKey,
            ignoreSLL
        )
    }
    fun sendScript(file:File){
        val codeReviserExtension = SingletonNavigatorService.codeReviserExtension
        val processedFile = codeReviserExtension?.processFile(file)
        if(this.connectorParams == null) {
            val fakeClassesExtension = SingletonNavigatorService.fakeClassesExtension
            this.connectorParams = fakeClassesExtension?.connectorParams
            if(this.connectorParams == null) throw RuntimeException("Cant find nsd connection params. Pls set it by CodeRunnerExtension.setInstallation() method")
        }
        val connector = Connector(this.connectorParams)
        val message : String? = connector.execFile(processedFile)
        if (message != null) {
            println("------------NSD SCRIPT RESULT------------")
            println(message)
            println("-----------------------------------------")
        }
    }
}