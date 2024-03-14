package ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.data.dto

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "attributes")
class Attribute {

    @DatabaseField(generatedId = true)
    var id: Long = 0

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    var metaClass: MetaClass = MetaClass()

    @DatabaseField
    var code: String = ""

    @DatabaseField
    var title: String = ""

    @DatabaseField
    var type: AttributeType = AttributeType.OBJECT

    @DatabaseField
    var hardcoded: Boolean = false

    @DatabaseField
    var required: Boolean = false

    @DatabaseField
    var requiredInInterface: Boolean = false

    @DatabaseField
    var unique: Boolean = false

    @ForeignCollectionField(eager = false)
    var groups: Collection<AttributeAndGroupLink> = mutableListOf()

    @DatabaseField(dataType = DataType.LONG_STRING)
    var description: String? = null

    @DatabaseField
    var relatedMetaClass: String? = null

}