package ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.data.dto

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "attribute_and_group_link")
class AttributeAndGroupLink() {
    @DatabaseField(generatedId = true)
    var id: Long = 0

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    var attribute: Attribute = Attribute()

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    var group: AttributeGroup = AttributeGroup()

    constructor(attribute: Attribute, attributeGroup: AttributeGroup) : this() {
        this.attribute = attribute
        this.group = attributeGroup
    }
}