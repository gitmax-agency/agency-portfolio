plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.hypelist.presentation"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
}

dependencies {
    implementation(project(mapOf("path" to ":hype-data")))
    implementation(project(mapOf("path" to ":hype-domain")))
    implementation(project(mapOf("path" to ":hype-entities")))
    implementation(project(mapOf("path" to ":hype-resources")))
    implementation(project(mapOf("path" to ":hype-architecture")))

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation-android:1.5.4")
    implementation("com.google.android.material:material:1.10.0")
    implementation("com.airbnb.android:lottie-compose:6.2.0")
    implementation("androidx.media:media:1.7.0")
    implementation("androidx.media3:media3-ui:1.2.0")
    implementation("androidx.media3:media3-exoplayer:1.2.0")
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")


    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("androidx.compose.material3:material3:1.2.0-alpha11")

    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    // DI
    implementation("io.insert-koin:koin-core:3.2.2")
    implementation("io.insert-koin:koin-android:3.2.3")
    implementation("io.insert-koin:koin-core-ext:3.0.1")
    implementation("io.insert-koin:koin-androidx-compose:3.2.2")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.firebaseui:firebase-ui-auth:7.2.0")

    implementation("com.google.accompanist:accompanist-systemuicontroller:0.28.0")

    implementation("androidx.compose.material:material:1.5.4")

    // Maps
    implementation("org.osmdroid:osmdroid-android:6.1.16")

    // Blur test
    implementation("com.github.skydoves:cloudy:0.1.2")

    implementation("com.google.android.gms:play-services-auth:20.7.0")
}