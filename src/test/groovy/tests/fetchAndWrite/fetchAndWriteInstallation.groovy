package tests.fetchAndWrite

import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.services.MetainfoUpdateService

import static tests.TestUtils.*

//Set<String> metaCodes = ["serviceCall", "employee", "task", "ou", 'agreement', 'team', 'root', 'slmService']
println("Начало")
new MetainfoUpdateService(connectorParams, metainfoHolder).fetchMeta()
