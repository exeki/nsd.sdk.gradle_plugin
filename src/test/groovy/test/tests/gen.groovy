package test.tests

import static test.TestUtils.*

import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.JarGeneratorService

new JarGeneratorService (artifactConstants, db).generate(installationId)