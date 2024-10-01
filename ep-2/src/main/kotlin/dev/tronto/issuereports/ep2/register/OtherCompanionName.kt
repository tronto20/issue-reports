package dev.tronto.issuereports.ep2.register

import kotlinx.serialization.Serializable

@Serializable
data class OtherCompanionName(
    val value: Int,
) {
    companion object OtherName
}
