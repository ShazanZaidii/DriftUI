package com.example.driftui.firebase

/**
 * Simple text search using Kotlin stdlib.
 */
fun <T> List<T>.search(
    query: String,
    selector: (T) -> String
): List<T> {
    if (query.isBlank()) return this
    return filter {
        selector(it).contains(query, ignoreCase = true)
    }
}

fun <T> List<T>.search(
    query: String,
    selectors: List<(T) -> String>
): List<T> {
    if (query.isBlank()) return this
    return filter { item ->
        selectors.any { selector ->
            selector(item).contains(query, ignoreCase = true)
        }
    }
}
