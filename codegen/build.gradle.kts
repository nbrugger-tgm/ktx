plugins {
    `application`
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.sun.xsom:xsom:20140925")
    implementation("com.squareup:kotlinpoet:1.15.3")
    implementation("com.google.devtools.ksp:symbol-processing-api:2.2.0-2.0.2")
}

kotlin {
    jvmToolchain(21)
}