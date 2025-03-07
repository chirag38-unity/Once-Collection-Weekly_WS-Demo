package com.cr.oncecollectionweekly.domain.models

data class FetchProductDetailsResponse(
    val data: ProductData?,
    val message: String,
    val status: Int
)
