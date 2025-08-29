plugins {
    kotlin("multiplatform")
}
kotlin {
    jvm()
    jvmToolchain(21)
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation("com.google.devtools.ksp:symbol-processing-api:2.2.0-2.0.2")
                implementation(project(":codegen"))
                implementation(project(":processor:annotations"))
            }
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
        }
    }
}
repositories {
    mavenCentral()
}