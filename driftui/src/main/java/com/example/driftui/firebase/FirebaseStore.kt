package com.example.driftui.firebase

import androidx.compose.runtime.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

class FirebaseStore<T : Any>(
    private val collection: String,
    private val model: KClass<T>,
    // 1. NEW: Optional selector to manually define the ID
    private val explicitSelector: ((T) -> String)? = null
) {

    private val firestore = FirebaseFirestore.getInstance()
    private val collectionRef = firestore.collection(collection)

    private val _items = mutableStateListOf<T>()
    val items: List<T> get() = _items

    private var listener: ListenerRegistration? = null

    // 2. NEW: Smart logic to determine the ID
    private val idGetter: (T) -> String = explicitSelector ?: run {
        // Strategy A: Look for a field named exactly "id"
        val idProperty = model.memberProperties.find { it.name == "id" }

        if (idProperty != null) {
            { item -> idProperty.get(item).toString() }
        } else {
            // Strategy B: Fail safely if we can't find an ID
            { _ ->
                error("Class '${model.simpleName}' does not have a field named 'id'. You must pass an idSelector manually in the constructor.")
            }
        }
    }

    @Composable
    fun Bind() {
        DisposableEffect(collection) {
            listener = collectionRef.addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val fresh = snapshot.documents.mapNotNull { doc ->
                    doc.data?.let { fromMap(it) }
                }
                _items.clear()
                _items.addAll(fresh)
            }
            onDispose {
                listener?.remove()
                listener = null
            }
        }
    }

    fun add(item: T) {
        collectionRef.document(documentId(item)).set(toMap(item))
    }

    fun edit(item: T?, transform: T.() -> T) {
        if (item == null) return
        val updated = item.transform()
        collectionRef.document(documentId(item)).set(toMap(updated))
    }

    fun remove(item: T?) {
        if (item == null) return
        collectionRef.document(documentId(item)).delete()
    }

    fun <V> removeBy(prop: (T) -> V, value: V) {
        val target = _items.find { prop(it) == value } ?: return
        remove(target)
    }

    fun removeAll() {
        _items.forEach { collectionRef.document(documentId(it)).delete() }
    }

    fun seed(vararg seeds: T, key: (T) -> Any) {
        collectionRef.get().addOnSuccessListener { snapshot ->
            val existingKeys = snapshot.documents
                .mapNotNull { it.data }
                .map { fromMap(it) }
                .map(key)
                .toSet()

            seeds.forEach {
                if (key(it) !in existingKeys) {
                    collectionRef.document(key(it).toString()).set(toMap(it))
                }
            }
        }
    }

    // 3. UPDATED: Use the smart getter
    private fun documentId(item: T): String = idGetter(item)

    private fun toMap(obj: T): Map<String, Any?> =
        model.memberProperties.associate { it.name to it.get(obj) }

    private fun fromMap(map: Map<String, Any?>): T {
        val ctor = model.primaryConstructor
            ?: error("Data class must have primary constructor")

        val args = ctor.parameters.mapNotNull { param ->
            if (map.containsKey(param.name)) {
                param to map[param.name]
            } else {
                null
            }
        }.toMap()

        return ctor.callBy(args)
    }
}