plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.scarecat"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.scarecat"
        minSdk = 24
        targetSdk = 35
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
}

val cameraxVersion = "1.3.0-alpha08"
val cameraViewVersion = "1.0.0-alpha14"
val cameraExtensionsVersion = "1.0.0-alpha14"

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.camera.lifecycle)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // ML Kit Image Labeling
    implementation("com.google.mlkit:image-labeling:17.0.7")
    // Библиотека для работы с изображениями
    implementation("com.google.mlkit:image-labeling-custom:17.0.2")

    // CameraX core library
    implementation("androidx.camera:camera-core:$cameraxVersion")
    // CameraX Camera2 implementation
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    // CameraX Lifecycle library
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    // CameraX View class
    implementation("androidx.camera:camera-view:$cameraViewVersion")
    // CameraX Extensions library
    implementation("androidx.camera:camera-extensions:$cameraExtensionsVersion")
}