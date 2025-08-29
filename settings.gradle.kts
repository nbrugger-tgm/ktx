pluginManagement {
    plugins {
        kotlin("jvm") version "2.2.0"
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

include("runtime")
include("codegen")
include("processor")
include("processor:annotations")
include("html5")


rootProject.name="ktx"

