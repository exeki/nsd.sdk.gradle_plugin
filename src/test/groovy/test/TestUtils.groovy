package test

import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.ArtifactConstants
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.data.DbAccess

class TestUtils {
    static String installationId = "DSO_TEST"
    static ArtifactConstants artifactConstants = new ArtifactConstants(installationId)
    static DbAccess db = DbAccess.createDefaultByInstallationId(installationId)
}
