package ru.kazantsev.nsd.sdk.gradle_plugin.services

import ru.kazantsev.nsd.basic_api_connector.Connector
import java.io.File

/**
 * Отправляет код в NSD
 */
class CodeRunnerService(private val navigator: NavigatorService) {

    var consoleScriptPath : String? = null

    companion object {
        const val defaultRunningScript = "src/main/groovy/console.groovy"
    }

    fun sendScript(file:File){
        navigator.checkInstallationIsSpecifiedElseThrow()
        val codeReviserExtension = navigator.codeReviserService
        val processedFile = codeReviserExtension.processFile(file)
        val connector = Connector(navigator.connectorParams)
        val message : String? = connector.execFile(processedFile)
        if (message != null) {
            println("------------NSD SCRIPT RESULT------------")
            println(message)
            println("-----------------------------------------")
        }
    }
}