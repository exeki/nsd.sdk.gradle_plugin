package ru.kazantsev.nsd.sdk.gradle_plugin.extensions

import org.gradle.api.Project
import ru.kazantsev.nsd.basic_api_connector.ConnectorParams
import ru.kazantsev.nsd.sdk.artifact_generator.ArtifactConstants
import ru.kazantsev.nsd.sdk.artifact_generator.JarGeneratorService
import ru.kazantsev.nsd.sdk.client.MetainfoUpdateService
import ru.kazantsev.nsd.sdk.data.DbAccess
import java.io.File
import java.net.URLClassLoader

/**
 * Расширение, отвечающее за генерацию классов
 */
open class FakeClassesExtension(protected val project: Project) {

    /**
     * Класс зависимости фейковых классов, в которо
     */
    private var fakeClassesMetainfoClassName : String? = null

    /**
     * Метод получения наименования сгенерированных классов
     */
    private val fakeClassesMetainfoClassMethodName = "getGeneratedClassNames"

    /**
     * Признак того, что зависимость добавлена
     */
    var fakeClassesDependencyAdded = false

    /**
     * Перечень сгенерированных классов
     */
    var generatedClassNames: List<String>? = null
        private set

    /**
     * Путь до конфигурационного файла, из которого нужно считать данные для подклчюения
     */
    private var connectorParamsPath: String? = null

    /**
     * Целевое наименование артефакта для полдключения
     * @param artifactConstants константы сгенерированного артефакта
     * @return идентификатор артефакта,
     * по которому можно подключить сгенерированный артефакт
     */
    private fun getTargetArtifactName(artifactConstants: ArtifactConstants): String {
        val group = artifactConstants.targetArtifactGroup
        val name = artifactConstants.targetArtifactName
        val version = artifactConstants.targetArtifactVersion
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
        val artifactConstants = ArtifactConstants(installationId)
        val connectorParams: ConnectorParams = if (connectorParamsPath == null) {
            ConnectorParams.byConfigFile(installationId)
        } else {
            ConnectorParams.byConfigFileInPath(installationId, connectorParamsPath)
        }
        generateDependency(connectorParams, artifactConstants)
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
        val artifactConstants = ArtifactConstants(installationId)
        val connectorParams = ConnectorParams(
            installationId,
            scheme,
            host,
            accessKey,
            ignoreSLL
        )
        generateDependency(connectorParams, artifactConstants)
    }

    /**
     * Сгенерировать артефакт
     * @param connectorParams параметры подключения
     * @param artifactConstants константы артфакта
     */
    protected fun generateDependency(
        connectorParams: ConnectorParams,
        artifactConstants: ArtifactConstants
    ) {
        var localMavenPath = "${System.getProperty("user.home")}\\.m2\\repository"
        val localMavenRepository = File(localMavenPath)
        if (!localMavenRepository.exists()) throw RuntimeException("Local maven repo not exists")
        localMavenPath = localMavenPath
            .plus("\\")
            .plus(artifactConstants.targetArtifactGroup.split('.').joinToString("\\"))
            .plus("\\")
            .plus(artifactConstants.targetArtifactName.split('.').joinToString("\\"))
            .plus("\\")
            .plus(artifactConstants.targetArtifactVersion)

        val exists: Boolean = File(localMavenPath).exists()
        println("in path: $localMavenPath")
        println("dependency ${this.getTargetArtifactName(artifactConstants)} exists: $exists")
        if (exists) {
            println("connecting...")
            project.dependencies.add(
                "implementation",
                this.getTargetArtifactName(artifactConstants)
            )
            println("dependency added")
        } else {
            println("generating...")
            val db = DbAccess.createDefaultByInstallationId(connectorParams.userId)
            try {
                println("connecting...")
                MetainfoUpdateService(connectorParams, db).fetchMeta()
                JarGeneratorService(artifactConstants, db).generate(connectorParams.userId)
                project.dependencies.add(
                    "implementation",
                    this.getTargetArtifactName(artifactConstants)
                )
                println("dependency added")
            } catch (e : Exception) {
                throw e
            } finally {
                db.connection.close()
            }
        }
        this.fakeClassesDependencyAdded = true
        this.fakeClassesMetainfoClassName = "${artifactConstants.generatedMetaClassPackage}.${artifactConstants.generatedMetaClassName}"
        val config = project.configurations.findByName("runtimeClasspath")
        val classLoader = URLClassLoader(config!!.files.map { it.toURI().toURL() }.toTypedArray())
        val cl = Class.forName(fakeClassesMetainfoClassName, false, classLoader)
        val declaredMethod = cl.getDeclaredMethod(fakeClassesMetainfoClassMethodName)
        this.generatedClassNames = declaredMethod.invoke(cl) as List<String>
    }
}