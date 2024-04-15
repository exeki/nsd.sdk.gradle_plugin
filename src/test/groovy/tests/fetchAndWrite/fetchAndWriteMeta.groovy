package tests.fetchAndWrite

import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.services.MetainfoUpdateService

import static tests.TestUtils.*

Set<String> metaCodes = ["orderLine", "orderCall"]
MetainfoUpdateService writer = new MetainfoUpdateService(connectorParams, metainfoHolder)
writer.fetchMeta(metaCodes)