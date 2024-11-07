plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    //needed for automatic JSON serialization/deserialization
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"

    //for room
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.drawingactivity"
    compileSdk = 34
    testBuildType = "debug"

    defaultConfig {
        applicationId = "com.example.drawingactivity"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                cppFlags += ""
            }
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    buildToolsVersion = "34.0.0"
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("junit:junit:4.13.2")
    implementation("androidx.test.ext:junit-ktx:1.1.5")
    implementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-firestore-ktx:24.11.0")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")

    // Espresso dependencies for UI testing
    androidTestImplementation("com.android.support.test:runner:1.0.2")
    androidTestImplementation("com.android.support.test:rules:1.0.2")
    androidTestImplementation("com.android.support.test.espresso:espresso-intents:3.0.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:core-ktx:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test:core:1.4.0")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.navigation:navigation-testing:2.4.1")

    debugImplementation("androidx.fragment:fragment-testing:1.6.2")
    debugImplementation("androidx.fragment:fragment-ktx:1.6.2")

    // AndroidJUnitRunner and JUnit Rules
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation ("androidx.arch.core:core-testing:2.1.0")



    // Mockito for mocking in tests
    testImplementation("org.mockito:mockito-core:4.4.0")
    androidTestImplementation("org.mockito:mockito-android:4.4.0")
    testImplementation("org.mockito:mockito-inline:4.4.0")
    androidTestImplementation ("org.robolectric:robolectric:4.6.1")
    androidTestImplementation("io.mockk:mockk-android:1.12.0")

    //VM + JPC
    implementation("androidx.compose.runtime:runtime-livedata:1.6.1")

    //ROOM STUFF
    implementation("androidx.room:room-common:2.6.1")
    implementation("androidx.databinding:databinding-adapters:8.2.2")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Jetpack ComposeView Stuff
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.02.02"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.02"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // as state flow
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")
}

