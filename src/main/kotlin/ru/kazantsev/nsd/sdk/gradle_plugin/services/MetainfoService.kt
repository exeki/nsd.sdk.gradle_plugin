package ru.kazantsev.nsd.sdk.gradle_plugin.services

import java.net.URLClassLoader

class MetainfoService (private val navigator: NavigatorService) {

    companion object {
        /**
         * Метод получения наименования сгенерированных классов
         */
        private const val fakeClassesMetainfoClassMethodName = "getGeneratedClassNames"
    }

    var fakeClassesDependencyAdded = false
    var fakeClassesMetainfoClassName : String? = null
    var fakeClassesArtifactName : String? = null
    fun getGeneratedClassNames () : List<String> {
        if(!fakeClassesDependencyAdded) throw RuntimeException("Cant get generatedClassNames because fakeClasses dependency not added")
        try {
            val config = navigator.project.configurations.findByName("runtimeClasspath")
            val classLoader = URLClassLoader(config!!.files.map { it.toURI().toURL() }.toTypedArray())
            val cl = Class.forName(fakeClassesMetainfoClassName, false, classLoader)
            val declaredMethod = cl.getDeclaredMethod(fakeClassesMetainfoClassMethodName)
            return declaredMethod.invoke(cl) as List<String>
        } catch (e:Exception) {
            throw RuntimeException("Cant get generatedClassNames: ${e.message}")
        }

    }
}