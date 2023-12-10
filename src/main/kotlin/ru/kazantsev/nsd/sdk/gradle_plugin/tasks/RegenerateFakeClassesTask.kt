package ru.kazantsev.nsd.sdk.gradle_plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import ru.kazantsev.nsd.sdk.gradle_plugin.extensions.FakeClassesExtension
import javax.inject.Inject

class RegenerateFakeClassesTask @Inject constructor(
    private val extension: FakeClassesExtension
) : DefaultTask() {
    companion object {
        const val group = "nsd_sdk"
        const val description = "Regenerate fake classes dependency by fetching full metainfo from NSD installation"
    }
    @TaskAction
    fun regenerate() {
        extension.generateDependency()
    }

}