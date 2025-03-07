package com.cr.oncecollectionweekly.data.repository

import com.cr.oncecollectionweekly.domain.models.FetchProductDetailsResponse
import com.cr.oncecollectionweekly.utils.DataError
import com.cr.oncecollectionweekly.utils.Result

interface StoreRepository {

    suspend fun getData(
        categoryId: String, productId: String,
        languageId: String, storeId: String
    ) : Result<FetchProductDetailsResponse, DataError>

}