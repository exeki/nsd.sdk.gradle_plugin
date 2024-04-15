package ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.services

import com.fasterxml.jackson.databind.ObjectMapper
import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.client.dto.MetaClassWrapperDto
import java.io.File

class MetainfoHolder private constructor(val installationId: String) {

    val file: File

    var objectMapper: ObjectMapper = ObjectMapper()

    private var metaClasses: MutableList<MetaClassWrapperDto>

    init {
        val id = installationId.lowercase()
        val filepath = "${System.getProperty("user.home")}\\nsd_sdk\\data\\$id\\sdk_meta_store.json"
        this.file = File(filepath)
        val type =
            objectMapper.typeFactory.constructCollectionType(MutableList::class.java, MetaClassWrapperDto::class.java)
        this.metaClasses = if (file.exists()) objectMapper.readValue(file, type)
        else mutableListOf()
    }

    companion object {
        var instance: MetainfoHolder? = null

        @JvmStatic
        fun getInstance(installationId: String): MetainfoHolder {
            if (instance == null) instance = MetainfoHolder(installationId)
            return instance!!
        }

    }


    fun addAll(metaClasses: Collection<MetaClassWrapperDto>) {
        metaClasses.forEach { add(it) }
    }

    fun add(metaClass: MetaClassWrapperDto) {
        val existed = metaClasses.find { it.fullCode == metaClass.fullCode }
        if (existed != null) metaClasses[metaClasses.indexOf(existed)] = metaClass
        else metaClasses.add(metaClass)
    }

    fun setAll(metaClasses: Collection<MetaClassWrapperDto>) {
        this.metaClasses = metaClasses.toMutableList()
    }

    fun writeToFile() {
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        file.writeText(objectMapper.writeValueAsString(this.metaClasses))
    }

    fun getAll(): List<MetaClassWrapperDto> {
        val list = mutableListOf<MetaClassWrapperDto>()
        list.addAll(this.metaClasses)
        return list.toList()
    }

    @Suppress("SENSELESS_COMPARISON")
    fun getByCode(code: String): MetaClassWrapperDto? {
        if (code == null) return null
        return this.metaClasses.find { it.fullCode == code }
    }

}