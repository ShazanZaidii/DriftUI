//package com.example.myapplication
//
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
//import com.example.driftui.* // Imports most public classes/functions
////import com.example.driftui.lightMode
////import com.example.driftui.preferredColorScheme
////import com.example.driftui.navigationBarBackButtonHidden
//@Composable
//fun LoginScreenView() {
//
//    val viewModel: LoginViewModel = StateObject()
//    val dismiss = Dismiss()
//
//    NavigationStack(Modifier.preferredColorScheme(lightMode).navigationBarBackButtonHidden(true).toolbarStyle(backgroundColor = Color.shazan)) {
//        DriftView {
//            VStack(
//                spacing = 16,
//                modifier = Modifier.padding(20).padding(bottom = 150)
//            ) {
//                Text("Sign In", Modifier
//                    .font(system(24, bold))
//                    .padding(trailing = 240))
//
//                TextField(
//                    placeholder = "Email",
//                    value = viewModel.email,
//                    modifier = Modifier
//                        .frame(width = 350, height = 50)
//                        .clipShape(RoundedRectangle(radius = 28))
//                        .background(Color.lightGray)
//                        .padding(8)
//                )
//
//                SecureField(
//                    placeholder = "Password",
//                    value = viewModel.password,
//                    modifier = Modifier
//                        .frame(width = 350, height = 50)
//                        .clipShape(RoundedRectangle(28))
//                        .background(Color.lightGray)
//                        .padding(8)
//                )
//
//                //Button
//                if (viewModel.isLoading.value) {
//                    Text("Loading…")
//                } else {
//                    Button(
//                        action = { viewModel.login() },
//                        modifier = Modifier
//                            .padding(top = 20)
//                            .frame(width = 120, height = 50)
//                            .clipShape(Capsule())
//                            .background(Color(0xFF567779))
//                    ) {
//                        Text(
//                            "Submit",
//                            Modifier.foregroundStyle(Color.white)
//                                .font(system(23, bold))
//                        )
//                    }
//                }
//               NavigationLink("Details") {
//               LoginScreenView()
//                }
//                NavigationLink(destination = {LoginScreenView()}) {
//                    Text("Try me too")
//                }
//
//// or styled:
//                toolbar(Modifier.toolbarStyle(backgroundColor = Color.black).frame(height = 85)) {
//                    ToolbarItem(ToolbarPlacement.Leading) { NavigationLink(destination = {MyScreen()}) { Text("Home", Modifier.font(system(size = 18,light)).foregroundStyle(Color.white).padding(top = 32)) } }
//                    ToolbarItem(ToolbarPlacement.Trailing) { NavigationLink(destination = {TestView()}) { Text("Next", Modifier.font(system(size = 18,light)).foregroundStyle(Color.white).padding(top = 32)) }}
//                    ToolbarItem(placement = ToolbarPlacement.Center) {
//                        Text("Dismiss", Modifier.onTapGesture(dismiss).padding(top = 26))
//                    }
//                }
//
//
//            }
//        }
//
//
//    }
//}
