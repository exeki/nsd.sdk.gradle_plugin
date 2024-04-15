package ru.kazantsev.nsd.sdk.gradle_plugin.services

import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.services.JarGeneratorService
import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.services.ProjectGeneratorService
import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.services.MetainfoHolder
import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.services.MetainfoUpdateService
import java.io.File

class FakeClassesService(private val navigator: NavigatorService) {

    var targetMetaclasses: Set<String> = setOf()

    /**
     * Целевое наименование артефакта для полдключения
     * @return идентификатор артефакта,
     * по которому можно подключить сгенерированный артефакт
     */
    private fun getTargetArtifactId(): String {
        navigator.checkInstallationIsSpecifiedElseThrow()
        val artifactConstants = navigator.artifactConstants
        val group = artifactConstants.targetArtifactGroup
        val name = artifactConstants.targetArtifactName
        val version = artifactConstants.targetArtifactVersion
        return "$group:$name:$version"
    }

    /**
     * Инициализировать артефакт
     */
    fun init() {
        val localMavenPath = "${System.getProperty("user.home")}\\.m2\\repository"
        val dependencyPath = localMavenPath
            .plus("\\")
            .plus(navigator.artifactConstants.targetArtifactGroup.split('.').joinToString("\\"))
            .plus("\\")
            .plus(navigator.artifactConstants.targetArtifactName.split('.').joinToString("\\"))
            .plus("\\")
            .plus(navigator.artifactConstants.targetArtifactVersion)

        val jarExists: Boolean = File(dependencyPath).exists()
        if (!jarExists) {
            println("Fake classes jar not exists in path \"$dependencyPath\", starting the generation...")
            this.generateFullDependency()
            println("Fake classes jar file generation in maven local repository is completed.")
        } else {
            println("Fake classes jar exists in maven local repository:")
            println(dependencyPath)
        }

        navigator.project.dependencies.add("implementation", getTargetArtifactId())

        val metainfoService = navigator.metainfoService
        metainfoService.fakeClassesDependencyAdded = true
        metainfoService.fakeClassesMetainfoClassName =
            "${navigator.artifactConstants.generatedMetaClassPackage}.${navigator.artifactConstants.generatedMetaClassName}"
        metainfoService.fakeClassesArtifactName = getTargetArtifactId()
    }

    fun generateTargetClasses(targetMeta: Set<String>) {
        val metaHolder = MetainfoHolder.getInstance(navigator.connectorParams.userId)
        println("Fetching metainfo...")
        MetainfoUpdateService(navigator.connectorParams, metaHolder).fetchMeta(targetMeta)
        println("Fetching metainfo - done")
        println("Project generation...")
        ProjectGeneratorService(navigator.artifactConstants, metaHolder).generate()
        println("Project generation - done")
        println("Jar generation...")
        JarGeneratorService(navigator.artifactConstants, metaHolder).generate()
        println("Jar generation - done")
    }

    fun generateFullDependency() {
        val metaHolder = MetainfoHolder.getInstance(navigator.connectorParams.userId)
        println("Fetching metainfo (it may take about 5 minutes)...")
        MetainfoUpdateService(navigator.connectorParams, metaHolder).fetchMeta()
        println("Fetching metainfo - done")
        println("Project generation...")
        ProjectGeneratorService(navigator.artifactConstants, metaHolder).generate()
        println("Project generation - done")
        println("Jar generation...")
        JarGeneratorService(navigator.artifactConstants, metaHolder).generate()
        println("Jar generation - done")
    }
}