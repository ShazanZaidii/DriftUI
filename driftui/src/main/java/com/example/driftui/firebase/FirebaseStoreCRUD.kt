package com.example.driftui.firebase

import androidx.compose.runtime.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

enum class LoadState {
    Loading,
    Loaded
}

class FirebaseStore<T : Any>(
    private val collection: String,
    private val model: KClass<T>,
    private val explicitSelector: ((T) -> String)? = null
) {

    private val firestore = FirebaseFirestore.getInstance()
    private val collectionRef = firestore.collection(collection)

    private val _items = mutableStateListOf<T>()
    val items: List<T> get() = _items

    private val _loadState = mutableStateOf(LoadState.Loading)
    val loadState: LoadState get() = _loadState.value

    private var listener: ListenerRegistration? = null

    // Smart ID Selector: Checks for 'uid' first, then 'id'
    private val idGetter: ((T) -> String)? = explicitSelector
        ?: model.memberProperties
            .find { it.name == "uid" || it.name == "id" }
            ?.let { prop -> { item -> prop.get(item).toString() } }

    fun bind() {
        if (listener != null) return

        _loadState.value = LoadState.Loading

        listener = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) {
                return@addSnapshotListener
            }

            val fresh = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { fromMap(it) }
            }

            _items.clear()
            _items.addAll(fresh)
            _loadState.value = LoadState.Loaded
        }
    }

    fun unbind() {
        listener?.remove()
        listener = null
    }

    fun rebind() {
        unbind()
        bind()
    }

    @Composable
    fun collect(): List<T> = items

    @Composable
    fun state(): Pair<LoadState, List<T>> = loadState to items

    // --- CRUD ---

    fun add(item: T) {
        val id = idGetter?.invoke(item)
        if (id != null) {
            collectionRef.document(id).set(toMap(item))
        } else {
            collectionRef.add(toMap(item))
        }
    }

    fun edit(item: T?, transform: T.() -> T) {
        if (item == null || idGetter == null) return
        val updated = item.transform()
        val id = idGetter.invoke(item) // Use old ID to find the doc
        collectionRef.document(id).set(toMap(updated))
    }

    fun remove(item: T?) {
        if (item == null || idGetter == null) return
        collectionRef.document(idGetter(item)).delete()
    }

    fun updateOptimistically(item: T) {
        if (idGetter == null) return
        val id = idGetter.invoke(item)
        val index = _items.indexOfFirst { idGetter.invoke(it) == id }
        if (index != -1) {
            _items[index] = item
        }
    }

    fun <V> removeBy(prop: (T) -> V, value: V) {
        val targets = _items.filter { prop(it) == value }
        if (targets.isEmpty()) return

        val batch = firestore.batch()
        targets.forEach { item ->
            idGetter?.invoke(item)?.let { id ->
                batch.delete(collectionRef.document(id))
            }
        }
        batch.commit()
    }

    fun removeList(itemsToDelete: List<T>) {
        if (idGetter == null || itemsToDelete.isEmpty()) return

        // Batch writes (Max 500 ops per batch)
        itemsToDelete.chunked(500).forEach { chunk ->
            val batch = firestore.batch()
            chunk.forEach { item ->
                idGetter.invoke(item).let { id ->
                    batch.delete(collectionRef.document(id))
                }
            }
            batch.commit()
        }
    }

    fun removeAll() {
        if (idGetter == null || _items.isEmpty()) return

        _items.chunked(500).forEach { chunk ->
            val batch = firestore.batch()
            chunk.forEach { item ->
                idGetter.invoke(item).let { id ->
                    batch.delete(collectionRef.document(id))
                }
            }
            batch.commit()
        }
    }

    // --- REFLECTION HELPERS ---

    private fun toMap(obj: T): Map<String, Any?> =
        model.memberProperties.associate { it.name to it.get(obj) }

    private fun fromMap(map: Map<String, Any?>): T {
        val ctor = model.primaryConstructor
            ?: error("Data class must have primary constructor")

        val args = ctor.parameters.mapNotNull { param ->
            // --- CRITICAL FIX START ---
            // Try to find the value in the map
            var rawValue = map[param.name]

            // MIGRATION FALLBACK:
            // If the class expects 'uid' but the map only has 'id' (Old Data), use 'id'.
            if (rawValue == null && param.name == "uid" && map.containsKey("id")) {
                rawValue = map["id"]
            }

            // Only proceed if we found a value (or if we have a fallback)
            if (rawValue != null) {
                val typedValue = when {
                    param.type.classifier == String::class -> rawValue.toString()
                    param.type.classifier == Int::class && rawValue is Number -> rawValue.toInt()
                    param.type.classifier == Double::class && rawValue is Number -> rawValue.toDouble()
                    param.type.classifier == Long::class && rawValue is Number -> rawValue.toLong()
                    else -> rawValue
                }
                param to typedValue
            } else {
                // If value is null, return null so 'mapNotNull' skips this param
                // This forces Kotlin to use the Default Value defined in the Data Class
                null
            }
            // --- CRITICAL FIX END ---
        }.toMap()

        return ctor.callBy(args)
    }
}