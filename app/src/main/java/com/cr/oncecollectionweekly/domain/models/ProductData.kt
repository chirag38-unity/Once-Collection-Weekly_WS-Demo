package com.cr.oncecollectionweekly.domain.models

data class ProductData(
    val id : String,
    val brandName : String,
    val sku : String,
    val name : String,
    val price : String,
    val image : String,
    val description : String,
    val images : List<String>,
    val configurableOption: List<ConfigurableOption> = emptyList(),
    )
