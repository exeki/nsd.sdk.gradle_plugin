package ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.client.writers

import ru.kazantsev.nsd.basic_api_connector.ConnectorParams
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.data.DbAccess
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.data.dto.Installation

import java.util.Date

/**
 * Служба, записывающая инсталляции в хранилище
 */
class InstallationWriter (private val db: DbAccess) {
    private val dao = db.installationDao
    fun createOrUpdate(connectorParams: ConnectorParams) : Installation {
        var inst: Installation? = dao.queryForEq("host", connectorParams.host).lastOrNull()
        if (inst == null) inst = Installation()
        inst.host = connectorParams.host
        inst.lastUpdateDate = Date()
        inst.userId = connectorParams.userId
        dao.createOrUpdate(inst)
        return inst
    }
}