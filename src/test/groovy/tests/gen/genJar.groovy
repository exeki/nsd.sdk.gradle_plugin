/**
 * author:
 * version:
 * since:
 * description:
 */

package tests.gen

import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.ArtifactConstants
import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.services.JarGeneratorService
import static tests.TestUtils.*

JarGeneratorService get = new JarGeneratorService(new ArtifactConstants("DSO_TEST"), metainfoHolder)
get.generate()