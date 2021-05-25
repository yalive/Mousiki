object Versions {
    val min_sdk = 21
    val target_sdk = 30
    val compile_sdk = 30

    val kotlin = "1.4.21"
    val android_gradle_plugin = "4.0.1"

    val buildToolsVersion = "30.0.2"
    val cocoapodsext = "0.12"
    val coroutines = "1.4.2-native-mt"
    val kermit = "0.1.8"
    val karmok = "0.1.8"
    val koin = "3.0.1"
    val ktlint_gradle_plugin = "9.4.1"
    val ktor = "1.5.3"
    val junit = "4.13.1"
    val material = "1.2.1"
    val desugarJdkLibs = "1.1.1"
    val multiplatformSettings = "0.7"
    val robolectric = "4.4"
    val sqlDelight = "1.4.4"
    val stately = "1.1.1"
    val serialization = "1.0.1"
    val kotlinxDateTime = "0.1.1"

    object AndroidX {
        val appcompat = "1.2.0"
        val constraintlayout = "2.0.4"
        val core = "1.3.2"
        val lifecycle = "2.2.0"
        val recyclerview = "1.1.0"
        val test = "1.3.0"
        val test_ext = "1.1.2"
    }
}

object Deps {

    val koinCore = "io.insert-koin:koin-core:${Versions.koin}"

    object SqlDelight {
        val gradle = "com.squareup.sqldelight:gradle-plugin:${Versions.sqlDelight}"
        val runtime = "com.squareup.sqldelight:runtime:${Versions.sqlDelight}"
        val coroutinesExtensions =
            "com.squareup.sqldelight:coroutines-extensions:${Versions.sqlDelight}"
        val runtimeJdk = "com.squareup.sqldelight:runtime-jvm:${Versions.sqlDelight}"
        val driverIos = "com.squareup.sqldelight:native-driver:${Versions.sqlDelight}"
        val driverAndroid = "com.squareup.sqldelight:android-driver:${Versions.sqlDelight}"
    }

    object Coroutines {
        val common = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
        val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
        val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"
    }

    object Ktor {
        val commonCore = "io.ktor:ktor-client-core:${Versions.ktor}"
        val commonJson = "io.ktor:ktor-client-json:${Versions.ktor}"
        val commonLogging = "io.ktor:ktor-client-logging:${Versions.ktor}"
        val androidCore = "io.ktor:ktor-client-okhttp:${Versions.ktor}"
        val ios = "io.ktor:ktor-client-ios:${Versions.ktor}"
        val commonSerialization = "io.ktor:ktor-client-serialization:${Versions.ktor}"
    }
}