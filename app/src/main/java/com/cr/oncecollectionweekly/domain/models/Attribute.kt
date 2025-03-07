package com.cr.oncecollectionweekly.domain.models

data class Attribute(
    val attributeImageUrl: String?,
    val colorCode: String? = null,
    val images: List<String> = emptyList(),
    val optionId: String,
    val price: String,
    val swatchUrl: String,
    val value: String
)
