plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android { namespace = "com.example.photobooth"; compileSdk = 35
    defaultConfig { applicationId = "com.example.photobooth"; minSdk = 26; targetSdk = 35; versionCode = 1; versionName = "1.0" }`r`n    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }`r`n    kotlinOptions { jvmTarget = "17" }
}
