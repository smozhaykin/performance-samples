/*
 * Copyright 2021 The Android Open Source Project
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

import com.android.build.gradle.BaseExtension

plugins {
    alias(libs.plugins.application) apply false
    alias(libs.plugins.library) apply false
    alias(libs.plugins.test) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.baselineprofile) apply false
    alias(libs.plugins.kotlin.kover)
}

subprojects {
    afterEvaluate {
        configureAndroid()
    }
}

fun Project.configureAndroid() {
    (project.extensions.findByName("android") as? BaseExtension)?.run {
        plugins.apply(rootProject.libs.plugins.kotlin.kover.get().pluginId)
        koverReport {
            defaults {
                mergeWith("debug")
            }
        }
        rootProject.dependencies.kover(project)
    }
}