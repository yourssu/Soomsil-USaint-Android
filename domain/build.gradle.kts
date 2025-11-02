plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.yourssu.soomsil.usaint.domain"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
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
    // :core 모듈 의존성
    implementation(project(":core"))

    // :data 모듈 의존성 (Repository 사용)
    implementation(project(":data"))

    // Hilt (UseCase에서 @Inject 사용)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Coroutines (UseCase에서 사용)
    implementation(libs.androidx.lifecycle.runtime.ktx)
}