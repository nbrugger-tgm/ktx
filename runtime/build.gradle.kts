import org.gradle.kotlin.dsl.application
plugins {
    kotlin("multiplatform")
}

kotlin {
    jvmToolchain(21)
    jvm ()
    js {
        browser {}
        nodejs {}
    }
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }
    linuxX64()
    linuxArm64()
    mingwX64()//windows
    androidNativeX64()
    androidNativeArm64()
    androidNativeArm32()
    androidNativeX86()
}

repositories {
    mavenCentral()
}