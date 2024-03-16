package ru.kazantsev.nsd.sdk.gradle_plugin.extensions

import org.gradle.api.Project
import ru.kazantsev.nsd.basic_api_connector.ConnectorParams
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.ArtifactConstants
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.JarGeneratorService
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.ProjectGeneratorService
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.client.MetainfoUpdateService
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.data.DbAccess
import ru.kazantsev.nsd.sdk.gradle_plugin.services.SingletonNavigatorService
import java.io.File

/**
 * Расширение, отвечающее за генерацию классов
 */
open class FakeClassesExtension(protected val project: Project) {

    var connectorParams: ConnectorParams? = null

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
    private fun getTargetArtifactId(): String {
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
        this.connectorParams = if (connectorParamsPath == null) ConnectorParams.byConfigFile(installationId)
        else ConnectorParams.byConfigFileInPath(installationId, connectorParamsPath)

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

        val jarExists: Boolean = File(localMavenPath).exists()
        println("")
        println("NSD SDK FAKE CLASSES")
        val targetJarName = "${this.artifactConstants!!.targetArtifactName}-${this.artifactConstants!!.targetArtifactVersion}.jar"
        if (!jarExists) {
            println("Fake classes jar \"$targetJarName\" not exists in path \"$localMavenPath\"")
            this.generateDependency()
            println("Fake classes jar file generation is complete in maven local repository. Connect it to project by adding this id to the dependencies:")
            println(this.getTargetArtifactId())
        } else {
            val dep = project.configurations.getByName("compileClasspath").find { it.name == targetJarName}
            if(dep == null) {
                println("Fake classes jar file already exists in maven local repository. Connect it to project by adding this id to the dependencies:")
                println(this.getTargetArtifactId())
            } else {
                println("Fake classes added")
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
                val metainfoService = SingletonNavigatorService.metainfoService!!
                metainfoService.fakeClassesDependencyAdded = true
                metainfoService.fakeClassesMetainfoClassName =
                    "${this.artifactConstants!!.generatedMetaClassPackage}.${this.artifactConstants!!.generatedMetaClassName}"
                metainfoService.fakeClassesArtifactName = getTargetArtifactId()
            }
        }
    }

    protected fun generateTargetClasses(){
        if (this.artifactConstants == null) throw RuntimeException("Cant find artifactConstants")
        if (this.connectorParams == null) throw RuntimeException("Cant find connectorParams")
        if (this.targetMeta == null) throw RuntimeException("Please specify the target metaclass codes")
        if (this.targetMeta!!.isEmpty()) throw RuntimeException("Please specify the target metaclass codes")
        val db = DbAccess.createDefaultByInstallationId(this.connectorParams!!.userId)
        try {
            println("Fetching metainfo...")
            MetainfoUpdateService(this.connectorParams!!, db).fetchMeta(this.targetMeta!!)
            println("Fetching metainfo - done")
            println("Project generation...")
            ProjectGeneratorService(this.artifactConstants!!, db).generate()
            println("Project generation - done")
            println("Jar generation...")
            JarGeneratorService(this.artifactConstants!!, db).generate()
            println("Jar generation - done")
        } catch (e: Exception) {
            throw e
        } finally {
            db.connection.close()
        }
    }

    protected fun generateDependency() {
        if (this.artifactConstants == null) throw RuntimeException("Cant find artifactConstants")
        if (this.connectorParams == null) throw RuntimeException("Cant find connectorParams")
        val db = DbAccess.createDefaultByInstallationId(this.connectorParams!!.userId)
        try {
            println("Fetching metainfo (it may take about 5 minutes)...")
            MetainfoUpdateService(this.connectorParams!!, db).fetchMeta()
            println("Fetching metainfo - done")
            println("Project generation...")
            ProjectGeneratorService(this.artifactConstants!!, db).generate()
            println("Project generation - done")
            println("Jar generation...")
            JarGeneratorService(this.artifactConstants!!, db).generate()
            println("Jar generation - done")
        } catch (e: Exception) {
            throw e
        } finally {
            db.connection.close()
        }
    }
}