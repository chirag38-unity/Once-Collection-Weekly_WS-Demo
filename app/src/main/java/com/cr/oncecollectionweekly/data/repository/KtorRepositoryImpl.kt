package com.cr.oncecollectionweekly.data.repository

import com.cr.oncecollectionweekly.data.models.FetchProductDetailsResponseDTO
import com.cr.oncecollectionweekly.data.models.toFetchProductDetailsResponse
import com.cr.oncecollectionweekly.domain.models.FetchProductDetailsResponse
import com.cr.oncecollectionweekly.utils.DataError
import com.cr.oncecollectionweekly.utils.DataResultState
import com.cr.oncecollectionweekly.utils.Result
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException

class KtorRepositoryImpl constructor ( private val client: HttpClient ) : StoreRepository {


    override suspend fun getData(
        categoryId: String,
        productId: String,
        languageId: String,
        storeId: String
    ): Result<FetchProductDetailsResponse, DataError> {
        return withContext(Dispatchers.IO) {

            var response : HttpResponse? = null

            try {
                coroutineContext.ensureActive()

                response = client.get {
                    url(Webservices.getProductDetails( categoryId = categoryId, productId = productId ))
                    parameter("lang", languageId)
                    parameter("store", storeId)
                }

                coroutineContext.ensureActive()

                when(response.status.value) {
                    in 200..299 -> {
                        val res = response.body<FetchProductDetailsResponseDTO>()
                        return@withContext Result.Success(res.toFetchProductDetailsResponse())
                    }
                    401 -> return@withContext(Result.Error(DataError.NetworkError.UNAUTHORIZED))
                    408 -> return@withContext(Result.Error(DataError.NetworkError.REQUEST_TIMEOUT))
                    409 -> return@withContext(Result.Error(DataError.NetworkError.CONFLICT))
                    413 -> return@withContext(Result.Error(DataError.NetworkError.PAYLOAD_TOO_LARGE))
                    500 -> return@withContext(Result.Error(DataError.NetworkError.SERVER_ERROR))
                    else -> {
                        return@withContext(Result.Error(DataError.NetworkError.UNKNOWN))
                    }
                }
            } catch (e : UnresolvedAddressException) {
                    return@withContext(Result.Error(DataError.NetworkError.NO_INTERNET))
            } catch (e : SerializationException) {
                    return@withContext(Result.Error(DataError.NetworkError.SERIALIZATION))
            } catch(e: CancellationException) {
                    return@withContext(Result.Error(DataError.NetworkError.CANCELLED))
            }  catch(e: NoTransformationFoundException) {
                response?.let { text ->
                    val respText = text.bodyAsText()
                    val jsonResponse = Json.decodeFromString<FetchProductDetailsResponseDTO>(respText)
                    return@withContext Result.Success(jsonResponse.toFetchProductDetailsResponse())
                }
                return@withContext(Result.Error(DataError.NetworkError.SERIALIZATION))
            } catch (e : Exception) {
                Napier.e("getData exception",e)
                return@withContext(Result.Error(DataError.NetworkError.UNKNOWN))
            }


        }
    }

}