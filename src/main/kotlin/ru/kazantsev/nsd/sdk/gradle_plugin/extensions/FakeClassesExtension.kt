package ru.kazantsev.nsd.sdk.gradle_plugin.extensions

import org.gradle.api.Project
import ru.kazantsev.nsd.basic_api_connector.ConnectorParams
import ru.kazantsev.nsd.sdk.artifact_generator.ArtifactConstants
import ru.kazantsev.nsd.sdk.data.DbAccess

/**
 * Расширение, отвечающее за генерацию классов
 */
open class FakeClassesExtension {

    /**
     * Путь до конфигурационного файла, из которого нужно считать данные для подклчюения
     */
    protected var connectorParamsPath: String? = null
    protected var needToGenerate: Boolean = false
    protected var artifactConstants: ArtifactConstants? = null
    protected var connectorParams: ConnectorParams? = null
    protected var db: DbAccess? = null

    /**
     * Будут ли подключены генерация и подключение
     * артефакта с фейковыми классами
     */
    fun needToGenerate(): Boolean {
        return needToGenerate
    }

    /**
     * Целевое наименование артефакта для полдключения
     */
    fun getTargetArtifactName(): String? {
        return if (this.artifactConstants != null) {
            val group = this.artifactConstants!!.targetArtifactGroup
            val name = this.artifactConstants!!.targetArtifactName
            val version = this.artifactConstants!!.targetArtifactVersion
            "$group:$name:$version"
        } else {
            null
        }
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
        this.connectorParams = if (connectorParamsPath == null) {
            ConnectorParams.byConfigFile(installationId)
        } else {
            ConnectorParams.byConfigFileInPath(installationId, connectorParamsPath)
        }

        this.artifactConstants = ArtifactConstants(installationId)
        this.connectorParams = ConnectorParams.byConfigFile(installationId)
        this.db = DbAccess.createDefaultByInstallationId(installationId)
        this.needToGenerate = true
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
        this.needToGenerate = true
        this.artifactConstants = ArtifactConstants(installationId)
        this.db = DbAccess.createDefaultByInstallationId(installationId)
        this.connectorParams = ConnectorParams(
            installationId,
            scheme,
            host,
            accessKey,
            ignoreSLL
        )
    }
}