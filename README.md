Till now it supports SwiftUI like: 
1. H,V and ZStacks -Same syntax, but you can wrap these in DriftView{} to center align and start modifying as in SwiftUI
2. Paddings Example- Text("hello", Modifier.padding(leading = 20) <h3> Note: Paddings can't be negative </h3>
3. Background & Color
4. Shapes, ClipShape - Remember clipShape should be used before giving a background color
5. Frame
6. font modifier - To use this we need to remove default "import androidx.compose.material3.Text" and add "import com.example.driftui.Text" (if you dont modify)
7. Images (the file shoud be saved in - app -> src -> main -> res -> drawable -> Your image )
```
Image("//Image name goes here")

```
8. TextField & SecureField(for passwords)

```
// Defining variables
    val username = remember { State("") }
    val password = remember { State("") }

// Using in TextField and SecureField
    TextField("Username",value = username, Modifier.frame(width= 170, height = 40))
    SecureField("Password", value = password, Modifier.frame(width= 170, height = 40))

// Accessing Variables values:
    Text("Live Username: ${username.value}")
    Text("Live Password: ${password.value}")


```   
9. Buttons
```
Button(
                        action = {
                            username.set("")
                            password.set("")
                        },
                        Modifier
                            .frame(width = 280, height = 50)
                            .clipShape(Capsule()) // Clip before background
                            .background(Color(0xFF567779)) // Using the new Swifty color
                    ) {
                        Text("Clear",
                            Modifier
                                .foregroundStyle(Color.white) // Using the new Swifty color
                                .font(system(size = 26, weight = bold))
                        )
                    }
```




10. Divider()
```
Divider(color = Color.white, thickness = 5)
```

11. Lists 
```
// Simple Implementation:
List(alignment = center, modifier = Modifier.padding(top = 50)){
    Text("Hello World")
    Divider()
    Text("banana")
     }

// Using Arrays:
val food = listOf("Apple", "Banana", "Orange", "Grape")
List(food, alignment = top, modifier = Modifier.padding(top = 50)) { item ->
     Text(item, modifier = Modifier.padding(16))
     }

Or you can also say items = array name as:
List(items = food, alignment = top, modifier = Modifier.padding(top = 50)) { item ->
     Text(item, modifier = Modifier.padding(16))
     }

```

12. MVVM (For sample refer - https://github.com/ShazanZaidii/DriftUI/tree/main/MVVM%20Implementation )
13. NavigationStack and Toolbar
Implementation:
<img width="1728" height="1117" alt="Screenshot 2025-11-18 at 10 14 22 PM" src="https://github.com/user-attachments/assets/ed1cbe48-a8b2-4415-b0d6-5cd4fa2f3eac" />
14. preferredColorScheme:

```
NavigationStack(Modifier.preferredColorScheme(lightMode // or DarkMode)){
//Content here
}
```

15. Dismis()-

```
val dismiss = Dismiss()
Button(dismiss) { Text("Dismiss" , Modifier.font(system(size = 28, weight = bold)))}
```

16. Toolbar with modifiers -       

<img width="1728" height="1117" alt="Screenshot 2025-11-20 at 1 44 09 PM" src="https://github.com/user-attachments/assets/0dbb3a48-4e5a-44e0-a403-e0474cee3b9c" />


```
//**Inside OnCreate**- 
//Set WindowCompat.setDecorFitsSystemWindows(window, true) to make toolbar be pushed below the camera punch and
// either WindowCompat.setDecorFitsSystemWindows(window, false)  or just remove this line to make status bAr overlay on Top of Toolbar


```

```
NavigationStack(Modifier.toolbarStyle(foregroundColor = Color.shazan, backgroundColor = Color.DarkGray)) {
        DriftView {
            toolbar(
                Modifier.toolbarStyle(backgroundColor = Color.yellow)
                    .frame(width = 450, height = 100)
                    .clipShape(RoundedRectangle(68))
            ) {
                ToolbarItem(placement = ToolbarPlacement.Center) {
                        Text(
                            "I am a Toolbar!", Modifier.frame(width = 290)
                                .padding(top = 38).padding(leading = 55).foregroundStyle(Color.white)
                                .font(system(size = 26, weight = bold))
                        )
                }
            }


        }

    }
```
17. Toggle: [Can accept modifiers- onColor, offColor, & thumbColor]
```
// Type 1:
val wifi = State(true)
Toggle("Simple", value = wifi)

//Type 2:
val wifi = State(true)
Toggle(value = wifi, Modifier.padding(top = 5)){
                    Text("Switch", Modifier.font(system(size =18, weight = bold)))
                }

// Type 3: (Simple with custom Toggle color Modifiers) :
Toggle("Hello", value = wifi, Modifier.toggleStyle(onColor = Color.Magenta, offColor = Color.Transparent, thumbColor = Color.Green) )

// Type 4: (Advanced with custom Toggle color Modifiers)
Toggle(value = wifi, Modifier.toggleStyle(onColor = Color.teal, offColor = Color.gray, thumbColor = Color.DarkGray).padding(bottom = 120)){
                    Text("Switch", Modifier.foregroundStyle(Color.white))
                }


//                       To access the underlying value of the variable wifi-
                         Text("Current value is- ${wifi.value}")


```

18. ScaleEffect, RotationEffect & Offsets: (Yes, offsets can be negative too, Below is an example of how to use offsets and scaleEffect inside a Toggle):
```
 Toggle("Hello", value = wifi, Modifier
.padding(top= 220)
.toggleStyle(onColor = Color.Magenta, offColor = Color.Transparent, thumbColor = Color.Green)
.rotationEffect(degrees = 180)
.scaleEffect(3))

```

19. Opacity:
```
Text("Shazan", Modifier.foregroundStyle(Color.white).font(system(size = 37)).zIndex(1f).padding(top = 90).opacity(0.5))

```
20. Sliders:
```
Type1: Simple (no modifiers)
var size = State(10)
Slider(value = size)

Type2: [Advanced]
var volume = State(50)
Slider(
       value = volume,
       range = 0..200,
       step = 10,
       Modifier.sliderStyle(
       activeTrackColor = Color.shazan,
       thumbColor = Color.teal,
       stepColor = Color.white,
       stepOpacity = 0.05)
       .frame(width = 250, height = 30)
        )

```
21. onDoubleTap, onTripleTap, onHold and untilHold (which accepts parameters) -
```
@Composable
fun LoginScreenView2() {
    var isHighlighted = State(false)
    NavigationStack(Modifier.toolbarStyle(foregroundColor = Color.shazan, backgroundColor = Color.shazan).preferredColorScheme(lightMode)) {
        DriftView {
            var value = State(0)

                VStack {
                   Text("Hold Me", Modifier.untilHold(onPress = { isHighlighted.set(true) }, onRelease = {isHighlighted.set(false)}).font(system(size = 48, weight = bold)).foregroundStyle(Color.shazan))
                    Text("${isHighlighted.value}", Modifier.padding(top = 50))
                    Slider(value = value, range = 0..100, step = 12,  modifier = Modifier.sliderStyle(stepOpacity = 0.1).rotationEffect(-90).offset(x= 120,y = 180))
                    Button(action = {isHighlighted.toggle()}) {
                        Text("Touch Me")
                    }

                    toolbar(Modifier.frame(height = 110)) {
                        ToolbarItem(placement = ToolbarPlacement.Center) {
                            Text("Gesture Control", Modifier.foregroundStyle(Color.white).font(system(size = 28, weight = bold)).padding(top = 55))
                        }
                    }
                }



        }

    }
}

```
Steps to Use DriftUI:
1. Create a new Module of type Android Library
2. Name it driftui
3. if already not created, create a new package named- "com.example.driftui" and there create a new Kotlin Class/File and paste the contents of Layout.kt from the repo
4. Satisfy build.gradle requirements.
5. And you are ready to go!

Project Structure:
![ss](https://github.com/user-attachments/assets/ce719ab5-02a8-407b-98e2-5cc479a90a9f)

For Imports:

```
//MainActivity.kt:
package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.driftui.DriftView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DriftView {
                LoginScreenView()
            }
        }
    }
}

```

```
//build.gradle.kts(:driftui)
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.driftui"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.11.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // MVVM Dependency that was missing
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
}

```

```
//build.gradle.kts(:app)
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":driftui"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.foundation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
```

Checkout Screenhot:
![Screenshot 2025-11-18 at 12 43 29 PM](https://github.com/user-attachments/assets/5139df8a-706d-4fc8-af20-3d3cecf37ee5)

