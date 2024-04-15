package ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.client.dto

/**
 * DTO с информацией по атрибуту
 */
class AttributeDto {
    var title: String = ""
    var code: String = ""
    var type: String = ""
    var hardcoded: Boolean = false
    var required: Boolean = false
    var requiredInInterface: Boolean = false
    var unique: Boolean = false
    var relatedMetaClass : String? = null
    var description: String? = null
}