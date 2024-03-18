package ru.kazantsev.nsd.sdk.gradle_plugin.services

import org.gradle.api.Project
import ru.kazantsev.nsd.basic_api_connector.ConnectorParams
import ru.kazantsev.nsd.sdk.gradle_plugin.Extension
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.ArtifactConstants

class NavigatorService(val project: Project) {
    companion object {
        var instance: NavigatorService? = null
    }

    init {
        instance = this
    }

    val codeReviserService: CodeReviserService = CodeReviserService(this)
    val fakeClassesService: FakeClassesService = FakeClassesService(this)
    val dependencyService: DependencyService = DependencyService(this)
    val metainfoService: MetainfoService = MetainfoService(this)
    val codeRunnerService: CodeRunnerService = CodeRunnerService(this)
    val afterEvaluateService: AfterEvaluateService = AfterEvaluateService(this)
    val sourceSetsService: SourceSetsService = SourceSetsService(this)
    val extension: Extension = project.extensions.create(
        "sdk",
        Extension::class.java,
        this
    )

    private lateinit var _installationId: String
    val installationId: String
        get() {
            checkInstallationIsSpecifiedElseThrow()
            return this._installationId
        }

    private lateinit var _artifactConstants: ArtifactConstants
    val artifactConstants: ArtifactConstants
        get() {
            checkInstallationIsSpecifiedElseThrow()
            return this._artifactConstants
        }

    private lateinit var _connectorParams: ConnectorParams
    val connectorParams: ConnectorParams
        get() {
            checkInstallationIsSpecifiedElseThrow()
            return this._connectorParams
        }

    var installationIsSpecified: Boolean = false
        private set

    fun checkInstallationIsSpecifiedElseThrow() {
        if (!installationIsSpecified) throw RuntimeException("Please specify the installation ID")
    }

    fun setInstallation(
        installationId: String
    ) {
        this.installationIsSpecified = true
        this._installationId = installationId
        this._connectorParams = ConnectorParams.byConfigFile(installationId)
        this._artifactConstants = ArtifactConstants(installationId)
    }

    fun setInstallation(
        installationId: String,
        connectorParamsPath: String
    ) {
        this.installationIsSpecified = true
        this._installationId = installationId
        this._connectorParams = ConnectorParams.byConfigFileInPath(installationId, connectorParamsPath)
        this._artifactConstants = ArtifactConstants(installationId)
    }

    fun setInstallation(
        installationId: String,
        scheme: String,
        host: String,
        accessKey: String,
        ignoreSLL: Boolean
    ) {
        this.installationIsSpecified = true
        this._artifactConstants = ArtifactConstants(installationId)
        this._installationId = installationId
        this._connectorParams = ConnectorParams(
            installationId,
            scheme,
            host,
            accessKey,
            ignoreSLL
        )
    }
}