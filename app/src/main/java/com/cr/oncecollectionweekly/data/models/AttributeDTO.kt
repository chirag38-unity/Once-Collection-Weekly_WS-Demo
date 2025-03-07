package com.cr.oncecollectionweekly.data.models

import com.cr.oncecollectionweekly.domain.models.Attribute
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AttributeDTO(
    @SerialName("attribute_image_url") @SerializedName("attribute_image_url") val attributeImageUrl: String? = null,
    @SerialName("color_code") @SerializedName("color_code") val colorCode: String? = null,
    @SerialName("images") @SerializedName("images") val images: List<String> = emptyList(),
    @SerialName("option_id") @SerializedName("option_id") val optionId: String,
    @SerialName("price") @SerializedName("price") val price: String,
    @SerialName("swatch_url") @SerializedName("swatch_url") val swatchUrl: String,
    @SerialName("value") @SerializedName("value") val value: String
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