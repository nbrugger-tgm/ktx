import org.gradle.kotlin.dsl.application
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinNativeCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp") version "2.2.10-2.0.2"
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
    jvm()
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
    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            dependencies {
                compileOnly(project(":processor:annotations"))
                // some kotlin multiplatform issues
                api(project(":processor:annotations"))
                api(project(":runtime"))
            }
        }
    }
}
dependencies {
    add("kspCommonMainMetadata",project(":processor"))
}
//
tasks

tasks.withType<KotlinCompilationTask<*>> {
    dependsOn("kspCommonMainKotlinMetadata")
}