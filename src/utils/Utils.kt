package utils

import java.nio.file.Files
import java.nio.file.Paths

internal object Utils {
    fun getLinesFromResource(resource: String): List<String> {
        val url = Utils.javaClass.classLoader.getResource(resource)
        if (url != null)
            return Files.readAllLines(Paths.get(url.toURI()))
        return emptyList()
    }

    fun getLineFromResource(resource: String): String {
        return getLinesFromResource(resource)[0]
    }

    fun <T> getItemsFromResource(resource: String, transform: ((String) -> T)): List<T> {
        return getLinesFromResource(resource).map(transform).toList()
    }
}