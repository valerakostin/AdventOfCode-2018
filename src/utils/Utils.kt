package utils

import java.nio.file.Files
import java.nio.file.Paths

fun getLinesFromSources(name: String): List<String> {
    val url = String::class.java.classLoader.getResource(name)
    if (url != null) {
        return Files.readAllLines(Paths.get(url.toURI()))
    }
    return emptyList()

}