package ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.services.src_generation

import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import org.jsoup.Jsoup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.ArtifactConstants
import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.services.MetainfoHolder
import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.client.dto.MetaClassWrapperDto
import ru.naumen.common.shared.utils.ISProperties
import ru.naumen.core.server.script.spi.IScriptDtObject
import javax.lang.model.element.Modifier

/**
 * Служба, отвечающая за генерацию прототипов кода классов
 */
class ClassGeneratorService(private val artifactConstants: ArtifactConstants, private val metaHolder : MetainfoHolder) {

    val generatedClasses: MutableSet<String> = mutableSetOf()

    private val logger: Logger = LoggerFactory.getLogger(ClassGeneratorService::class.java)

    /**
     * Генерирует javaDoc для метакласса
     * @param metaClass метакласс для которого нужен javaDoc
     * @return заранее собранный CodeBlock.Builder
     */
    private fun generateClassJavaDocProto(metaClass: MetaClassWrapperDto): CodeBlock.Builder {

        val javaDocProto = CodeBlock.builder()
            .add("<strong>Наименование: </strong>${metaClass.title};<br>\n")
            .add("<strong>Код: </strong>${metaClass.fullCode.replace('$', artifactConstants.classDelimiter)};<br>\n")
            .add("<strong>Жизненный цикл: </strong>${metaClass.hasWorkflow};<br>\n")
            .add("<strong>Назначение ответственного: </strong>${metaClass.hasResponsible};<br>\n")
            .add("<strong>Системный: </strong>${metaClass.hardcoded};<br>\n")
        if (metaClass.parent != null)
            javaDocProto.add("<strong>Родитель: </strong>${metaClass.parent!!.replace('$', artifactConstants.classDelimiter)};<br>\n")
        if (metaClass.description != null && metaClass.description!!.isNotEmpty()) {
            var clearDescription: String = Jsoup.parse(metaClass.description!!).text().replace('$', artifactConstants.classDelimiter)
            if (clearDescription.isNotEmpty()) {
                clearDescription = clearDescription.replace('>', ' ').replace('<', ' ')
                javaDocProto.add("<strong>Описание: </strong> $clearDescription;")
            }
        }
        return javaDocProto
    }

    /**
     * Генерирует прототип класса
     * @param metaClass метакласс для которого нужен прототип
     * @return заранее собранный TypeSpec.Builder
     */
    fun generateClassProto(metaClass: MetaClassWrapperDto): TypeSpec.Builder {
        logger.info("Generating class ${metaClass.fullCode}...")
        logger.debug("Creating class proto...")
        val className = artifactConstants.getClassNameFromMetacode(metaClass.fullCode)
        val classProto: TypeSpec.Builder = TypeSpec
            .classBuilder(className)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addSuperinterface(IScriptDtObject::class.java)
        if(metaClass.fullCode == "abstractBO") {
            classProto.addSuperinterface(ISProperties::class.java)
            classProto.addSuperinterface(ParameterizedTypeName.get(Map::class.java, String::class.java, Object::class.java))
        }
        logger.debug("Creating class proto - done")
        logger.debug("Generating javaDoc...")
        classProto.addJavadoc(generateClassJavaDocProto(metaClass).build())
        logger.debug("Generating javaDoc - done")
        logger.debug("Creating class fields...")
        val fieldGenerator = FieldGeneratorService(artifactConstants, metaHolder)
        metaClass.attributes.forEach {
            val fieldProto = fieldGenerator.generateFieldProto(it, metaClass)
            if (fieldProto != null) classProto.addField(fieldProto.build())
        }
        logger.debug("Creating class fields - done")
        logger.info("Class ${metaClass.fullCode} generation - done")
        generatedClasses.add(className)
        return classProto
    }

}