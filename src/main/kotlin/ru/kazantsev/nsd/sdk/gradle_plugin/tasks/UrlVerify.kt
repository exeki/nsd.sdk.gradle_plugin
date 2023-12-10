package ru.kazantsev.nsd.sdk.gradle_plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option


class UrlVerify : DefaultTask() {
    @get:Input
    @set:Option(
        option = "url",
        description = "Configures the URL to be verified."
    )
    var url: String? = null

    @TaskAction
    fun verify() {
        logger.quiet("Verifying URL '{}'", url)
        // verify URL by making a HTTP call
    }
}