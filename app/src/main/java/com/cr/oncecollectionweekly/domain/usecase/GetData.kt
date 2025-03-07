package com.cr.oncecollectionweekly.domain.usecase

import com.cr.oncecollectionweekly.data.repository.StoreRepository
import com.cr.oncecollectionweekly.domain.models.FetchProductDetailsResponse
import com.cr.oncecollectionweekly.utils.DataError
import com.cr.oncecollectionweekly.utils.Result

class GetData constructor (
    private val storeRepository: StoreRepository
) {

    suspend operator fun invoke (
        languageId: String, storeId: String
    ) : Result<FetchProductDetailsResponse, DataError> {
        return storeRepository.getData(languageId = languageId, storeId = storeId)
    }

}