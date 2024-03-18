package ru.kazantsev.nsd.sdk.gradle_plugin.services

import java.io.File

/**
 * Сервис, ответственный за обработку файлов с кодом с использованием фейковых классов
 */
class CodeReviserService(private val navigator: NavigatorService) {

    companion object {
        private const val fakeClassesRealName = "IScriptDtObject"
    }

    var srcPath: String = "src/main"
    var outPath: String = "sdk/out"
    val processDir: File
    val outDir: File

    init {
        val projectDir = navigator.project.projectDir.path
        this.processDir = File("$projectDir\\$srcPath")
        this.outDir = File("$projectDir\\${this.outPath}")
    }

    fun process() {
        this.process(this.processDir)
    }

    fun process(file: File) {
        if (this.outDir.isFile) throw RuntimeException("CodeReviserExtension.processDir: Out directory cant by a file")
        if (file.isFile) processFile(file)
        else {
            file.listFiles()?.forEach {
                process(it)
            }
        }
    }

    //TODO на будущее
    private fun processModule(directory: File) {
        //взять все файлы из одной папки
        //все файлы разделить по строкам
        //найти все указания пакетов - оставить одно
        //найти все импорты - привести к уникальному
        //сложить в один файл
        //добавить строку с MODULE_NAME
        if (directory.isFile) throw RuntimeException("Got file in method \"processModule\" but expects directory")
        var text = ""
        var packageString = ""
        var imports = ""
        directory.listFiles().forEach { file ->
            if (file.isDirectory) throw RuntimeException("Directories not allowed in module package")
        }
    }

    fun processFile(file: File): File {
        if (!file.isFile) throw RuntimeException("CodeReviserExtension.processFile: File to process cant by a directory. File: ${file.path}")
        var filePath = file.name
        var lookFolder = file.parentFile
        while (true) {
            if(lookFolder == null) break
            filePath = "${lookFolder.name}\\$filePath"
            if (lookFolder == processDir) break
            else lookFolder = lookFolder.parentFile
        }
        val targetFile = File("${this.outDir.path}\\$filePath")
        targetFile.parentFile.mkdirs()
        var text = file.readText()
        val generatedClassNames = navigator.metainfoService.getGeneratedClassNames()
        if (generatedClassNames.isNotEmpty()) {
            generatedClassNames.forEach {
                text = text.replace(it, fakeClassesRealName)
            }
        }
        targetFile.writeText(text)
        return targetFile
    }
}