package ru.kazantsev.nsd.sdk.gradle_plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

import ru.kazantsev.nsd.sdk.gradle_plugin.extensions.FakeClassesExtension

class SdkPlugin : Plugin<Project> {

    companion object {
        const val FAKE_CLASSES_EXTENSION_NAME = "fakeClasses"
    }

    override fun apply(project: Project) {
        val fakeClassesExt: FakeClassesExtension =
            project.extensions.create(FAKE_CLASSES_EXTENSION_NAME, FakeClassesExtension::class.java, project)

        project.tasks.create("createFakeClasses") {
            project.extensions.getByType(FakeClassesExtension::class.java)
        }
    }
}