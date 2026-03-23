package com.example.driftui.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.util.Locale
import kotlin.math.min

// Entry point for reading JSON files from assets or url
//Call this inside a LaunchedEffect or a CoroutineScope when loading data
suspend fun readJson(source: String): DriftJson = withContext(Dispatchers.IO) {
    val context = DriftRegistry.context
        ?: throw IllegalStateException("DriftUI not initialized. Call DriftSetup first.")

    val rawString = try {
        if (source.startsWith("http")) {
            URL(source).readText()
        } else {
            context.assets.open(source).bufferedReader().use { it.readText() }
        }
    } catch (e: Exception) { "{}" }

    DriftJson.parse(rawString)
}

// chainable wrapper for parsing and navigating JSON structures
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

    // accessors for bracket syntax navigation
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

    // type converters
    override fun toString(): String = element.toString()

    val string: String get() = element.toString()
    val int: Int get() = element.toString().toIntOrNull() ?: 0
    val double: Double get() = element.toString().toDoubleOrNull() ?: 0.0
    val bool: Boolean get() = element.toString().toBoolean()

    // generic map conversion for iterating over keys
    val map: Map<String, DriftJson> get() {
        if (element !is JSONObject) return emptyMap()
        val result = mutableMapOf<String, DriftJson>()
        element.keys().forEach { k -> result[k] = this[k] }
        return result
    }

    // iteration support for standard kotlin collection functions
    override fun iterator(): Iterator<DriftJson> {
        return when (element) {
            is JSONArray -> object : Iterator<DriftJson> {
                var index = 0
                override fun hasNext(): Boolean = index < element.length()
                override fun next(): DriftJson = DriftJson(element.get(index++))
            }
            // treats JSON object values as a list during iteration
            is JSONObject -> object : Iterator<DriftJson> {
                val keys = element.keys()
                override fun hasNext(): Boolean = keys.hasNext()
                override fun next(): DriftJson = DriftJson(element.get(keys.next()))
            }
            else -> emptyList<DriftJson>().iterator()
        }
    }

    // data manipulation tools

    // sorts values by specific key
    fun sort(key: String, ascending: Boolean = true): List<DriftJson> {
        val list = this.toList()
        return if (ascending) {
            list.sortedBy { it[key].string }
        } else {
            list.sortedByDescending { it[key].string }
        }
    }

    // exact search
    fun search(key: String, query: String): List<DriftJson> {
        return this.filter {
            it[key].string.contains(query, ignoreCase = true)
        }
    }

    // fuzzy search using levenshtein distance
    fun fuzzySearch(key: String, query: String, threshold: Int = 2): List<DriftJson> {
        val q = query.lowercase(Locale.ROOT)
        return this.filter { item ->
            val target = item[key].string.lowercase(Locale.ROOT)

            // fast path for direct containment
            if (target.contains(q)) return@filter true

            // fallback to levenshtein calculation
            calculateLevenshtein(target, q) <= threshold
        }
    }

    // internal helper functions
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