package ru.kazantsev.nsd.sdk.gradle_plugin.extensions

import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import ru.kazantsev.nsd.basic_api_connector.ConnectorParams
import ru.kazantsev.nsd.sdk.artifact_generator.ArtifactConstants
import ru.kazantsev.nsd.sdk.artifact_generator.JarGeneratorService
import ru.kazantsev.nsd.sdk.artifact_generator.client.MetainfoUpdateService
import ru.kazantsev.nsd.sdk.artifact_generator.data.DbAccess
import ru.kazantsev.nsd.sdk.gradle_plugin.services.SingletonNavigatorService
import java.io.File

/**
 * Расширение, отвечающее за генерацию классов
 */
open class FakeClassesExtension(protected val project: Project) {

    private var connectorParams: ConnectorParams? = null

    var targetMeta : Set<String>? = null

    /**
     * Перечень сгенерированных классов
     */
    var generatedClassNames: List<String>? = null
        private set

    private var artifactConstants: ArtifactConstants? = null

    protected var installationId: String? = null
        private set


    /**
     * Путь до конфигурационного файла, из которого нужно считать данные для подклчюения
     */
    private var connectorParamsPath: String? = null

    /**
     * Целевое наименование артефакта для полдключения
     * @return идентификатор артефакта,
     * по которому можно подключить сгенерированный артефакт
     */
    private fun getTargetArtifactName(): String {
        if (this.artifactConstants == null) throw RuntimeException("Cant find artifactConstants")
        val group = artifactConstants!!.targetArtifactGroup
        val name = artifactConstants!!.targetArtifactName
        val version = artifactConstants!!.targetArtifactVersion
        return "$group:$name:$version"
    }

    /**
     * Указывает необходимость сгенерировать фейковые классы.
     * Расширение затянет метаинформацию из NSD создаст артефакт с ними.
     * Артефакт автоматически будет поключен к проекту.
     * Параметры для подключения будут затянуты из указанной в
     * конфигурационном файле по пути connectorParamsPath.
     * По умолчанию user.home/nsd_sdk/conf/nsd_connector_params.json
     * @param installationId идентификатор инсталляции
     */
    fun generate(installationId: String) {
        this.installationId = installationId
        this.artifactConstants = ArtifactConstants(installationId)
        this.connectorParams = if (connectorParamsPath == null) {
            ConnectorParams.byConfigFile(installationId)
        } else {
            ConnectorParams.byConfigFileInPath(installationId, connectorParamsPath)
        }
        generateAndAddDependency()
    }


    /**
     * Указывает необходимость сгенерировать фейковые классы
     * расширение затянет метаинформацию из NSD создаст артефакт с ними.
     * Артефакт автоматически будет поключен к проекту
     * @param installationId идентификатор инсталляции
     * @param scheme схема, по которой обращаться к инсталляции
     * @param host хост инсталляции
     * @param accessKey ключ доступа для получения метаинформации
     * @param ignoreSLL необходимость игнорировать SSL
     */
    fun generate(
        installationId: String,
        scheme: String,
        host: String,
        accessKey: String,
        ignoreSLL: Boolean
    ) {
        this.artifactConstants = ArtifactConstants(installationId)
        this.connectorParams = ConnectorParams(
            installationId,
            scheme,
            host,
            accessKey,
            ignoreSLL
        )
        generateAndAddDependency()
    }


    /**
     * Сгенерировать артефакт.
     * Предварительно должны быть заданы поля connectorParams и artifactConstants,
     * иначе исключение
     */
    protected fun generateAndAddDependency() {
        if (this.artifactConstants == null) throw RuntimeException("Cant find artifactConstants")
        if (this.connectorParams == null) throw RuntimeException("Cant find connectorParams")
        var localMavenPath = "${System.getProperty("user.home")}\\.m2\\repository"
        val localMavenRepository = File(localMavenPath)
        if (!localMavenRepository.exists()) throw RuntimeException("Local maven repo not exists")
        localMavenPath = localMavenPath
            .plus("\\")
            .plus(artifactConstants!!.targetArtifactGroup.split('.').joinToString("\\"))
            .plus("\\")
            .plus(artifactConstants!!.targetArtifactName.split('.').joinToString("\\"))
            .plus("\\")
            .plus(artifactConstants!!.targetArtifactVersion)

        val exists: Boolean = File(localMavenPath).exists()
        println("FAKE CLASSES")
        println("dependency ${this.getTargetArtifactName()}")
        println("in path: $localMavenPath")
        println("exists: $exists")
        if (!exists) generateDependency()
        project.dependencies.add("implementation", this.getTargetArtifactName())
        project.task("regenerate_all_fake_classes") {
            it.group = "nsd_sdk"
            it.description = "Regenerate fake classes dependency by fetching full metainfo from NSD installation"
            it.doLast {
                this.generateDependency()
            }
        }
        project.task("regenerate_target_fake_classes") {
            it.group = "nsd_sdk"
            it.description = "Regenerate some fake classes in dependency by target fetching metainfo from NSD installation"
            it.doLast {
                this.generateTargetClasses()
            }
        }
        println("writing metainfo...")
        val metainfoService = SingletonNavigatorService.metainfoService!!
        metainfoService.fakeClassesDependencyAdded = true
        metainfoService.fakeClassesMetainfoClassName =
            "${this.artifactConstants!!.generatedMetaClassPackage}.${this.artifactConstants!!.generatedMetaClassName}"
        metainfoService.fakeClassesArtifactName = getTargetArtifactName()
        println("metainfo writing - done")
    }

    protected fun generateTargetClasses(){
        if (this.artifactConstants == null) throw RuntimeException("Cant find artifactConstants")
        if (this.connectorParams == null) throw RuntimeException("Cant find connectorParams")
        if (this.targetMeta == null) throw RuntimeException("Please specify the target metaclass codes")
        if (this.targetMeta!!.isEmpty()) throw RuntimeException("Please specify the target metaclass codes")

        println("generating...")
        val db = DbAccess.createDefaultByInstallationId(this.connectorParams!!.userId)
        try {
            MetainfoUpdateService(this.connectorParams!!, db).fetchMeta(this.targetMeta!!)
            JarGeneratorService(this.artifactConstants!!, db).generate(this.connectorParams!!.userId)
            project.dependencies.add("implementation", this.getTargetArtifactName())
            println("dependency added")
        } catch (e: Exception) {
            throw e
        } finally {
            db.connection.close()
        }
        println("generation - done")
    }

    protected fun generateDependency() {
        if (this.artifactConstants == null) throw RuntimeException("Cant find artifactConstants")
        if (this.connectorParams == null) throw RuntimeException("Cant find connectorParams")
        println("generating...")
        val db = DbAccess.createDefaultByInstallationId(this.connectorParams!!.userId)
        try {
            MetainfoUpdateService(this.connectorParams!!, db).fetchMeta()
            JarGeneratorService(this.artifactConstants!!, db).generate(this.connectorParams!!.userId)
            project.dependencies.add("implementation", this.getTargetArtifactName())
            println("dependency added")
        } catch (e: Exception) {
            throw e
        } finally {
            db.connection.close()
        }
        println("generation - done")
    }
}