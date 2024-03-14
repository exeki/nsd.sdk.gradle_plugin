package tests.data

import com.j256.ormlite.stmt.PreparedQuery
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.data.dto.Attribute
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.data.dto.AttributeType

import static tests.TestUtils.*

PreparedQuery<Attribute> query = db.attributeDao.queryBuilder().where().eq("type", AttributeType.OBJECT).prepare()
Attribute attr =  db.attributeDao.queryForFirst(query)
logger.info(attr.relatedMetaClass)