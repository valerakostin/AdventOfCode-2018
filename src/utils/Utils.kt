package utils

import java.nio.file.Files
import java.nio.file.Paths

internal object Utils {
    fun getLinesFromSources(name: String): List<String> {
        val url = Utils.javaClass.classLoader.getResource(name)
        if (url != null) {
            return Files.readAllLines(Paths.get(url.toURI()))
        }
        return emptyList()
    }
}