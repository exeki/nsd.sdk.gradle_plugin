package ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.services.src_generation

import com.squareup.javapoet.*
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.jsoup.Jsoup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.ArtifactConstants
import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.services.MetainfoHolder
import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.client.dto.AttributeDto
import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.client.dto.MetaClassWrapperDto
import ru.naumen.common.shared.utils.DateTimeInterval
import ru.naumen.common.shared.utils.IHyperlink
import ru.naumen.core.server.script.spi.AggregateContainerWrapper
import ru.naumen.core.server.script.spi.ScriptDate
import ru.naumen.core.server.script.spi.ScriptDtOList
import ru.naumen.core.server.script.spi.ScriptDtOSet
import ru.naumen.core.shared.timer.BackTimerDto
import ru.naumen.core.shared.timer.TimerDto
import ru.naumen.metainfo.shared.IClassFqn
import ru.naumen.metainfo.shared.elements.sec.ISGroup
import java.lang.Exception
import java.lang.reflect.Type
import javax.lang.model.element.Modifier
import ru.naumen.common.shared.utils.SourceCode

/**
 * Служба для генерации прототипов полей
 */
class FieldGeneratorService(private var artifactConstants: ArtifactConstants, private val metaHolder: MetainfoHolder) {

    companion object {
        val FORBIDDEN_FIELD_NAMES = setOf("private", "protected", "interface", "class", "int", "if", "boll")
    }

    private val logger: Logger = LoggerFactory.getLogger(FieldGeneratorService::class.java)

    /**
     * Генерирует прототип поля без каких либо дополнительных элементов
     * @param type тип генерируемого поля
     * @param attr атрибут, значения которого будет задействованы
     * @return прототип поля без каких либо дополнительных элементов
     */
    private fun getFieldProto(type: Type, attr: AttributeDto): FieldSpec.Builder {
        val clazz = when (type) {
            Long::class.java -> ClassName.get("java.lang", "Long")
            Boolean::class.java -> ClassName.get("java.lang", "Boolean")
            Double::class.java -> ClassName.get("java.lang", "Double")
            else -> ClassName.get(type)
        }
        return FieldSpec.builder(
            clazz,
            attr.code,
            Modifier.PUBLIC,
            Modifier.FINAL
        )
    }

    /**
     * Генерирует прототип поля без каких либо дополнительных элементов
     * @param meta код метакласса, фейковый класс которого будет использован как тип
     * @param attr атрибут, значения которого будет задействованы
     * @return прототип поля без каких либо дополнительных элементов
     */
    private fun getFieldProto(meta: String, attr: AttributeDto): FieldSpec.Builder {
        val className: ClassName = if (metaHolder.getByCode(meta) != null) {
            ClassName.get(artifactConstants.packageName, artifactConstants.getClassNameFromMetacode(meta))
        } else {
            ClassName.get(Object::class.java)
        }
        return FieldSpec.builder(
            className,
            attr.code,
            Modifier.PUBLIC,
            Modifier.FINAL
        )
    }

    /**
     * Генерирует прототип поля с дженеразилированным типом без каких либо дополнительных элементов
     * @param type класс поля
     * @param genericType класс, который дженерализирован класс поля
     * @param attr атрибут, значения которого будет задействованы
     * @return прототип поля без каких либо дополнительных элементов
     */
    private fun getGenericFieldProto(type: Class<*>, genericType: Class<*>, attr: AttributeDto): FieldSpec.Builder {
        return FieldSpec.builder(
            ParameterizedTypeName.get(
                ClassName.get(type),
                ClassName.get(genericType)
            ),
            attr.code,
            Modifier.PUBLIC,
            Modifier.FINAL
        )
    }

    /**
     * Генерирует прототип поля с дженерализированным типом без каких либо дополнительных элементов
     * классом, которым дженерализирован класс поля, будет фейковый класс созданный из метакласса
     * @param type класс поля
     * @param attr атрибут, значения которого будет задействованы
     * @return прототип поля без каких либо дополнительных элементов
     */
    private fun getGenericFieldProto(type: Class<*>, attr: AttributeDto): FieldSpec.Builder {
        val className: ClassName =
            if (metaHolder.getByCode(attr.relatedMetaClass!!) != null) {
                ClassName.get(
                    artifactConstants.packageName,
                    artifactConstants.getClassNameFromMetacode(attr.relatedMetaClass!!)
                )
            } else ClassName.get(Object::class.java)
        return FieldSpec.builder(
            ParameterizedTypeName.get(ClassName.get(type), className),
            attr.code,
            Modifier.PUBLIC,
            Modifier.FINAL
        )
    }

