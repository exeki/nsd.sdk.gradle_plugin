package tests

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.kazantsev.nsd.basic_api_connector.ConnectorParams
import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.ArtifactConstants
import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.client.nsd_connector.SdkApiConnector
import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.services.MetainfoHolder

class TestUtils {
    static String INSTALLATION_ID = "DSO_TEST"
    static Logger logger = LoggerFactory.getLogger(getClass())
    static ConnectorParams connectorParams = ConnectorParams.byConfigFile(INSTALLATION_ID)
    static SdkApiConnector nsdFakeApi = new SdkApiConnector(connectorParams)
    static ObjectMapper objectMapper = new ObjectMapper()
    static ArtifactConstants artifactConstants = new ArtifactConstants(INSTALLATION_ID)
    static MetainfoHolder metainfoHolder =  MetainfoHolder.getInstance(INSTALLATION_ID)
}

