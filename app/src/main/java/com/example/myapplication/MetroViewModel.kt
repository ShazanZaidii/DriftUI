package com.example.myapplication

import com.example.driftui.core.ObservableObject
import com.example.driftui.core.Published
import com.example.driftui.core.Storage
import com.example.driftui.core.DriftStore
import com.example.driftui.core.State
import com.example.driftui.core.readJson

data class User(
    val username: String,
    val password: String
)

class MetroViewModel : ObservableObject() {
    val users = DriftStore("users_db", User::class)
    init {
        users.add(User("admin", "12345"))

    }
    val admin get() = users.items.find { it.username == "admin" }
    val name get() = admin?.username?:"Value not found"



    var source = Storage(key = "source", defaultValue = "")
    var destination = Storage(key = "destination", defaultValue = "")
    var email = Published("")
    var password = Published("")


    //Graph:
    val graph = readJson("metro_graph_final_fixed.json")
    val allStations = graph["stations"]
    val sortedList = allStations.sort("name")
    var query = State("")
    val searchResults = allStations.fuzzySearch("name", query = query.value)


    var isLoading = Published(false)

    fun login() {
        isLoading.set(true)
        // simulate work (in future you can use coroutines)
    }

}
