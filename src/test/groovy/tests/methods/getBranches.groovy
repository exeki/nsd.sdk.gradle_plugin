package tests.methods

import ru.kazantsev.nsd.sdk.gradle_plugin.fake_classes.services.src_generation.AttributeType

import static tests.TestUtils.getNsdFakeApi

def obj = nsdFakeApi.getMetaClassBranchesInfo(["catalogItem", "abstractSysObj", "abstractBO"])
List<String> codes = obj.collect{it.attributes.collect{it.type}}.flatten()
codes.each {code ->
    if(!AttributeType.values().any{it.code == code}) {
        println(code)
    }
}

