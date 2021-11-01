plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-parcelize")
    kotlin("plugin.serialization") version "1.4.10"
    id("com.squareup.sqldelight")
    id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
    kotlin("native.cocoapods")
}

version = "1.0"

multiplatformSwiftPackage {
    packageName("shared")
    swiftToolsVersion("5.3")
    targetPlatforms {
        iOS { v("13") }
    }
}

kotlin {
    android()

    listOf(
        iosX64(),
        iosArm64()
        //iosSimulatorArm64() waiting ktor M1 support
    )

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "13.0"
        frameworkName = "shared"
        podfile = project.file("../iosApp/Podfile")
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

                // DI
                implementation(Deps.koinCore)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.annotation:annotation:1.1.0")
                implementation(Deps.SqlDelight.driverAndroid)

                implementation(Deps.Ktor.androidCore)
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        //val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            //iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                //Network
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

sqldelight {
    database("MousikiDb") {
        packageName = "com.cas.musicplayer"
    }
}
