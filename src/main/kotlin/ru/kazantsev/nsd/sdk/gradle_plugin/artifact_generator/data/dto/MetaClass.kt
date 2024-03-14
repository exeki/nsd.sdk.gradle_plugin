package ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.data.dto

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.table.DatabaseTable
import java.util.*

@DatabaseTable(tableName = "metaclasses")
class MetaClass {
    @DatabaseField(generatedId = true)
    var id: Long = 0

    @DatabaseField
    var title: String = ""

    @DatabaseField
    var classCode: String = ""

    @DatabaseField
    var caseCode: String? = ""

    @DatabaseField
    var fullCode: String = ""

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    var parent: MetaClass? = null

    @ForeignCollectionField(eager = false)
    var children: Collection<MetaClass> = mutableListOf();

    @ForeignCollectionField(eager = false)
    var attributes: Collection<Attribute> = mutableListOf();

    @ForeignCollectionField(eager = false)
    var attributeGroups: Collection<AttributeGroup> = mutableListOf();

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    var installation: Installation = Installation()

    @DatabaseField
    var lastUpdateDate: Date = Date()

    @DatabaseField(dataType = DataType.LONG_STRING)
    var description : String? = null

    @DatabaseField
    var hasResponsible : Boolean = false

    @DatabaseField
    var hasWorkflow  : Boolean = false

    @DatabaseField
    var hardcoded : Boolean = false

}