package ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.services

import org.slf4j.LoggerFactory
import ru.kazantsev.nsd.basic_api_connector.ConnectorParams
import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.client.nsd_connector.SdkApiConnector
import java.util.*

//TODO сделать удаление и архивацию метаклассов и полей
/**
 * Служба, направленная на получение и помещение в хранилище метаинформации из инсталляции
 * @param connectorParams  параметры коннектора, из них будет извлечена и сохранена инсталляция
 * @param db экземпляр доступа к базе данных
 */
class MetainfoUpdateService(
    connectorParams: ConnectorParams,
    private val metaHolder: MetainfoHolder
) {

    companion object {
        private val DEFAULT_CODES: MutableSet<String> = mutableSetOf("catalogItem", "abstractSysObj", "abstractBO")
    }
    private val logger = LoggerFactory.getLogger(javaClass)
    private val connector: SdkApiConnector
    private val metaClassCodes: MutableSet<String> = mutableSetOf()
    private val fetchedMeta: MutableSet<String> = mutableSetOf()

    init {
        this.connector = SdkApiConnector(connectorParams)
        this.connector.setDebugLogging(false)
    }

    /**
     * Затянуть всю метаинформацию по инсталляции
     */
    fun fetchMeta() {
        metaClassCodes.addAll(DEFAULT_CODES)
        val metaDtos = connector.getMetaClassBranchesInfo(metaClassCodes)
        metaHolder.addAll(metaDtos)
        metaHolder.writeToFile()
    }

    /**
     * Затянуть конкретную метаинформацию по инсталляции
     * @param targetMetaClassCodes дополнительные коды метаклассов
     */
    fun fetchMeta(targetMetaClassCodes: Set<String>) {
        fetchedMeta.addAll(metaHolder.getAll().map{it.fullCode})
        if(fetchedMeta.size == 0) {
            fetchMeta()
        } else {
            metaClassCodes.addAll(targetMetaClassCodes)
            val metaDtos = connector.getMetaClassBranchesInfo(metaClassCodes)
            metaHolder.setAll(metaDtos)
            metaHolder.writeToFile()
        }
    }
}