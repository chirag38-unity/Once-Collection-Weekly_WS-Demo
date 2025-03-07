package com.cr.oncecollectionweekly.utils

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import java.net.SocketTimeoutException
import kotlin.coroutines.cancellation.CancellationException

class NetworkCallWrapper {

    @PublishedApi
    internal var currentJob: Job? = null

    inline fun <reified T> execute(
        scope: CoroutineScope,
        crossinline networkCall: suspend () -> HttpResponse,
        crossinline onSuccess: (T) -> Unit,
        crossinline onError: (DataError.NetworkError) -> Unit
    ) {
        currentJob?.cancel()
        currentJob = scope.launch {
            when (val result = safeApiCall<T>(networkCall)) {
                is Result.Success -> onSuccess(result.data)
                is Result.Error -> onError(result.error)
            }
        }
    }

    suspend inline fun <reified T> getResult(
        crossinline networkCall: suspend () -> HttpResponse
    ): Result<T, DataError.NetworkError> {
        return safeApiCall(networkCall)
    }

    @PublishedApi
    internal suspend inline fun <reified T> safeApiCall(
        crossinline networkCall: suspend () -> HttpResponse
    ): Result<T, DataError.NetworkError> {
        return try {
            withContext(Dispatchers.IO) {
                coroutineContext.ensureActive()
                val response = networkCall()
                coroutineContext.ensureActive()
                handleResponse<T>(response)
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    @PublishedApi
    internal suspend inline fun <reified T> handleResponse(
        response: HttpResponse
    ): Result<T, DataError.NetworkError> {
        return when (response.status.value) {
            in 200..299 -> try {
                Result.Success(response.body<T>())
            } catch (e: SerializationException) {
                Result.Error(DataError.NetworkError.SERIALIZATION)
            }
            else -> Result.Error(mapStatusCodeToError(response.status.value))
        }
    }

    @PublishedApi
    internal fun handleException(e: Exception): Result<Nothing, DataError.NetworkError> {
        return when (e) {
            is CancellationException -> Result.Error(DataError.NetworkError.CANCELLED)
            is UnresolvedAddressException -> Result.Error(DataError.NetworkError.NO_INTERNET)
            is SerializationException -> Result.Error(DataError.NetworkError.SERIALIZATION)
            is SocketTimeoutException -> Result.Error(DataError.NetworkError.REQUEST_TIMEOUT)
            else -> Result.Error(DataError.NetworkError.UNKNOWN)
        }
    }

    @PublishedApi
    internal fun mapStatusCodeToError(statusCode: Int): DataError.NetworkError {
        return when (statusCode) {
            401 -> DataError.NetworkError.UNAUTHORIZED
            408 -> DataError.NetworkError.REQUEST_TIMEOUT
            409 -> DataError.NetworkError.CONFLICT
            413 -> DataError.NetworkError.PAYLOAD_TOO_LARGE
            500 -> DataError.NetworkError.SERVER_ERROR
            429 -> DataError.NetworkError.TOO_MANY_REQUESTS
            else -> DataError.NetworkError.UNKNOWN
        }
    }

    fun cancel() {
        currentJob?.cancel()
    }
}