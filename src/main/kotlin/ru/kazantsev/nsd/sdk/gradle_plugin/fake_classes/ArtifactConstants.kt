package ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes

import java.io.File
import java.net.URI


/**
 * Отвечает за константы артефакта
 */
class ArtifactConstants {

    /**
     * Описывает константы
     */
    companion object {
        /**
         * Постфикс для артефакта, если ему задается наименование
         */
        const val defaultArtifactPostfix: String = "_fake_classes"
        /**
         * Версия для артефакта по умолчанию
         */
        const val defaultArtifactVersion : String = "1.0.0"
        /**
         * Группа для артефакта по умолчанию
         */
        const val defaultArtifactGroup : String = "ru.kazantsev.nsd.sdk"
        /**
         * Наименование папки для проекта по умолчанию
         */
        const val defaultProjectFolderName : String = "fake_classes_project"
        /**
         * Постфикс для генерируемых классов по умолчанию
         */
        const val defaultClassNamePostfix : String = "SDO"
        /**
         * Разделитель меж частиями имени генерируемых классов (класс, тип метакласса и постфикс)  по умолчанию
         */
        const val defaultClassDelimiter : Char = '_'
        /**
         * Пакет, куда будут складываться все генерируемые классы  по умолчанию
         */
        const val defaultPackageName : String = "ru.naumen.core.server.script.spi"
        /**
         * Пакет, куда будет отправлен класс, содержащий метоинформацию артефакта  по умолчанию
         */
        const val defaultGeneratedMetaClassPackage : String = "ru.kazantsev.nsd.sdk.generated_fake_classes"
        /**
         * Наименование класса с метаинформацией по умолчанию
         */
        const val defaultGeneratedMetaClassName : String = "GeneratedMeta"
        /**
         * Uri до моего github репозитория
         */
        const val exekiRepoUri = "https://maven.pkg.github.com/exeki/*"
        /**
         * Имя на github теккущего пользователя, которое берется из переменной окружения
         */
        private val userGithubName = System.getenv("GITHUB_USERNAME")
        /**
         * Токен на github теккущего пользователя, который берется из переменной окружения
         */
        private val userGithubToken = System.getenv("GITHUB_TOKEN")
    }

    /**
     * @param workingDirectoryPath путь до рабочей директории, где будет сгенерирован проект
     * @param artifactName наименовании инсталляции
     */
    constructor(artifactName: String, workingDirectoryPath: String) {
        this.installationId = artifactName
        this.workingDirectory = "${workingDirectoryPath}\\data\\${artifactName.lowercase()}"
        this.projectPath = workingDirectory
        this.targetArtifactName = artifactName.lowercase() + defaultArtifactPostfix
        this.projectFolder ="$projectPath\\$projectFolderName"
        this.generatedProjectSrcPath = "$projectFolder\\src\\main\\java"
        listOf(generatedProjectSrcPath).forEach { File(it).mkdirs() }
    }

    /**
     * Расставит путь до рабочей директории по умолчанию ${System.getProperty("user.home")}\nsd_sdk
     * @param artifactName префикс артефакта
     */
    constructor(artifactName: String) : this(artifactName, "${System.getProperty("user.home")}\\nsd_sdk")

    /**
     * Пользовательский ID инсталляции
     */
    val installationId : String

    /**
     * Наименование jar файла
     */
    val targetArtifactName : String

    /**
     * Версия jar файла
     */
    val targetArtifactVersion =
        defaultArtifactVersion

    /**
     * Целевая группа артефакта
     */
    val targetArtifactGroup =
        defaultArtifactGroup

    /**
     * Имя папки создаваемого проекта
     */
    val projectFolderName =
        defaultProjectFolderName

    /**
     * Постфикс для всех сгенерированных классов
     */
    val classNamePostfix =
        defaultClassNamePostfix

    /**
     * Во всех классах или описаниях классов будет этот символ
     */
    val classDelimiter: Char =
        defaultClassDelimiter

    /**
     * Пакет, куда будут сгружаться все сгенерированные классы
     */
    val packageName: String =
        defaultPackageName

    /**
     * Имя для класса, который будет являться хранилищем метаинформации
     */
    val generatedMetaClassName =
        defaultGeneratedMetaClassName

    /**
     * Пакет, куда складывается метаинформация
     */
    val generatedMetaClassPackage =
        defaultGeneratedMetaClassPackage

    /**
     * Работая директория для хранения файлов
     */
    val workingDirectory: String

    /**
     * Путь до папки, куда будет помещена папка с проектом
     */
    val projectFolder: String

    /**
     * Путь до проекта
     */
    val projectPath: String

    /**
     * Папка исходников сгенерированного проекта
     */
    val generatedProjectSrcPath: String

    /**
     * Этот uri будет интегрирован в build.gradle сгенерированного проекта
     */
    var repositoryUri : URI = URI(exekiRepoUri)

    /**
     * Этот username будет интегрирован в build.gradle сгенерированного проекта
     */
    var repositoryUsername : String? = userGithubName

    /**
     * Этот password будет интегрирован в build.gradle сгенерированного проекта
     */
    var repositoryPassword : String? = userGithubToken

    /**
     * Указать репозиторий, из которого происходит затягивание
     * дополнительных зависимостей для сгенерированного артефакта фейковых классов.
     * Разрешены только maven репозитории
     * @param uri ссылка на репозиторий
     */
    fun setRepository(uri: URI) {
        this.repositoryUri = uri
        this.repositoryUsername = null
        this.repositoryPassword = null
    }

    /**
     * Указать репозиторий, из которого происходит затягивание
     * дополнительных зависимостей для сгенерированного артефакта фейковых классов.
     * Разрешены только maven репозитории
     * @param uri ссылка на репозиторий
     * @param username имя пользователя
     * @param password пароль
     */
    fun setRepository(uri: URI, username: String, password: String) {
        setRepository(uri)
        this.repositoryUsername = username
        this.repositoryPassword = password
    }

    /**
     * Получить наименование для класса на основании кода метакласса NSD
     */
    fun getClassNameFromMetacode(code: String): String {
        val strings: MutableList<String> = mutableListOf()
        code.split("$").forEach {
            strings.add(it[0].uppercase() + it.substring(1, it.length))
        }
        strings.add(classNamePostfix)
        return strings.joinToString(classDelimiter.toString())
    }
}