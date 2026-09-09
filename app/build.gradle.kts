plugins {
    alias(libs.plugins.android.application)
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("D:\\AndroidStudioProjects\\ebyzom-parking-finder\\keystore")
            storePassword = "Genvicgar97"
            keyAlias = "key0"
            keyPassword = "Genvicgar97"
        }
    }
    namespace = "com.ebyzom.ebyzomparkingfinder"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.ebyzom.ebyzomparkingfinder"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false // Lo mantenemos desactivado para evitar problemas con WebView y ProGuard por ahora
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}