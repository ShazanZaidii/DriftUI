package com.example.myapplication

import com.example.driftui.ObservableObject
import com.example.driftui.Published
import com.example.driftui.State

class LoginViewModel : ObservableObject() {

    var email = Published("")
    var password = Published("")
    var isLoading = Published(false)

    fun login() {
        isLoading.set(true)
        // simulate work (in future you can use coroutines)
    }
}
