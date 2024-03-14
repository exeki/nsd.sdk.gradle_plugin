package ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.data.dto

import com.fasterxml.jackson.annotation.JsonCreator

enum class AttributeType(private val code: String, private val title: String) {

    OBJECT("object", "Ссылка на БО"),
    CATALOG_ITEM("catalogItem", "Элемент справочника"),
    DT_INTERVAL("dtInterval", "Временной интервал"),
    BOOL("bool", "Логический"),
    DOUBLE("double", "Вещественное число"),
    INTEGER("integer", "Целое число"),
    DATE("date", "Дата"),
    BACK_TIMER("backTimer", "Обратный счетчик"),
    BACK_BO_LINKS("backBOLinks", "Обратная ссылка"),
    TEXT("text", "Текст"),
    STATE("state", "Статус"),
    STRING("string", "Строка"),
    DATE_TIME("dateTime", "Дата/Время"),
    AGGREGATE("aggregate", "Агрегирующий атрибут"),
    RICH_TEXT("richtext", "Текст в формате RTF"),
    HYPERLINK("hyperlink", "Гиперссылка"),
    CATALOG_ITEM_SET("catalogItemSet", "Набор элементов справочника"),
    FILE("file", "Файл"),
    META_CLASS("metaClass", "Метакласс"),
    RESPONSIBLE("responsible", "Ответственный"),
    TIMER("timer", "Счетчик времени"),
    BO_LINKS("boLinks", "Набор ссылок на БО"),
    LICENSE("license", "Лицензия"),
    CASE_LIST("caseList", "Набор метаклассов"),
    UUID("uuid", ""),
    FILE_CONTENT("fileContent", "Содержимое файла"),
    UNKNOWN("unknown", "Неизвестно"),
    LOCALIZED_TEXT("localizedText", "Локализованный текст"),
    COLOR("color", "Цвет"),
    SEC_GROUPS("secGroups", "Группы пользователей"),
    SYSTEM_OBJECT("systemObject", "Системный объект"),
    MULTI_CLASS_OBJECTS("multiClassObjects", "Мультиклассовые объекты"),
    COMMENT_OBJECTS("commentObjects", "Объекты комментариев"),
    RECORD_TYPE("recordType", "Тип записи"),
    SYSTEM_STATE("systemState", "Системный статус");

    companion object {
        @JsonCreator
        fun getByCode(code: String): AttributeType? {
            return entries.find { it.code == code }
        }
    }

    fun getTitle(): String {
        return this.title
    }

    fun getCode(): String {
        return this.code
    }
}