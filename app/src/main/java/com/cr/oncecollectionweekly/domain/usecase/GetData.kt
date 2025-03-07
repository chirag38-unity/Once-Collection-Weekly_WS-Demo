package com.cr.oncecollectionweekly.domain.usecase

import com.cr.oncecollectionweekly.data.repository.StoreRepository
import com.cr.oncecollectionweekly.domain.models.FetchProductDetailsResponse
import com.cr.oncecollectionweekly.utils.DataError
import com.cr.oncecollectionweekly.utils.Result

class GetData constructor (
    private val storeRepository: StoreRepository
) {

    suspend operator fun invoke (
        categoryId: String, productId: String,
        languageId: String, storeId: String
    ) : Result<FetchProductDetailsResponse, DataError> {
        return storeRepository.getData( categoryId = categoryId, productId = productId, languageId = languageId, storeId = storeId)
    }

}