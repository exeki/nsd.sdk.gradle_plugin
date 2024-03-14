/**
 * author:
 * version:
 * since:
 * description:
 */

package tests.gen

import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.ArtifactConstants
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.data.DbAccess
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.JarGeneratorService

String pathToDb = 'C:\\Users\\ekazantsev\\nsd_sdk\\data\\' + "DSO_TEST".toLowerCase() + '\\sdk_meta_store.mv.db'
JarGeneratorService get = new JarGeneratorService(new ArtifactConstants("DSO_TEST"), new DbAccess(pathToDb))
get.generate()