plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
    id("maven-publish")

}

android {
    namespace = "com.shazan.driftui"
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
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

}

dependencies {
    // Versions
    val supabaseVersion = "3.0.1"
    val ktorVersion = "3.0.0"
    val lifecycleVersion = "2.8.0"

    // COMPOSE CORE
    api(platform("androidx.compose:compose-bom:2025.11.00"))
    api("androidx.compose.ui:ui")
    api("androidx.compose.foundation:foundation")
    api("androidx.compose.foundation:foundation-layout")
    api("androidx.compose.material3:material3")
    api("androidx.compose.runtime:runtime")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // UTILS & SERIALIZATION
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

    // ANDROIDX / JETPACK
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.core:core-ktx:1.13.0")
    api("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    api("androidx.navigation:navigation-compose:2.8.5")
    api("androidx.compose.material:material-icons-extended:1.7.5")

    // Image Loading
    implementation("io.coil-kt:coil-compose:2.5.0")

    // BACKEND & HEAVY SDKS
    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.1")

    implementation("io.github.jan-tennert.supabase:supabase-kt:$supabaseVersion")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:$supabaseVersion")
    implementation("io.github.jan-tennert.supabase:auth-kt:$supabaseVersion")
    implementation("io.github.jan-tennert.supabase:realtime-kt:$supabaseVersion")
    implementation("io.github.jan-tennert.supabase:storage-kt:$supabaseVersion")

    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")

    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")

    // ICONS
    implementation("com.composables:icons-lucide-android:1.0.0")

}


afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "com.shazan"
                artifactId = "driftui"
                version = "3.8.2"
            }
        }
    }
}