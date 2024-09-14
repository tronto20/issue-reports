package dev.tronto.issuereports.ep1

import kotlinx.serialization.json.Json
import org.springframework.boot.web.codec.CodecCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.json.KotlinSerializationJsonDecoder
import org.springframework.http.codec.json.KotlinSerializationJsonEncoder

@Configuration
class Ep1Configuration {
    @Bean
    fun json(): Json = Json

    @Bean
    fun kotlinSerializationJsonEncoder(json: Json): KotlinSerializationJsonEncoder =
        KotlinSerializationJsonEncoder(json)

    @Bean
    fun kotlinSerializationJsonDecoder(json: Json): KotlinSerializationJsonDecoder =
        KotlinSerializationJsonDecoder(json)

    @Bean
    fun codecCustomizer(
        encoder: KotlinSerializationJsonEncoder,
        decoder: KotlinSerializationJsonDecoder,
    ): CodecCustomizer = CodecCustomizer { configurer ->
        configurer.defaultCodecs()
            .kotlinSerializationJsonEncoder(encoder)
        configurer.defaultCodecs()
            .kotlinSerializationJsonDecoder(decoder)
    }
}
