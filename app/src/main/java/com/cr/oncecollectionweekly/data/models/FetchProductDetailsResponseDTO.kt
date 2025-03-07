package com.cr.oncecollectionweekly.data.models

import com.cr.oncecollectionweekly.domain.models.FetchProductDetailsResponse
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FetchProductDetailsResponseDTO(
    @SerialName("data") @SerializedName("data") val productData: ProductDataDTO? = null,
    @SerialName("message") @SerializedName("message") val message: String = "Something Went Wrong",
    @SerialName("status") @SerializedName("status") val status: Int = 100
)

fun FetchProductDetailsResponseDTO.toFetchProductDetailsResponse() = FetchProductDetailsResponse(
    data = this.productData?.toProductData(),
    message = this.message,
    status = this.status

)