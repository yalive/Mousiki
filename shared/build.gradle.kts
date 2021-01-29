import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-parcelize")
    kotlin("plugin.serialization") version "1.4.10"
    id("com.squareup.sqldelight")
}

kotlin {
    android()
    ios {
        binaries {
            framework {
                baseName = "shared"
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Deps.Coroutines.common) {
                    version {
                        strictly(Versions.coroutines)
                    }
                }
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.1")
                implementation(Deps.SqlDelight.runtime)
                implementation("com.russhwolf:multiplatform-settings:0.7")
                implementation("com.squareup.sqldelight:coroutines-extensions:1.4.4")
                implementation("com.squareup.sqldelight:coroutines-extensions:1.4.4")

                // Ktor
                implementation(Deps.Ktor.commonCore)
                implementation(Deps.Ktor.commonJson)
                implementation(Deps.Ktor.commonLogging)
                implementation(Deps.Ktor.commonSerialization)

                // Logs
                api("co.touchlab:kermit:0.1.7")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.annotation:annotation:1.1.0")
                implementation(Deps.SqlDelight.driverAndroid)

                implementation(Deps.Ktor.androidCore)

                implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
                // Firebase
                implementation("com.google.firebase:firebase-config-ktx:20.0.2")
                implementation("com.google.firebase:firebase-analytics:18.0.0")
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(Deps.SqlDelight.driverIos)
                implementation(Deps.Ktor.ios)
            }
        }
    }
}

android {
    compileSdkVersion(30)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
    }
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    buildTypes {
        getByName("debug") {
            this.setMatchingFallbacks(mutableListOf("release"))
        }
    }
}

val packForXcode by tasks.creating(Sync::class) {
    group = "build"
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val sdkName = System.getenv("SDK_NAME") ?: "iphonesimulator"
    val targetName = "ios" + if (sdkName.startsWith("iphoneos")) "Arm64" else "X64"
    print("Target is $targetName")
    val framework =
        kotlin.targets.getByName<KotlinNativeTarget>(targetName).binaries.getFramework(mode)
    inputs.property("mode", mode)
    dependsOn(framework.linkTask)
    val targetDir = File(buildDir, "xcode-frameworks")
    from({ framework.outputDirectory })
    into(targetDir)
}
tasks.getByName("build").dependsOn(packForXcode)

sqldelight {
    database("MousikiDb") {
        packageName = "com.cas.musicplayer"
    }
}
