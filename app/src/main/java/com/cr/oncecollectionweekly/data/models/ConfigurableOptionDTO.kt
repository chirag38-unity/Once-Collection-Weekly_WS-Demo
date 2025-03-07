package com.cr.oncecollectionweekly.data.models

import com.cr.oncecollectionweekly.domain.models.ConfigurableOption
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfigurableOptionDTO(
    @SerialName("attribute_code") val attributeCode: String,
    @SerialName("attribute_id") val attributeId: Int,
    @SerialName("attributes") val attributes: List<AttributeDTO> = emptyList(),
    @SerialName("type") val type: String
)

fun ConfigurableOptionDTO.toConfigurableOption() = ConfigurableOption(
    attributeCode = this.attributeCode,
    attributeId = this.attributeId,
    attributes = this.attributes.map { it.toAttribute() },
    type = this.type
)