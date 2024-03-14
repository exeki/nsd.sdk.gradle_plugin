package tests

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.kazantsev.nsd.basic_api_connector.ConnectorParams
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.ArtifactConstants
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.client.nsd_connector.FakeApiConnector
import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.data.DbAccess

class TestUtils {
    static String INSTALLATION_ID = "DSO_TEST"
    static Logger logger = LoggerFactory.getLogger(getClass())
    static ConnectorParams connectorParams = ConnectorParams.byConfigFile(INSTALLATION_ID)
    static FakeApiConnector nsdFakeApi = new FakeApiConnector(connectorParams)
    static ObjectMapper objectMapper = new ObjectMapper()
    static DbAccess db = DbAccess.createDefaultByInstallationId(INSTALLATION_ID)
    static ArtifactConstants artifactConstants = new ArtifactConstants(INSTALLATION_ID)
}

