package ru.kazantsev.nsd.sdk.gradle_plugin.extensions

import org.gradle.api.Project
import ru.kazantsev.nsd.sdk.gradle_plugin.services.SingletonNavigatorService
import java.io.File

open class CodeReviserExtension(private val project: Project) {
    companion object {
        private const val fakeClassesRealName = "IScriptDtObject"
    }

    var srcPath: String = "src/main"
    var outPath: String = "sdk/out"
    private val processDir: File
    private val outDir: File

    init {
        val projectDir = project.projectDir.path
        this.processDir = File("$projectDir\\$srcPath")
        this.outDir = File("$projectDir\\${this.outPath}")
        project.task("build_src") {
            it.group = "nsd_sdk"
            it.description = "Creates files from all sources in the " +
                    "specified directory with code ready to be placed in NSD"
            it.doLast {
                this.process()
            }
        }
    }

    private fun process() {
        processFile(processDir)
    }

    //TODO на будущее
    private fun processModule(directory: File) {
        if (directory.isFile) throw RuntimeException("Got file in method \"processModule\" but expects directory")
        var text = ""
        var packageString = ""
        var imports = ""
        directory.listFiles().forEach { file ->
            if (file.isDirectory) throw RuntimeException("Directories not allowed in module package")
        }
    }

    private fun processFile(file: File) {
        if (this.outDir.isFile) throw RuntimeException("Out directory cant by a file")
        if (file.isFile) {
            var filePath = file.name
            var lookFolder = file.parentFile
            while (true) {
                filePath = "${lookFolder.name}\\$filePath"
                if (lookFolder == processDir) break
                else lookFolder = lookFolder.parentFile
            }
            val targetFile = File("${this.outDir.path}\\$filePath")
            targetFile.parentFile.mkdirs()
            var text = file.readText()
            val generatedClassNames = SingletonNavigatorService.metainfoService!!.getGeneratedClassNames()
            if (generatedClassNames.isEmpty()) {
                throw RuntimeException("Cant find generatedClassNames in fakeClassesExtension")
            }
            generatedClassNames.forEach {
                text = text.replace(it, fakeClassesRealName)
            }
            targetFile.writeText(text)
        } else {
            file.listFiles()?.forEach {
                processFile(it)
            }
        }
    }
}