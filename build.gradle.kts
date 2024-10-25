plugins {
    alias(libs.plugins.binaryCompatibilityValidator)
}

val veloxioVersion = libs.versions.veloxio.get()

subprojects {
    group = "de.cacheoverflow.veloxio"
    version = veloxioVersion
    
    apply(plugin = "kotlin-multiplatform")
    configureTargets()
}
