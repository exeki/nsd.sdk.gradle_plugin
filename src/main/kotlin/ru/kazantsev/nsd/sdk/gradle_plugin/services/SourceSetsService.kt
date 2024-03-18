package ru.kazantsev.nsd.sdk.gradle_plugin.services

import org.gradle.api.tasks.GroovySourceSet
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import java.io.File
import java.io.FileWriter


class SourceSetsService(private val navigator: NavigatorService) {
    companion object {
        val newConsoleFileText = """
           //The script from this file can be sent to the NSD 
           //for execution using the task "send_script".
           //The result of the execution will be displayed in the console.
           
           import static ru.kazantsev.nsd.sdk.global_variables.ApiPlaceholder.*
           import static ru.kazantsev.nsd.sdk.global_variables.GlobalVariablesPlaceholder.*
           import ru.naumen.core.server.script.spi.*
        """.trimIndent()
    }

    var consoleFilePath: String = CodeRunnerService.defaultRunningScript
    var sourceSets: MutableSet<String> = mutableSetOf("src/main/groovy", "src/main/modules", "src/main/scripts")
    fun createConsoleFile() {
        val consoleFile = File(consoleFilePath)
        if (!consoleFile.exists()) {
            println("The console file does not exist on the path \"$consoleFilePath\", creating...")
            val parent = consoleFile.parentFile
            if (!parent.exists()) parent.mkdirs()
            consoleFile.createNewFile()
            val writer = FileWriter(consoleFile)
            writer.write(newConsoleFileText)
            writer.close()
            println("Created.")
        } else println("The console file exists.")
    }

    fun createSourceSetsFolders() {
        println("Checking src folders...")
        val sourceSetContainer = navigator.project.getExtensions().getByType(SourceSetContainer::class.java)
        val main = sourceSetContainer.maybeCreate(SourceSet.MAIN_SOURCE_SET_NAME)
        var created = 0
        sourceSets.forEach {
            val file = File(it)
            if(!file.exists()) {
                println("Creating src folder \"${it}\".")
                file.mkdirs()
                created++;
            }
            main.java.srcDir(it)
        }
        if(created == 0) println("All folders exists.")
    }
}