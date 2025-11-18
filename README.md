Till now it supports SwiftUI like: 
1. H,V and ZStacks -Same syntax, but you can wrap these in DriftView{} to center align and start modifying as in SwiftUI
2. Paddings Example- Text("hello", Modifier.padding(leading = 20)
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

