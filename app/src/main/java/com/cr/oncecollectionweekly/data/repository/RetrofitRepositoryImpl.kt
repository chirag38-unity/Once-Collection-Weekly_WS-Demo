package com.cr.oncecollectionweekly.data.repository

import com.cr.oncecollectionweekly.data.models.toFetchProductDetailsResponse
import com.cr.oncecollectionweekly.data.remote.RetrofitInterface
import com.cr.oncecollectionweekly.domain.models.FetchProductDetailsResponse
import com.cr.oncecollectionweekly.utils.DataError
import com.cr.oncecollectionweekly.utils.Result
import com.google.gson.JsonParseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okio.IOException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException
import kotlin.coroutines.cancellation.CancellationException

class RetrofitRepositoryImpl constructor(
    private val retrofitClient: RetrofitInterface
) : StoreRepository {

    override suspend fun getData(
        categoryId: String,
        productId: String,
        languageId: String,
        storeId: String
    ): Result<FetchProductDetailsResponse, DataError> {

        return withContext(Dispatchers.IO) {
            try {

                coroutineContext.ensureActive()

                val response = retrofitClient.getData( categoryId, productId, languageId, storeId)

                coroutineContext.ensureActive()

                if (response.isSuccessful) {
                    return@withContext(Result.Success(response.body()!!.toFetchProductDetailsResponse()))
                } else {
                    return@withContext(Result.Error(DataError.NetworkError.UNKNOWN))
                }
            } catch (e : UnresolvedAddressException) {
                return@withContext(Result.Error(DataError.NetworkError.CONFLICT))
            } catch (e : UnknownHostException) {
                return@withContext(Result.Error(DataError.NetworkError.CONFLICT))
            } catch (e : ConnectException) {
                return@withContext(Result.Error(DataError.NetworkError.NO_INTERNET))
            } catch (e : IOException) {
                return@withContext(Result.Error(DataError.NetworkError.NO_INTERNET))
            } catch (e : SocketTimeoutException) {
                return@withContext(Result.Error(DataError.NetworkError.REQUEST_TIMEOUT))
            } catch (e : JsonParseException) {
                return@withContext(Result.Error(DataError.NetworkError.SERIALIZATION))
            } catch(e: CancellationException) {
                return@withContext(Result.Error(DataError.NetworkError.CANCELLED))
            } catch (e : HttpException) {
                when(e.code()) {
                    401 -> return@withContext(Result.Error(DataError.NetworkError.UNAUTHORIZED))
                    408 -> return@withContext(Result.Error(DataError.NetworkError.REQUEST_TIMEOUT))
                    409 -> return@withContext(Result.Error(DataError.NetworkError.CONFLICT))
                    413 -> return@withContext(Result.Error(DataError.NetworkError.PAYLOAD_TOO_LARGE))
                    500 -> return@withContext(Result.Error(DataError.NetworkError.SERVER_ERROR))
                    else -> {
                        return@withContext(Result.Error(DataError.NetworkError.UNKNOWN))
                    }
                }
            } catch (e : Exception) {
                return@withContext(Result.Error(DataError.NetworkError.UNKNOWN))
            }
        }

    }

}