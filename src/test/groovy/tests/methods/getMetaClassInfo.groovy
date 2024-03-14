package tests.methods

import com.fasterxml.jackson.databind.ObjectMapper
import static tests.TestUtils.*


ObjectMapper objectMapper = new ObjectMapper()

def obj = nsdFakeApi.getMetaClassInfo("serviceCall")

logger.info(
        objectMapper.writeValueAsString(obj.attributes.type.unique())
)

