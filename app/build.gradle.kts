import com.google.protobuf.gradle.GenerateProtoTask
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.androidx.room)
    id("com.google.protobuf") version "0.9.4"
    alias(libs.plugins.baselineprofile)
}

val properties = Properties()
properties.load(FileInputStream(rootProject.file("local.properties")))

android {
    namespace = "com.yourssu.soomsil.usaint"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.yourssu.soomsil.usaint"
        minSdk = 28
        targetSdk = 35
        versionCode = 22
        versionName = "0.2.9"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "POSTHOG_TOKEN", "\"${properties.getProperty("posthog_token")}\"")
        buildConfigField("String", "POSTHOG_DEV_TOKEN", "\"${properties.getProperty("posthog_dev_token")}\"")

        externalNativeBuild {
            cmake {
                arguments += listOf("-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        buildConfig = true
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

baselineProfile {
    dexLayoutOptimization = true
}

dependencies {
    constraints {
        implementation(libs.jna)
    }

    // rusaint
    implementation(libs.rusaint)

    // room dependencies
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.profileinstaller)
    "baselineProfile"(project(":baselineprofile"))
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.gson)

    // DataStore
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.javalite)

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

    // MixPanel
    implementation(libs.posthog.android)

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

    androidTestImplementation(libs.androidx.uiautomator)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// Setup protobuf configuration, generating lite Java and Kotlin classes
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.29.2"
    }

    // Generates the java Protobuf-lite code for the Protobufs in this project. See
    // https://github.com/google/protobuf-gradle-plugin#customizing-protobuf-compilation
    // for more information.
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}

// https://github.com/google/ksp/issues/1590#issuecomment-1826387452
androidComponents {
    onVariants(selector().all()) { variant ->
        afterEvaluate {
            val variantName = variant.name.replaceFirstChar { it.uppercaseChar() }
            val protoTask = project.tasks.getByName("generate${variantName}Proto") as GenerateProtoTask
            val generatedProtoJavaDir = file("${protoTask.outputBaseDir}/java")

            project.tasks.getByName("ksp${variantName}Kotlin") {
                dependsOn(protoTask)
                when (this) {
                    is com.google.devtools.ksp.gradle.KspAATask -> {
                        kspConfig.javaSourceRoots.from(generatedProtoJavaDir)
                    }

                    is org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompileTool<*> -> {
                        setSource(generatedProtoJavaDir)
                    }
                }
            }
        }
    }
}
