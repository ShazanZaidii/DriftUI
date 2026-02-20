plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
    id("maven-publish")

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

    lint {
        abortOnError = false
    }


}

dependencies {
    // Versions - Defined here for clarity and easy updates
    val voyagerVersion = "1.1.0-beta02"
    val supabaseVersion = "3.0.1" // Ensure this matches your project needs
    val ktorVersion = "3.0.0"
    val lifecycleVersion = "2.8.0"

    // --- COMPOSE CORE (Standard implementation is fine for internal UI) ---
    implementation(platform("androidx.compose:compose-bom:2025.11.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.runtime:runtime")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // --- UTILS & SERIALIZATION ---
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
    // Essential for Supabase @Serializable objects
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

    // --- ANDROIDX / JETPACK ---
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.core:core-ktx:1.13.0")
    api("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    api("androidx.navigation:navigation-compose:2.8.5")
    api("androidx.compose.material:material-icons-extended:1.7.5")

    // --- IMAGE LOADING ---
    api("io.coil-kt:coil-compose:2.5.0")

    // --- VOYAGER NAVIGATION ---
    api("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
    api("cafe.adriel.voyager:voyager-tab-navigator:$voyagerVersion")
    api("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")

    // --- FIREBASE (Keep if you are bridging both) ---
    api(platform("com.google.firebase:firebase-bom:34.7.0"))
    api("com.google.firebase:firebase-firestore-ktx:25.1.1")

    // Supabase Core
    api("io.github.jan-tennert.supabase:supabase-kt:$supabaseVersion")
    api("io.github.jan-tennert.supabase:postgrest-kt:$supabaseVersion")
    api("io.github.jan-tennert.supabase:auth-kt:$supabaseVersion")
    api("io.github.jan-tennert.supabase:realtime-kt:$supabaseVersion")
    api("io.github.jan-tennert.supabase:storage-kt:$supabaseVersion")

// Ktor (Supabase engine)
    api("io.ktor:ktor-client-okhttp:$ktorVersion")

// Credential Manager (Modern Google login)
    api("androidx.credentials:credentials:1.3.0")
    api("androidx.credentials:credentials-play-services-auth:1.3.0")
    api("com.google.android.libraries.identity.googleid:googleid:1.1.1")

}


afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                // This is the crucial part that bundles your 'api' deps into the POM
                from(components["release"])

                groupId = "com.example"
                artifactId = "driftui"
                version = "1.4.2"
            }
        }
    }
}