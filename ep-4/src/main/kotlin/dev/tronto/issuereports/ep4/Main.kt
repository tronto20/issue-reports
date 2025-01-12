package dev.tronto.issuereports.ep4

import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service

@SpringBootApplication
class Ep4Application

fun main() {
    runApplication<Ep4Application>()
}

@JvmInline
value class Test(val value: String)

@Service
class TestService {
    fun testing(test: Test) {
        println(test.value)
    }
}

@Service
class TestService2(
    val service: TestService,
) {
    @PostConstruct
    fun test() {
        val testValue = Test("test")
        service.testing(testValue)
    }
}
