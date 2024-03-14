package tests.fetchAndWrite

import ru.kazantsev.nsd.sdk.gradle_plugin.artifact_generator.client.MetainfoUpdateService

import static tests.TestUtils.*

//Set<String> metaCodes = ["serviceCall", "employee", "task", "ou", 'agreement', 'team', 'root', 'slmService']
new MetainfoUpdateService(connectorParams, db).fetchMeta()
