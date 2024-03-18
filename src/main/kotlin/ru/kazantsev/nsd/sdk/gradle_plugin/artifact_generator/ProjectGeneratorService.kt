package ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator

import com.squareup.javapoet.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.data.DbAccess
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.data.dto.Installation
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.src_generation.ClassGeneratorService
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.src_generation.MetainfoClassGeneratorService
import java.io.File

/**
 * Служба, генерирующая весь проект
 * является входной точкой в генерацию проекта
 * @param artifactConstants константы генерируемого артефакта (пути, наименования)
 * @param db экземпляр связи с базой данных
 */
class ProjectGeneratorService(private var artifactConstants: ArtifactConstants, private val db: DbAccess) {

    private val logger: Logger = LoggerFactory.getLogger(ProjectGeneratorService::class.java)
    private val classLoader: ClassLoader = this.javaClass.classLoader

    /**
     * Скопировать файл с заменой текста
     * @param resourcePath ресурс, который нужно скопировать
     * @param targetFolder целевая папка
     * @param variables значения для змены
     */
    private fun copyResourceAsTemplate(resourcePath: String, targetFolder: String, variables: Map<String, String?>) {
        var template = classLoader.getResource(resourcePath)?.readText()
            ?: throw RuntimeException("Cant find $resourcePath file in resources")
        variables.forEach { (key, value) ->
            template = template.replace("\${$key}", value ?: "")
        }
        File("$targetFolder\\${File(resourcePath).name}").writeText(template)
    }

    /**
     * Скопировать файл
     * @param resourcePath ресурс, который нужно скопировать
     * @param targetFilePath целевой файл
     */
    private fun copyFile(resourcePath: String, targetFilePath: String) {
        val inputStream = classLoader.getResourceAsStream(resourcePath)
            ?: throw RuntimeException("Cant find $resourcePath file in resources")
        val targetFile = File(targetFilePath)
        targetFile.parentFile.mkdirs()
        targetFile.writeBytes(inputStream.readAllBytes())
    }

    /**
     * Сгенерировать проект
     * хранилище метаинформации должно быть заранее наполнено
     */
    fun generate() {
        val inst: Installation =
            db.installationDao.queryForEq("userId", this.artifactConstants.installationId).firstOrNull()
                ?: throw RuntimeException("Installation $this.artifactConstants.installationId not found in datasource")
        logger.info("Project generation started...")
        val existedProject = File(artifactConstants.projectFolder)
        if (existedProject.exists()) {
            logger.info("Found existed project folder, deleting...")
            existedProject.delete()
            logger.info("Existed project deleting - done")
        }
        val classGenerator = ClassGeneratorService(artifactConstants, db)
        logger.info("Class generation started...")
        inst.metaClasses.forEach {
            val classProto: TypeSpec = classGenerator.generateClassProto(it).build()
            val file = File(artifactConstants.generatedProjectSrcPath)
            val fileContent = JavaFile.builder(artifactConstants.packageName, classProto).build()
            fileContent.writeTo(file)
        }
        logger.info("Class generation - done")
        logger.info("Metainfo class generation started...")
        val metaClassGenService = MetainfoClassGeneratorService(classGenerator, artifactConstants)
        val meteClazz: TypeSpec = metaClassGenService.generateMetaInfoClass().build()
        val fileContent = JavaFile.builder(artifactConstants.generatedMetaClassPackage, meteClazz)
        fileContent.build().writeTo(File(artifactConstants.generatedProjectSrcPath))
        logger.info("Metainfo class generation - done")
        logger.info("Copy files...")

        copyFile(
            "ru/kazantsev/nsd/sdk/artifact_generator/project_files/gradle/wrapper/gradle-wrapper.jar",
            "${artifactConstants.projectFolder}\\gradle\\wrapper\\gradle-wrapper.jar"
        )
        copyFile(
            "ru/kazantsev/nsd/sdk/artifact_generator/project_files/gradle/wrapper/gradle-wrapper.properties",
            "${artifactConstants.projectFolder}\\gradle\\wrapper\\gradle-wrapper.properties"
        )
        copyFile(
            "ru/kazantsev/nsd/sdk/artifact_generator/project_files/src/main/java/ru/kazantsev.nsd.sdk.generated_fake_classes/package-info.java",
            "${artifactConstants.generatedProjectSrcPath}\\ru\\kazantsev\\nsd\\sdk\\generated_fake_classes\\package-info.java"
        )
        copyFile(
            "ru/kazantsev/nsd/sdk/artifact_generator/project_files/src/main/java/ru/naumen/core/server/script/spi/package-info.java",
            "${artifactConstants.generatedProjectSrcPath}\\ru\\naumen\\core\\server\\script\\spi\\package-info.java"
        )
        copyFile(
            "ru/kazantsev/nsd/sdk/artifact_generator/project_files/src/main/java/ru/naumen/core/shared/dto/ISDtObject.java",
            "${artifactConstants.generatedProjectSrcPath}\\ru\\naumen\\core\\shared\\dto\\ISDtObject.java"
        )
        copyFile(
            "ru/kazantsev/nsd/sdk/artifact_generator/project_files/src/main/java/ru/naumen/core/shared/dto/package-info.java",
            "${artifactConstants.generatedProjectSrcPath}\\ru\\naumen\\core\\shared\\dto\\package-info.java"
        )
        copyFile(
            "ru/kazantsev/nsd/sdk/artifact_generator/project_files/gradlew.bat",
            "${artifactConstants.projectFolder}\\gradlew.bat"
        )
        copyFile(
            "ru/kazantsev/nsd/sdk/artifact_generator/project_files/gradlew",
            "${artifactConstants.projectFolder}\\gradlew"
        )

        //copyResourceDirectory("ru/kazantsev/nsd/sdk/artifact_generator/project_files/gradle", "${artifactConstants.projectFolder}\\gradle")
        //copyResourceDirectory("ru/kazantsev/nsd/sdk/artifact_generator/project_files/src", "${artifactConstants.projectFolder}\\src")

        copyResourceAsTemplate(
            "ru/kazantsev/nsd/sdk/artifact_generator/project_files/build.gradle.kts",
            artifactConstants.projectFolder,
            mapOf(
                "targetArtifactVersion" to artifactConstants.targetArtifactVersion,
                "targetArtifactGroup" to artifactConstants.targetArtifactGroup,
                "repoUri" to artifactConstants.repositoryUri.toString(),
                "repoUsername" to artifactConstants.repositoryUsername,
                "repoPassword" to artifactConstants.repositoryPassword
            )
        )

        copyResourceAsTemplate(
            "ru/kazantsev/nsd/sdk/artifact_generator/project_files/settings.gradle.kts",
            artifactConstants.projectFolder,
            mapOf("targetArtifactName" to artifactConstants.targetArtifactName)
        )

        logger.info("Copy files - done")
        logger.info("Project generation - done")
    }
}