package ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.data.dto

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.table.DatabaseTable
import java.util.Date

@DatabaseTable(tableName = "installations")
class Installation {

    @DatabaseField(unique = true)
    var host: String = ""

    @DatabaseField(generatedId = true, unique = true)
    var id: Long = 0

    @ForeignCollectionField(eager = false)
    var metaClasses: Collection<MetaClass> = mutableListOf()

    @DatabaseField
    var lastUpdateDate: Date = Date()

    @DatabaseField
    var userId: String = ""
}