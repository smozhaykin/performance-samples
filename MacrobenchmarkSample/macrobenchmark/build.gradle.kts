/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.android.build.api.dsl.ManagedVirtualDevice

plugins {
    id("kotlin-android")
    alias(libs.plugins.test)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.baselineprofile)
}

// [START macrobenchmark_setup_android]
android {
    // [START_EXCLUDE]
    compileSdk = 34
    namespace = "com.example.macrobenchmark"

    defaultConfig {
        // Minimum supported version for Baseline Profiles.
        // On lower APIs, apps are fully AOT compile, therefore Baseline Profiles aren't needed.
        minSdk = 24
        targetSdk = 34
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    targetProjectPath = ":app"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    testOptions.managedDevices.devices {
        create<ManagedVirtualDevice>("pixel6Api31") {
            device = "Pixel 6"
            apiLevel = 31
            systemImageSource = "aosp"
        }
    }
    // [END_EXCLUDE]
    // Note that your module name may have different name
    targetProjectPath = ":app"
    // Enable the benchmark to run separately from the app process
    experimentalProperties["android.experimental.self-instrumenting"] = true

    buildTypes {
        // declare a build type to match the target app"s build type
        create("benchmark") {
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
            // [START_EXCLUDE silent]
            // Selects release buildType if the benchmark buildType not available in other modules.
            matchingFallbacks.add("release")
            // [END_EXCLUDE]
        }
    }
}
// [END macrobenchmark_setup_android]

baselineProfile {

    // This specifies the managed devices to use that you run the tests on. The default
    // is none.
    managedDevices += "pixel6Api31"

    // This enables using connected devices to generate profiles. The default is true.
    // When using connected devices, they must be rooted or API 33 and higher.
    useConnectedDevices = false
}

dependencies {
    implementation(libs.benchmark.junit)
    implementation(libs.androidx.junit)
    implementation(libs.espresso)
    implementation(libs.ui.automator)
}
