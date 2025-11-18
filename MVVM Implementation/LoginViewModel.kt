package com.example.myapplication

import com.example.driftui.ObservableObject
import com.example.driftui.State

class LoginViewModel : ObservableObject() {

    var email = State("")
    var password = State("")
    var isLoading = State(false)

    fun login() {
        isLoading.set(true)
        // simulate work (in future you can use coroutines)
    }
}
