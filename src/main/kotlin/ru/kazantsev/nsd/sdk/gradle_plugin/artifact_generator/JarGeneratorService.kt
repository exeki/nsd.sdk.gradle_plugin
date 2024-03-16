package ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.data.DbAccess
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.data.dto.Installation
import java.io.File

/**
 * @param artifactConstants константы артефакта
 * @param db объект соединения с базой данных
 */
class JarGeneratorService(private val artifactConstants: ArtifactConstants, private val db: DbAccess) {

    /**
     * Это логгер. Думаю тут все понятно
     */
    private val logger: Logger = LoggerFactory.getLogger(JarGeneratorService::class.java)

    /**
     * Экземпляр службы, генерирущей проект
     */
    private val projectGenerator: ProjectGeneratorService = ProjectGeneratorService(artifactConstants, db)

    /**
     * Запустить сборку проекта при помощи gradle
     * ВАЖНО: gradle уже должен быть установлен на ПК
     */
    private fun runGradleBuild(task: String) {
        val processBuilder = ProcessBuilder("${artifactConstants.projectFolder}\\gradlew.bat", task)

        processBuilder.directory(File(artifactConstants.projectFolder))

        val process = processBuilder.start()
        val inputStream = process.inputStream
        val errorStream = process.errorStream
        val inputReader = inputStream.reader()
        val errorReader = errorStream.reader()

        var needToThrow = false

        println("GRADLE BUILD: running task ${task}")

        val inputThread = Thread {
            inputReader.forEachLine { line ->
                println("GRADLE BUILD: $line")
            }
        }

        val errorThread = Thread {
            needToThrow = true
            errorReader.forEachLine { line ->
                error("GRADLE BUILD: $line")
            }
        }

        inputThread.start()
        errorThread.start()

        process.waitFor()
        inputThread.join()
        errorThread.join()

        if(needToThrow) throw RuntimeException("Failed gradle build")
    }

    /**
     * Генерирует jar по единственной инсталляции в datasource
     */
    fun generate() {
        val installations = db.installationDao.queryForAll()
        if (installations.size > 1) throw RuntimeException("Found any installations in datasource")
        else if (installations.size == 0) throw RuntimeException("Cant find any installation in datasource")
        val installation: Installation = installations.firstOrNull()!!
        generate(installation)
    }

    /**
     * Генерирует jar по исталляции с указанным ID
     * @param installationId id искомой инсталляции, по которой нужно сгенерировать jar
     */
    fun generate(installationId: String) {
        val installation: Installation = db.installationDao.queryForEq("userId", installationId).firstOrNull()
            ?: throw RuntimeException("Installation $installationId not found in datasource")
        generate(installation)
    }

    /**
     * Генерирует jar по переданной инсталляции
     * @param installation инсталляция, по которой нужно сгенерировать jar
     */
    fun generate(installation: Installation) {
        logger.info("Start jar generation")
        projectGenerator.generateProject(installation)
        logger.info("Starting building artifact...")
        runGradleBuild("publishToMavenLocal")
        logger.info("Artifact build - done")
//        logger.info("Copying jar files to target folder...")
//        val newJarFolder = File(artifactConstants.newJarFolder)
//        if (newJarFolder.exists() && newJarFolder.isDirectory) {
//            newJarFolder.listFiles()?.forEach {
//                logger.info("Checking file ${it.name}...")
//                if (it.name.endsWith(".jar")) {
//                    if (it.exists()) logger.info("${it.name} существует в исходной папке")
//                    logger.info("Copying file ${it.name}...")
//                    val targetFile = File("${artifactConstants.targetJarFolder}\\${it.name}")
//                    targetFile.createNewFile()
//                    Files.copy(
//                        it.toPath(),
//                        targetFile.toPath(),
//                        StandardCopyOption.REPLACE_EXISTING
//                    )
//                    logger.info("Copying file ${it.name} - done")
//                }
//            }
//        } else {
//            throw RuntimeException("Cant find newJarFolder directory ${artifactConstants.newJarFolder} or file is not directory")
//        }
//        logger.info("Copying jar files - done")
        logger.info("Artifact generation - done")
    }
}