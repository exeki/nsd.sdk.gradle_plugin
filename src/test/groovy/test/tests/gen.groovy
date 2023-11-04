package test.tests

import static test.TestUtils.*

import ru.kazantsev.nsd.sdk.artifact_generator.JarGeneratorService

new JarGeneratorService (artifactConstants, db).generate(installationId)