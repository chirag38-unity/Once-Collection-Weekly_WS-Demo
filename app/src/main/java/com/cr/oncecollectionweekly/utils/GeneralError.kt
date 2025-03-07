package com.cr.oncecollectionweekly.utils

import android.content.Context
import com.cr.oncecollectionweekly.R

interface GeneralError

sealed interface DataError: GeneralError {
    enum class NetworkError : DataError {
        REQUEST_TIMEOUT,
        UNAUTHORIZED,
        CONFLICT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        PAYLOAD_TOO_LARGE,
        SERVER_ERROR,
        SERIALIZATION,
        CANCELLED,
        UNKNOWN;
    }

    enum class Local: DataError {
        DISK_FULL,
        UNKNOWN
    }
}

// NetworkError.KT

fun DataError.toString(context: Context): String {
    val resId = when(this) {
        DataError.NetworkError.REQUEST_TIMEOUT -> R.string.error_request_timeout
        DataError.NetworkError.TOO_MANY_REQUESTS -> R.string.error_too_many_requests
        DataError.NetworkError.NO_INTERNET -> R.string.error_no_internet
        DataError.NetworkError.SERVER_ERROR -> R.string.error_unknown
        DataError.NetworkError.SERIALIZATION -> R.string.error_serialization
        DataError.NetworkError.UNKNOWN -> R.string.error_unknown
        DataError.NetworkError.UNAUTHORIZED -> R.string.unauthorised
        DataError.NetworkError.CONFLICT -> R.string.network_conflict
        DataError.NetworkError.PAYLOAD_TOO_LARGE -> R.string.payload_too_large
        DataError.NetworkError.CANCELLED -> R.string.request_cancelled
        DataError.Local.DISK_FULL -> R.string.disk_full
        DataError.Local.UNKNOWN -> R.string.error_unknown
    }
    return context.getString(resId)
}