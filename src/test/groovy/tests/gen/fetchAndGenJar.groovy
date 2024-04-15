/**
 * author:
 * version:
 * since:
 * description:
 */

package tests.gen

import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.services.MetainfoUpdateService
import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.services.JarGeneratorService
import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.services.ProjectGeneratorService

import static tests.TestUtils.*

new MetainfoUpdateService(connectorParams, metainfoHolder).fetchMeta()
new ProjectGeneratorService(artifactConstants, metainfoHolder).generate()
new JarGeneratorService(artifactConstants, metainfoHolder).generate()
