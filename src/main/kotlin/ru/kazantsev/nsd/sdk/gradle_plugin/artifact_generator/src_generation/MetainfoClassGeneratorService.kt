package ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.src_generation

import com.squareup.javapoet.*
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.ArtifactConstants
import java.util.stream.Collectors
import javax.lang.model.element.Modifier

/**
 * Служба, генерирующая прототип класса, являющийся хранилищем метаинформации
 */
class MetainfoClassGeneratorService(
    private val classGeneratorService: ClassGeneratorService,
    private val artifactConstants: ArtifactConstants
) {

    /**
     * Генерирует прототип класса, являющийся хранилищем метаинформации
     */
    fun generateMetaInfoClass(): TypeSpec.Builder {
        val generatedMetaClassProto = TypeSpec.classBuilder(artifactConstants.generatedMetaClassName)
        generatedMetaClassProto.addField(
            FieldSpec
                .builder(
                    ParameterizedTypeName.get(
                        ClassName.get(List::class.java),
                        ClassName.get(String::class.java)
                    ),
                    "generatedClassNames"
                )
                .initializer(
                    "java.util.Arrays.asList(\$L)",
                    classGeneratorService.generatedClasses.stream().map { "\"$it\"" }.collect(Collectors.joining(", "))
                )
                .addJavadoc(CodeBlock.of("Перечень наименований сгенерированных классов"))
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
                .build()
        )
        generatedMetaClassProto.addField(
            FieldSpec.builder(String::class.java, "generatedClassesPackage")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC)
                .initializer("\"\$L\"", artifactConstants.packageName)
                .addJavadoc(CodeBlock.of("Package, куда были сформированы классы"))
                .build()
        )
        generatedMetaClassProto.addMethod(
            MethodSpec.methodBuilder("getGeneratedClassNames")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ParameterizedTypeName.get(List::class.java, String::class.java))
                .addStatement("return \$N", "generatedClassNames")
                .addJavadoc(CodeBlock.of("Получить перечень наименований сгенерированных классов \n @return перечень наименований сгенерированных классов"))
                .build()
        )
        generatedMetaClassProto.addMethod(
            MethodSpec.methodBuilder("getGeneratedClassesPackage")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.get(String::class.java))
                .addStatement("return \$N", "generatedClassesPackage")
                .addJavadoc(CodeBlock.of("Получить package, куда были сформированы классы \n @return Package, куда были сформированы классы"))
                .build()
        )
        generatedMetaClassProto.addJavadoc(CodeBlock.of("Класс, содержащий информацию по сгенерированным классам"))
        generatedMetaClassProto.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        return generatedMetaClassProto
    }
}