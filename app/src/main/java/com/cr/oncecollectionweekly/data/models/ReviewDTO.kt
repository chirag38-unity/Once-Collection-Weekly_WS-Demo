package com.cr.oncecollectionweekly.data.models

import com.cr.oncecollectionweekly.domain.models.Review
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewDTO(
    @SerialName("rating_count") val ratingCount: Int,
    @SerialName("total_review") val totalReview: Int
)

fun ReviewDTO.toReview() = Review(
    ratingCount = this.ratingCount,
    totalReview = this.totalReview
)