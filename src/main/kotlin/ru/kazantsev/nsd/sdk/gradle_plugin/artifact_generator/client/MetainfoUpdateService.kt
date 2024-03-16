package ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.client

import org.slf4j.LoggerFactory
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.data.DbAccess
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.client.writers.AttributeGroupWriter
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.client.writers.AttributeWriter
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.client.writers.InstallationWriter
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.client.writers.MetaClassWriter
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.client.nsd_connector.FakeApiConnector
import ru.kazantsev.nsd.basic_api_connector.ConnectorParams
import ru.kazantsev.nsd.basic_api_connector.HttpException
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.data.dto.Installation
import java.util.*

//TODO сделать удаление и архивацию метаклассов и полей
/**
 * Служба, направленная на получение и помещение в хранилище метаинформации из инсталляции
 * @param connectorParams  параметры коннектора, из них будет извлечена и сохранена инсталляция
 * @param db экземпляр доступа к базе данных
 */
class MetainfoUpdateService(
    connectorParams: ConnectorParams,
    private val db: DbAccess
) {

    companion object {
        private val DEFAULT_CODES: MutableSet<String> = mutableSetOf("catalogItem", "abstractSysObj", "abstractBO")
    }

    private var irrelevanceTime: Int = 0
    private val logger = LoggerFactory.getLogger(javaClass)
    private val connector: FakeApiConnector
    private val metaWriter = MetaClassWriter(db)
    private val attrWriter = AttributeWriter(db)
    private val groupWriter = AttributeGroupWriter(db)
    private val metaClassCodes: MutableSet<String> = mutableSetOf()
    private val installation: Installation = InstallationWriter(db).createOrUpdate(connectorParams)
    private val fetchedMeta: MutableSet<String> = mutableSetOf()

    init {
        this.connector = FakeApiConnector(connectorParams)
        this.connector.setDebugLogging(false)
    }

    /**
     * Установить отсечку по доте последнего обновления метакласса
     * если метакласс обновлен позже чем текущая дата минус указанное количество минут
     * то его обновление не произойдет
     * @param minutes количество минут для отсечки
     */
    fun setIrrelevanceTime(minutes: Int): MetainfoUpdateService {
        this.irrelevanceTime = minutes
        return this
    }

    /**
     * Затянуть и метакласс и все его типы
     * @param code код метакласса, который нужно запросить и записать
     */
    private fun fetchMetaClassBranch(code: String) {
        val existed = db.metaClassDao.queryForEq("fullCode", code).lastOrNull()
        if (existed != null) {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            calendar.add(Calendar.MINUTE, -irrelevanceTime)
            if (calendar.time < existed.lastUpdateDate) {
                logger.info("MetaClass ${existed.fullCode} already relevant")
                return
            }
        }
        try {
            val metaDto = connector.getMetaClassInfo(code)
            val meta = metaWriter.createOrUpdate(installation, metaDto)
            logger.info("MetaClass ${meta.fullCode} written")
            metaDto.attributes.forEach {
                val attr = attrWriter.createOrUpdate(meta, it)
                logger.debug("Attribute ${attr.code} written")
                if (it.relatedMetaClass != null && !metaClassCodes.contains(it.relatedMetaClass) && !fetchedMeta.contains(
                        it.relatedMetaClass
                    )
                ) {
                    logger.info("Adding another metaclass ${it.relatedMetaClass} to fetch from meta: $code, attr: ${attr.code}")
                    metaClassCodes.add(it.relatedMetaClass!!)
                }
            }
            metaDto.attributeGroups.forEach {
                val group = groupWriter.createOrUpdate(meta, it)
                logger.debug("Group ${group.code} written")
            }
            metaDto.children.forEach {
                fetchMetaClassBranch(it)
            }
        } catch (e: HttpException) {
            if(e.serverResponseStatus == 404) logger.debug("Metaclass \"$code\" not found: ${e.message}")
            else throw RuntimeException("Caught error while getting info about metaclass \"$code\": ${e.message}")
        } finally {
            fetchedMeta.add(code)
            metaClassCodes.remove(code)
        }
    }

    /**
     * Затянуть всю метаинформацию по инсталляции
     */
    fun fetchMeta() {
        metaClassCodes.addAll(DEFAULT_CODES)
        while (metaClassCodes.size != 0) {
            fetchMetaClassBranch(metaClassCodes.first())
        }
    }

    /**
     * Затянуть конкретную метаинформацию по инсталляции
     * @param targetMetaClassCodes дополнительные коды метаклассов
     */
    fun fetchMeta(targetMetaClassCodes: Set<String>) {
        fetchedMeta.addAll(db.metaClassDao.queryForEq("installation_id", installation.id).map { it.fullCode })
        metaClassCodes.addAll(targetMetaClassCodes)
        while (metaClassCodes.size != 0) {
            fetchMetaClassBranch(metaClassCodes.first())
        }
    }
}