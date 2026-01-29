//package com.example.myapplication
//
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import com.example.driftui.*
//
//@Composable
//fun LoginScreenView() {
//
//    val viewModel: LoginViewModel = StateObject()
//
//    DriftView {
//        VStack(
//            spacing = 16,
//            modifier = Modifier.padding(20)
//        ) {
//            Text(
//                "Sign In",
//                modifier = Modifier.font(system(size = 24, weight = bold))
//            )
//
//            TextField(
//                placeholder = "Email",
//                value = viewModel.email,
//                modifier = Modifier
//                    .frame(width = 300, height = 50)
//                    .clipShape(RoundedRectangle(28))   // You can use cornerRadius(28) too
//                    .background(Color.lightGray) // background drawn within clipped bounds
//                    .padding(8) // optional outer padding if you want spacing outside the field
//            )
//
//
//            SecureField(
//                placeholder = "Password",
//                value = viewModel.password,
//                modifier = Modifier
//                    .frame(width = 300, height = 50)
//                    .clipShape(RoundedRectangle(28))
//                    .background(Color.lightGray)
//                    .padding(8)
//            )
//
//
//
//            if (viewModel.isLoading.value) {
//                Text("Loading…")
//            } else {
//                Button(
//                    action = {
//                        viewModel.login()
//                    },
//                    Modifier
//                        .frame(width = 150, height = 50)
//                        .clipShape(Capsule()) // Clip before background
//                        .background(Color(0xFF567779)) // Using the Custom Color
//                ) {
//                    Text("Submit",
//                        Modifier
//                            .foregroundStyle(Color.white) // Using the new Swifty color
//                            .font(system(size = 26, weight = bold))
//                    )
//                }
//            }
//        }
//    }
//}
