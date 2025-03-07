package com.cr.oncecollectionweekly.data.remote

import com.cr.oncecollectionweekly.data.models.FetchProductDetailsResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitInterface {

    @GET("productdetails/{categoryId}/{productId}")
    @Headers("Accept: application/json", "Content-Type: application/json")
    suspend fun getData(
        @Path("categoryId") categoryId: String,
        @Path("productId") productId: String,
        @Query("lang") languageId: String,
        @Query("store") storeId: String
    ): Response<FetchProductDetailsResponseDTO>

}