package com.cr.oncecollectionweekly.data.models

import com.cr.oncecollectionweekly.domain.models.Review
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewDTO(
    @SerialName("rating_count") @SerializedName("rating_count") val ratingCount: Int,
    @SerialName("total_review") @SerializedName("total_review") val totalReview: Int
)

fun ReviewDTO.toReview() = Review(
    ratingCount = this.ratingCount,
    totalReview = this.totalReview
)