package dev.tronto.issuereports.ep1

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter
import reactor.core.publisher.Mono

@Configuration
class Ep1Router {
    @Serializable
    data class Response(
        @SerialName("str-value")
        val value: String,
    )

    val responseBody = listOf(
        Response("value1"),
        Response("value2")
    )

    @Bean
    fun router() = coRouter {
        GET("/bodyValue") {
            ok().bodyValueAndAwait(responseBody)
        }

        GET("/body") {
            ok().body(
                BodyInserters.fromPublisher(
                    Mono.just(responseBody),
                    object : ParameterizedTypeReference<List<Response>>() {}
                )
            ).awaitSingle()
        }

        GET("/bodyAndAwait") {
            ok().bodyAndAwait(responseBody)
        }

        GET("/bodyAndAwaitInline") {
            ok().bodyAndAwaitInline(responseBody)
        }
    }
}

suspend inline fun <reified T : Any> ServerResponse.BodyBuilder.bodyAndAwait(value: T): ServerResponse {
    return this.body(
        BodyInserters.fromPublisher(
            Mono.just(value),
            object : ParameterizedTypeReference<T>() {}
        )
    ).awaitSingle()
}

suspend inline fun <T : Any> ServerResponse.BodyBuilder.bodyAndAwaitInline(value: T): ServerResponse {
    return this.body(
        BodyInserters.fromPublisher(
            Mono.just(value),
            object : ParameterizedTypeReference<T>() {}
        )
    ).awaitSingle()
}
