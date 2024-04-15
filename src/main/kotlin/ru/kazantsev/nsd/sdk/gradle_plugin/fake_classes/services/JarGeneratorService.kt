package ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.ArtifactConstants
import java.io.File

/**
 * @param artifactConstants константы артефакта
 * @param db объект соединения с базой данных
 */
class JarGeneratorService(private val artifactConstants: ArtifactConstants, private val metaHolder: MetainfoHolder) {

    companion object {
        const val BUILD_TASK = "publishToMavenLocal"
    }

    /**
     * Это логгер. Думаю тут все понятно
     */
    private val logger: Logger = LoggerFactory.getLogger(JarGeneratorService::class.java)

    /**
     * Генерирует jar
     */
    fun generate() {
        logger.info("Starting building artifact...")
        val processBuilder = ProcessBuilder("${artifactConstants.projectFolder}\\gradlew.bat", BUILD_TASK)

        processBuilder.directory(File(artifactConstants.projectFolder))

        val process = processBuilder.start()
        val inputStream = process.inputStream
        val errorStream = process.errorStream
        val inputReader = inputStream.reader()
        val errorReader = errorStream.reader()


        println("GRADLE BUILD: running task $BUILD_TASK")

        val inputThread = Thread {
            inputReader.forEachLine { line ->
                println("GRADLE BUILD: $line")
            }
        }

        val errorThread = Thread {
            errorReader.forEachLine { line ->
                error("GRADLE BUILD: $line")
            }
        }

        inputThread.start()
        errorThread.start()

        process.waitFor()
        inputThread.join()
        errorThread.join()

        val exitValue = process.exitValue()
        if (exitValue != 0) throw RuntimeException("Failed gradle build with exit value $exitValue")
        logger.info("Artifact build - done")
    }
}