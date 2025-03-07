package com.cr.oncecollectionweekly.data.repository

object Webservices {

    const val BASE_URL = "https://klinq.com/rest/V1"

    fun getProductDetails(categoryId: String,productId: String) = "$BASE_URL/productdetails/$categoryId/$productId"

}