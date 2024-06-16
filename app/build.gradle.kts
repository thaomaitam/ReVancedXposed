plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "io.github.chsbuffer.revancedxposed"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.chsbuffer.revancedxposed"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        ndk {
            abiFilters.add("arm64-v8a")
        }
    }

    packagingOptions.resources {
        excludes.addAll(
            arrayOf(
                "META-INF/**", "kotlin/**", "**.bin"
            )
        )
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        compileOptions {
            freeCompilerArgs = listOf(
                "-Xno-param-assertions", "-Xno-receiver-assertions", "-Xno-call-assertions"
            )
        }
    }
}

dependencies {
    implementation(libs.dexkit)
    compileOnly(libs.xposed)
}