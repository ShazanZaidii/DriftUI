package com.example.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.driftui.*

@Composable
fun LoginScreenView() {

    val viewModel: LoginViewModel = StateObject()

    DriftView {
        VStack(
            spacing = 16,
            modifier = Modifier.padding(20)
        ) {
            Text(
                "Sign In",
                modifier = Modifier.font(system(size = 24, weight = bold))
            )

            TextField(
                placeholder = "Email",
                value = viewModel.email,
                modifier = Modifier
                    .padding(8)
                    .background(Color.lightGray)
                    .cornerRadius(8)
            )

            SecureField(
                placeholder = "Password",
                value = viewModel.password
            )


            if (viewModel.isLoading.value) {
                Text("Loading…")
            } else {
                Button(action = { viewModel.login() }) {
                    Text(
                        "Login",
                        modifier = Modifier
                            .padding(horizontal = 20, vertical = 12)
                            .foregroundStyle(Color.white)
                            .background(Color.blue)
                            .cornerRadius(10)
                    )
                }
            }
        }
    }
}
