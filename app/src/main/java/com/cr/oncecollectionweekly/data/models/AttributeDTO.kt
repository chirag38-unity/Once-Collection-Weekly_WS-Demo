package com.cr.oncecollectionweekly.data.models

import com.cr.oncecollectionweekly.domain.models.Attribute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttributeDTO(
    @SerialName("attribute_image_url") val attributeImageUrl: String? = null,
    @SerialName("color_code") val colorCode: String? = null,
    @SerialName("images") val images: List<String> = emptyList(),
    @SerialName("option_id") val optionId: String,
    @SerialName("price") val price: String,
    @SerialName("swatch_url") val swatchUrl: String,
    @SerialName("value") val value: String
)

fun AttributeDTO.toAttribute() = Attribute(
    attributeImageUrl = this.attributeImageUrl,
    colorCode = this.colorCode,
    images = this.images,
    optionId = this.optionId,
    price = this.price,
    swatchUrl = this.swatchUrl,
    value = this.value,
)