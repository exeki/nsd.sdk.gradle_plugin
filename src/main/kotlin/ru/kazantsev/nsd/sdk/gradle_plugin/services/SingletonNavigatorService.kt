package ru.kazantsev.nsd.sdk.gradle_plugin.services

import org.gradle.api.Project
import ru.kazantsev.nsd.sdk.gradle_plugin.extensions.CodeReviserExtension
import ru.kazantsev.nsd.sdk.gradle_plugin.extensions.FakeClassesExtension

class SingletonNavigatorService {
    companion object {
        var metainfoService: MetainfoService? = null
        var codeReviserExtension: CodeReviserExtension? = null
        var fakeClassesExtension: FakeClassesExtension? = null
        fun init(project: Project) {
            fakeClassesExtension = project.extensions.create(
                "fakeClasses",
                FakeClassesExtension::class.java,
                project
            )
            codeReviserExtension = project.extensions.create(
                "codeReviser",
                CodeReviserExtension::class.java,
                project
            )
            metainfoService = MetainfoService(project)
        }
    }
}