    /**
     * Заменяет все запрещенные в html символы
     */
    private fun replaceHtmlSymbols(str: String): String {
        return str
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
            .replace("$", "")
    }

    /**
     * Генерирует javaDoc для поля
     * @param attr аттрибут, на основании которого генерируется поле и javaDoc
     * @return прототип CodeBlock.Builder
     */
    private fun generateFieldJavaDocProto(attr: AttributeDto): CodeBlock.Builder {
        val javaDocProto = CodeBlock.builder()
            .add("<strong>Наименование: </strong>${replaceHtmlSymbols(attr.title)};<br>\n")
            .add("<strong>Код: </strong>${attr.code};<br>\n")
            .add("<strong>Тип: </strong>${AttributeType.getByCode(attr.type).getTitle()};<br>\n")
        if (attr.relatedMetaClass != null) javaDocProto.add("<strong>Связанный метакласс: </strong>${attr.relatedMetaClass};<br>\n")
        javaDocProto
            .add("<strong>Обязателен: </strong>${attr.required};<br>\n")
            .add("<strong>Обязателен в интерфейсе: </strong>${attr.requiredInInterface};<br>\n")
            .add("<strong>Уникальный: </strong>${attr.unique};<br>\n")
            .add("<strong>Системный: </strong>${attr.hardcoded};<br>\n")
        if (attr.description != null && attr.description!!.isNotEmpty()) {
            val clearDescription: String = Jsoup.parse(attr.description!!).text()
            if (clearDescription.isNotEmpty()) javaDocProto.add(
                "<strong>Описание: </strong> ${
                    replaceHtmlSymbols(
                        clearDescription
                    )
                };"
            )
        }
        return javaDocProto
    }

    /**
     * Генерирует прототип поля, который к установке в класс
     * @param attr аттрибут, на основании которого генерируется поле
     * @return прототип поля
     */
    fun generateFieldProto(attr: AttributeDto, metaClass : MetaClassWrapperDto): FieldSpec.Builder? {
        logger.debug("Generating field ${attr.code} with type ${attr.type}...")
        logger.debug("Creating proto...")
        if(attr.code in FORBIDDEN_FIELD_NAMES) {
            logger.error("Cant generate field named \"${attr.code}\" for metaClass \"${metaClass.fullCode}\", because it has a forbidden name.")
            return null
        }
        var fieldProto: FieldSpec.Builder? = null
        val type = AttributeType.getByCode(attr.type)
        try {
            fieldProto = when (type) {
                AttributeType.DOUBLE -> getFieldProto(Double::class.java, attr)
                AttributeType.AGGREGATE -> getFieldProto(AggregateContainerWrapper::class.java, attr)
                AttributeType.BACK_BO_LINKS -> getGenericFieldProto(
                    ScriptDtOList::class.java,
                    attr
                ) //getListAttrProto(attr.relatedMetaClass!!, attr)
                AttributeType.BACK_TIMER -> getFieldProto(BackTimerDto::class.java, attr)
                AttributeType.BOOL -> getFieldProto(Boolean::class.java, attr)
                AttributeType.BO_LINKS -> getGenericFieldProto(
                    ScriptDtOSet::class.java,
                    attr
                )//getListAttrProto(attr.relatedMetaClass!!, attr)
                AttributeType.CASE_LIST -> getGenericFieldProto(
                    ScriptDtOList::class.java,
                    IClassFqn::class.java,
                    attr
                ) //getAttrProto(Object::class.java, attr)
                AttributeType.CATALOG_ITEM -> getFieldProto(attr.relatedMetaClass!!, attr)
                AttributeType.CATALOG_ITEM_SET -> getGenericFieldProto(
                    ScriptDtOSet::class.java,
                    attr
                )//getListAttrProto(attr.relatedMetaClass!!, attr)
                AttributeType.DATE -> getFieldProto(ScriptDate::class.java, attr)
                AttributeType.DATE_TIME -> getFieldProto(ScriptDate::class.java, attr)
                AttributeType.DT_INTERVAL -> getFieldProto(DateTimeInterval::class.java, attr)
                AttributeType.INTEGER -> getFieldProto(Long::class.java, attr)
                AttributeType.META_CLASS -> getFieldProto(IClassFqn::class.java, attr)
                AttributeType.OBJECT -> getFieldProto(attr.relatedMetaClass!!, attr)
                AttributeType.STATE -> getFieldProto(String::class.java, attr)
                AttributeType.RESPONSIBLE -> getFieldProto(AggregateContainerWrapper::class.java, attr)
                AttributeType.STRING -> getFieldProto(String::class.java, attr)
                AttributeType.TEXT -> getFieldProto(String::class.java, attr)
                AttributeType.TIMER -> getFieldProto(TimerDto::class.java, attr)
                AttributeType.HYPERLINK -> getFieldProto(IHyperlink::class.java, attr)
                AttributeType.RICH_TEXT -> getFieldProto(String::class.java, attr)
                AttributeType.SEC_GROUPS -> getGenericFieldProto(
                    ScriptDtOList::class.java,
                    ISGroup::class.java,
                    attr
                )//getListAttrProto(ISGroup::class.java, attr)
                AttributeType.FILE -> FieldSpec.builder(
                    ParameterizedTypeName.get(
                        ClassName.get(ScriptDtOList::class.java),
                        ClassName.get(
                            artifactConstants.packageName,
                            artifactConstants.getClassNameFromMetacode("file")
                        )
                    ),
                    attr.code,
                    Modifier.PUBLIC,
                    Modifier.FINAL
                )

                AttributeType.LICENSE -> getFieldProto(String::class.java, attr)
                AttributeType.FILE_CONTENT -> getFieldProto(ByteArray::class.java, attr)
                AttributeType.UNKNOWN -> getFieldProto(Object::class.java, attr)
                AttributeType.UUID -> getFieldProto(String::class.java, attr)
                AttributeType.COLOR -> getFieldProto(String::class.java, attr)
                AttributeType.COMMENT_OBJECTS -> getFieldProto(Object::class.java, attr)
                AttributeType.RECORD_TYPE -> getFieldProto(Object::class.java, attr)
                AttributeType.MULTI_CLASS_OBJECTS -> getFieldProto(Object::class.java, attr)
                AttributeType.SYSTEM_OBJECT -> getFieldProto(Object::class.java, attr)
                AttributeType.SYSTEM_STATE -> getFieldProto(Object::class.java, attr)
                AttributeType.LOCALIZED_TEXT -> getFieldProto(String::class.java, attr)
                AttributeType.SOURCE_CODE -> getFieldProto(SourceCode::class.java, attr)
                AttributeType.LINKED_CLASSES -> getFieldProto(Object::class.java, attr)
                AttributeType.METRIC_SEVERITY -> getFieldProto(Object::class.java, attr)
                AttributeType.METRIC_VALUE -> getFieldProto(Object::class.java, attr)
                AttributeType.EXECUTION_RESULT -> getFieldProto(Object::class.java, attr)
            }
            if (fieldProto != null) {
                fieldProto.initializer("null")
                logger.debug("Creating proto - done")
                logger.debug("Adding annotations...")
                if (attr.required) fieldProto.addAnnotation(NotNull::class.java)
                else fieldProto.addAnnotation(Nullable::class.java)
                logger.debug("Adding annotations - done")
                logger.debug("Creating javaDoc...")
                fieldProto.addJavadoc(generateFieldJavaDocProto(attr).build())
                logger.debug("Creating javaDoc - done")
            }
        } catch (e: Exception) {
            logger.error("Cant generate field named \"${attr.code}\" for metaClass \"${metaClass.fullCode}\": ${e.javaClass.simpleName} ${e.message}")
        }
        logger.debug("Field \"${attr.code}\" generation done")
        return fieldProto
    }
}