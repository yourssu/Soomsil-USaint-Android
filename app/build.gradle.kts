import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.protobuf") version "0.9.4"
}

android {
    namespace = "com.yourssu.soomsil.usaint"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.yourssu.soomsil.usaint"
        minSdk = 26
        targetSdk = 35
        versionCode = 6
        versionName = "0.1.0"

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
        buildConfig = true
    }
}

dependencies {
    // rusaint
    implementation(libs.rusaint)

    // room dependencies
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // DataStore
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore)
    implementation("com.google.protobuf:protobuf-javalite:3.19.4")

    // webview
    implementation(libs.androidx.browser)

    // alarm permission
    implementation(libs.accompanist.permissions)

    // hilt dependencies
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)
    //

    // Preferences DataStore
    implementation(libs.androidx.datastore.preferences)

    // util
    implementation(libs.timber)

    // worker (Kotlin + coroutines)
    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.androidx.viewpager2)
    implementation(libs.compose.navigation)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.19.4"
    }

    // Generates the java Protobuf-lite code for the Protobufs in this project. See
    // https://github.com/google/protobuf-gradle-plugin#customizing-protobuf-compilation
    // for more information.
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                id("java") {
                    option("lite")
                }
            }
        }
    }
}
