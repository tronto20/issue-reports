package dev.tronto.issuereports.ep4

import org.springframework.aot.hint.ExecutableMode
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter

class KotlinValueClassRuntimeHints : RuntimeHintsRegistrar {
    private val serializableProvider =
        ClassPathScanningCandidateComponentProvider(false).apply {
            addIncludeFilter(AnnotationTypeFilter(JvmInline::class.java))
        }

    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        val reflection = hints.reflection()
        val targetClasses = serializableProvider.findCandidateComponents("dev.tronto.issuereports.ep4.register")
        targetClasses.forEach {
            val javaClass = Class.forName(it.beanClassName)
            val boxMethod = javaClass.getMethod(
                "box-impl"
            )
            val unboxMethod = javaClass.getMethod("unbox-impl")
            reflection.registerMethod(boxMethod, ExecutableMode.INVOKE)
            reflection.registerMethod(unboxMethod, ExecutableMode.INVOKE)
        }
    }
}
