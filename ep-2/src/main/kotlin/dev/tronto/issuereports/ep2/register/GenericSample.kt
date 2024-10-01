package dev.tronto.issuereports.ep2.register

import kotlinx.serialization.Serializable

@Serializable
data class GenericSample<T>(
    val value: T,
)
