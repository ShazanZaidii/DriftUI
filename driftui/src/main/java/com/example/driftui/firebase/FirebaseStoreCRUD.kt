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
    /**
     * Live snapshot of the Firestore collection.
     *
     * - Always reflects Firestore state
     * - Backed by Compose state
     * - Safe to use with Kotlin's filter / map / sorted APIs
     *
     * IMPORTANT:
     * - This is the ONLY source of truth
     * - All search/filter/sort must be done externally
     */
    val items: List<T> get() = _items

    private var listener: ListenerRegistration? = null

    // 2. NEW: Smart logic to determine the ID
    private val idGetter: ((T) -> String)? = explicitSelector
        ?: model.memberProperties
            .find { it.name == "id" }
            ?.let { prop -> { item -> prop.get(item).toString() } }


    fun bind() {
        if (listener != null) return   // 🔐 prevents duplicate listeners

        listener = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener

            val fresh = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { fromMap(it) }
            }

            _items.clear()
            _items.addAll(fresh)
        }
    }

    fun unbind() {
        listener?.remove()
        listener = null
    }

    @Composable
    fun collect(): List<T> {
        return items
    }


    fun add(item: T) {
        val id = idGetter?.invoke(item)
        if (id != null) {
            collectionRef.document(id).set(toMap(item))
        } else {
            collectionRef.add(toMap(item)) // Firebase auto-ID
        }
    }


    fun edit(item: T?, transform: T.() -> T) {
        if (item == null || idGetter == null) return
        val updated = item.transform()
        collectionRef.document(idGetter(item)).set(toMap(updated))
    }


    fun remove(item: T?) {
        if (item == null || idGetter == null) return
        collectionRef.document(idGetter(item)).delete()
    }


    fun <V> removeBy(prop: (T) -> V, value: V) {
        val targets = _items.filter { prop(it) == value }
        if (targets.isEmpty()) return

        targets.forEach { item ->
            val id = idGetter?.invoke(item)
            if (id != null) {
                collectionRef.document(id).delete()
            }
        }
    }


    fun removeAll() {
        if (idGetter == null) return
        _items.forEach {
            collectionRef.document(idGetter(it)).delete()
        }
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

    private fun toMap(obj: T): Map<String, Any?> =
        model.memberProperties.associate { it.name to it.get(obj) }

    private fun fromMap(map: Map<String, Any?>): T {
        val ctor = model.primaryConstructor
            ?: error("Data class must have primary constructor")

        val args = ctor.parameters.mapNotNull { param ->
            if (map.containsKey(param.name)) {
                val rawValue = map[param.name]

                // --- THE FIX STARTS HERE ---
                val typedValue = when {
                    // 1. If the class expects Int but got a generic Number (like Long), convert it
                    param.type.classifier == Int::class && rawValue is Number -> rawValue.toInt()

                    // 2. If the class expects Double but got a generic Number, convert it
                    param.type.classifier == Double::class && rawValue is Number -> rawValue.toDouble()

                    // 3. Otherwise, just pass the value as-is (String, Boolean, etc.)
                    else -> rawValue
                }
                // --- THE FIX ENDS HERE ---

                param to typedValue
            } else {
                null
            }
        }.toMap()

        return ctor.callBy(args)
    }
}