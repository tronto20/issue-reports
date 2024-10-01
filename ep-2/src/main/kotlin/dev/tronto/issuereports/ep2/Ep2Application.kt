package dev.tronto.issuereports.ep2

import dev.tronto.issuereports.ep2.register.GenericSample
import dev.tronto.issuereports.ep2.register.OtherCompanionName
import dev.tronto.issuereports.ep2.register.RegisterSample
import dev.tronto.issuereports.ep2.unregister.UnRegisterSample
import io.kotest.assertions.throwables.shouldThrow
import jakarta.annotation.PostConstruct
import kotlinx.serialization.SerializationException
import kotlinx.serialization.serializer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

@SpringBootApplication
class Ep2Application {

    fun registerSample() {
        // common case
        serializer<RegisterSample>()
        // kotlin KType
        serializer(typeOf<RegisterSample>())
        // java Type
        serializer(typeOf<RegisterSample>().javaType)
    }

    fun unregisterSample() {
        serializer<UnRegisterSample>()

        shouldThrow<SerializationException> {
            serializer(typeOf<UnRegisterSample>())
        }

        shouldThrow<SerializationException> {
            serializer(typeOf<UnRegisterSample>().javaType)
        }
    }

    fun registerCompanionName() {
        serializer<OtherCompanionName>()
        serializer(typeOf<OtherCompanionName>())
        serializer(typeOf<OtherCompanionName>().javaType)
    }

    fun genericSample() {
        serializer<GenericSample<Int>>()
        serializer(typeOf<GenericSample<Int>>())
        serializer(typeOf<GenericSample<Int>>().javaType)
    }

    fun objectSample() {
        serializer<Int>()
        serializer(typeOf<Int>())
        serializer(typeOf<Int>().javaType)
    }

    fun genericUnRegister() {
        serializer<GenericSample<UnRegisterSample>>()
        shouldThrow<SerializationException> {
            serializer(typeOf<GenericSample<UnRegisterSample>>())
        }
        shouldThrow<SerializationException> {
            serializer(typeOf<GenericSample<UnRegisterSample>>().javaType)
        }
    }

    @PostConstruct
    fun test() {
        registerSample()
        unregisterSample()
        registerCompanionName()
        genericSample()
        objectSample()
        genericUnRegister()
    }
}

fun main() {
    runApplication<Ep2Application>()
}
