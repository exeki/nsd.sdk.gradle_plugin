package tests.fetchAndWrite

import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.client.MetainfoUpdateService

import static tests.TestUtils.*

Set<String> metaCodes = ["orderLine", "orderCall"]
MetainfoUpdateService writer = new MetainfoUpdateService(connectorParams, db)
writer.fetchMeta(metaCodes)