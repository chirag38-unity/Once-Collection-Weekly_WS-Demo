package com.cr.oncecollectionweekly.domain.models

data class ConfigurableOption(
    val attributeCode: String,
    val attributeId: Int,
    val attributes: List<Attribute>,
    val type: String
)
