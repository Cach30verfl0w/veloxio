import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.DefaultCInteropSettings
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

val Project.kotlin: KotlinMultiplatformExtension get() = the()
fun Project.linuxTargets(): List<KotlinTarget> = with(kotlin) { listOf(linuxX64(), linuxArm64()) }

fun createCInterop(name: String, targets: List<KotlinTarget>, closure: DefaultCInteropSettings.() -> Unit) {
    targets.filterIsInstance<KotlinNativeTarget>().forEach {
        val main by it.compilations
        main.cinterops.create(name, closure)
    }
}

fun Project.configureTargets() = with(kotlin) {
    linuxX64()
    linuxArm64()
    // jvm()
}
