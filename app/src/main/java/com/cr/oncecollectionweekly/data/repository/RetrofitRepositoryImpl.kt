package com.cr.oncecollectionweekly.data.repository

import com.cr.oncecollectionweekly.data.models.FetchProductDetailsResponseDTO
import com.cr.oncecollectionweekly.data.models.toFetchProductDetailsResponse
import com.cr.oncecollectionweekly.data.remote.RetrofitInterface
import com.cr.oncecollectionweekly.domain.models.FetchProductDetailsResponse
import com.cr.oncecollectionweekly.utils.DataError
import com.cr.oncecollectionweekly.utils.Result
import io.github.aakira.napier.Napier
import io.ktor.client.call.NoTransformationFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import java.nio.channels.UnresolvedAddressException
import kotlin.coroutines.cancellation.CancellationException

class RetrofitRepositoryImpl constructor(
    private val retrofitClient: RetrofitInterface
) : StoreRepository {

    override suspend fun getData(
        languageId: String,
        storeId: String
    ): Result<FetchProductDetailsResponse, DataError> {

        return withContext(Dispatchers.IO) {
            try {

                val response = retrofitClient.getData(languageId, storeId)
                Napier.d(response.raw().body().toString(),null)
                if (response.isSuccessful) {
                    return@withContext(Result.Success(response.body()!!.toFetchProductDetailsResponse()))
                } else {
                    return@withContext(Result.Error(DataError.NetworkError.UNKNOWN))
                }
            } catch (e : UnresolvedAddressException) {
                    return@withContext(Result.Error(DataError.NetworkError.NO_INTERNET))
            } catch (e : SerializationException) {
                    return@withContext(Result.Error(DataError.NetworkError.SERIALIZATION))
            } catch(e: CancellationException) {
                    return@withContext(Result.Error(DataError.NetworkError.CANCELLED))
            }  catch(e: NoTransformationFoundException) {
                return@withContext(Result.Error(DataError.NetworkError.SERIALIZATION))
            } catch (e : Exception) {
                Napier.e("getData exception",e)
                return@withContext(Result.Error(DataError.NetworkError.UNKNOWN))
            }
        }

    }

}