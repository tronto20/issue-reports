package dev.tronto.issuereports.ep1

import com.ninjasquad.springmockk.SpykBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.verify
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.core.codec.DecodingException
import org.springframework.http.codec.json.KotlinSerializationJsonDecoder
import org.springframework.http.codec.json.KotlinSerializationJsonEncoder
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@WebFluxTest(Ep1Router::class)
@Import(Ep1Configuration::class)
@SpykBean(KotlinSerializationJsonEncoder::class, KotlinSerializationJsonDecoder::class)
class Ep1RouterTest(
    webTestClient: WebTestClient,
    encoder: KotlinSerializationJsonEncoder,
    ep1Router: Ep1Router,
) : FunSpec({

    extensions(SpringExtension)

    test("bodyValue 를 사용할 경우에는 KotlinSerializationEncoder 가 사용되지 않는다.") {
        val exchange = webTestClient.get().uri("/bodyValue").exchange()
        shouldThrow<DecodingException> {
            exchange.expectBody<List<Ep1Router.Response>>().isEqualTo(ep1Router.responseBody)
        }
        verify(exactly = 0) { encoder.encodeValue(any(), any(), any(), any(), any()) }
    }

    test("body 와 ParameterizedTypeReference를 사용할 경우에는 KotlinSerializationEncoder 가 사용된다.") {
        webTestClient.get().uri("/body").exchange().expectBody<List<Ep1Router.Response>>().isEqualTo(ep1Router.responseBody)
        verify(exactly = 1) { encoder.encodeValue(any(), any(), any(), any(), any()) }
    }

    test("ParameterizedTypeReference를 함수로 사용할 때에는 reified 를 사용하지 않으면 KotlinSerializationEncoder 가 사용되지 않는다.") {
        val exchange = webTestClient.get().uri("/bodyAndAwaitInline").exchange()
        shouldThrow<DecodingException> {
            exchange.expectBody<List<Ep1Router.Response>>().isEqualTo(ep1Router.responseBody)
        }
        verify(exactly = 0) { encoder.encodeValue(any(), any(), any(), any(), any()) }
    }

    test("ParameterizedTypeReference를 함수로 사용할 때에는 reified 를 사용해야 한다.") {
        webTestClient.get().uri("/bodyAndAwait").exchange().expectBody<List<Ep1Router.Response>>().isEqualTo(ep1Router.responseBody)
        verify(exactly = 1) { encoder.encodeValue(any(), any(), any(), any(), any()) }
    }
})
