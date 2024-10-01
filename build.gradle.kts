// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.devtools.ksp") version "1.9.25-1.0.20" apply false
    kotlin("plugin.serialization") version "1.9.25" apply false
    id("androidx.navigation.safeargs") version "2.8.1" apply false
}