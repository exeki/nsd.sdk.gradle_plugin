package ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.client.nsd_connector

import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.util.EntityUtils
import ru.kazantsev.nsd.basic_api_connector.Connector
import ru.kazantsev.nsd.basic_api_connector.ConnectorParams
import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.client.dto.MetaClassWrapperDto


/**
 * Коннектор к NSD
 */
class SdkApiConnector(params: ConnectorParams) : Connector(params) {

    private val moduleBase: String = "modules.sdkController."
    private val paramsConst: String = "request,response,user"

    /**
     * Получить информацию о метаклассе
     * @param metaClassCode код метакласса, инфа по которому нужна
     * @return dto с информацией
     */
    fun getMetaClassInfo(metaClassCode: String): MetaClassWrapperDto {
        val methodName = "getMetaClassInfo"
        val response: CloseableHttpResponse = this.execGet(
            moduleBase + methodName,
            paramsConst,
            mapOf("meta" to metaClassCode)
        )
        val body: String = EntityUtils.toString(response.entity)
        return this.objectMapper.readValue(body, MetaClassWrapperDto::class.java)
    }

    fun getMetaClassBranchInfo(metaClassCode: String): List<MetaClassWrapperDto> {
        val methodName = "getMetaClassBranchInfo"
        val response: CloseableHttpResponse = this.execGet(
            moduleBase + methodName,
            paramsConst,
            mapOf("meta" to metaClassCode)
        )
        val body: String = EntityUtils.toString(response.entity)
        return this.objectMapper.readValue(body, objectMapper.typeFactory.constructCollectionType(List::class.java, MetaClassWrapperDto::class.java))
    }

    fun getMetaClassBranchesInfo(metaClassCodes: Collection<String>): List<MetaClassWrapperDto> {
        val methodName = "getMetaClassBranchesInfo"
        val response: CloseableHttpResponse = this.execGet(
            moduleBase + methodName,
            paramsConst,
            mapOf("metas" to metaClassCodes.joinToString(","))
        )
        val body: String = EntityUtils.toString(response.entity)
        return this.objectMapper.readValue(body, objectMapper.typeFactory.constructCollectionType(List::class.java, MetaClassWrapperDto::class.java))
    }

}