package com.example.driftui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.util.Locale
import kotlin.math.min

// =============================================================================================
// 1. ENTRY POINT
// =============================================================================================

/**
 * Reads a JSON file from Assets or URL.
 * Usage: val data = readJson("metro.json")
 */
fun readJson(source: String): DriftJson {
    val context = DriftRegistry.context
        ?: throw IllegalStateException("DriftUI not initialized. Call DriftView or DriftStorage first.")

    val rawString = try {
        if (source.startsWith("http")) {
            // Note: For URLs, this should technically be in a suspend function,
            // but for simplicity in this helper we allow blocking or use strict mode policy.
            // Ideally, use: suspendReadJson()
            URL(source).readText()
        } else {
            context.assets.open(source).bufferedReader().use { it.readText() }
        }
    } catch (e: Exception) { "{}" }

    return DriftJson.parse(rawString)
}

// =============================================================================================
// 2. THE CHAINABLE WRAPPER
// =============================================================================================

class DriftJson(private val element: Any?) : Iterable<DriftJson> {

    companion object {
        fun parse(json: String): DriftJson {
            val trimmed = json.trim()
            return when {
                trimmed.startsWith("{") -> DriftJson(JSONObject(trimmed))
                trimmed.startsWith("[") -> DriftJson(JSONArray(trimmed))
                else -> DriftJson(trimmed)
            }
        }
    }

    // --- ACCESSORS (The "graph['stations']" syntax) ---

    operator fun get(key: String): DriftJson {
        return when (element) {
            is JSONObject -> if (element.has(key)) DriftJson(element.get(key)) else DriftJson(null)
            else -> DriftJson(null)
        }
    }

    operator fun get(index: Int): DriftJson {
        return when (element) {
            is JSONArray -> if (index in 0 until element.length()) DriftJson(element.get(index)) else DriftJson(null)
            else -> DriftJson(null)
        }
    }

    // --- CONVERTERS ---

    override fun toString(): String = element.toString()

    val string: String get() = element.toString()
    val int: Int get() = element.toString().toIntOrNull() ?: 0
    val double: Double get() = element.toString().toDoubleOrNull() ?: 0.0
    val bool: Boolean get() = element.toString().toBoolean()

    // Convert current node to a generic Map (for when you need keys)
    val map: Map<String, DriftJson> get() {
        if (element !is JSONObject) return emptyMap()
        val result = mutableMapOf<String, DriftJson>()
        element.keys().forEach { k -> result[k] = this[k] }
        return result
    }

    // --- ITERATION (Enable .filter, .map, .forEach) ---

    override fun iterator(): Iterator<DriftJson> {
        return when (element) {
            is JSONArray -> object : Iterator<DriftJson> {
                var index = 0
                override fun hasNext(): Boolean = index < element.length()
                override fun next(): DriftJson = DriftJson(element.get(index++))
            }
            // If it's an Object (like your stations map), treat values as a list
            is JSONObject -> object : Iterator<DriftJson> {
                val keys = element.keys()
                override fun hasNext(): Boolean = keys.hasNext()
                override fun next(): DriftJson = DriftJson(element.get(keys.next()))
            }
            else -> emptyList<DriftJson>().iterator()
        }
    }

    // =========================================================================================
    // 3. POWER TOOLS (Search, Sort, Fuzzy)
    // =========================================================================================

    /**
     * Sorts the list/object values by a specific key.
     * Usage: .sort("name")
     */
    fun sort(key: String, ascending: Boolean = true): List<DriftJson> {
        val list = this.toList() // Uses the iterator above
        return if (ascending) {
            list.sortedBy { it[key].string }
        } else {
            list.sortedByDescending { it[key].string }
        }
    }

    /**
     * Exact search.
     * Usage: .search("name", "Mundka")
     */
    fun search(key: String, query: String): List<DriftJson> {
        return this.filter {
            it[key].string.contains(query, ignoreCase = true)
        }
    }

    /**
     * Fuzzy Search using Levenshtein Distance.
     * Usage: .fuzzySearch("name", "Mundka", threshold = 2)
     */
    fun fuzzySearch(key: String, query: String, threshold: Int = 2): List<DriftJson> {
        val q = query.lowercase(Locale.ROOT)
        return this.filter { item ->
            val target = item[key].string.lowercase(Locale.ROOT)

            // 1. Direct containment (Fast path)
            if (target.contains(q)) return@filter true

            // 2. Levenshtein calculation
            calculateLevenshtein(target, q) <= threshold
        }
    }

    // --- INTERNAL HELPERS ---

    private fun calculateLevenshtein(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = min(min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost)
            }
        }
        return dp[s1.length][s2.length]
    }
}