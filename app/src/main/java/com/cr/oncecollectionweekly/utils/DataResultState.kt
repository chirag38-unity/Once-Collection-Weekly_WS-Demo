package com.cr.oncecollectionweekly.utils

sealed class DataResultState<out T> {
    data object Idle : DataResultState<Nothing>()
    data object Loading : DataResultState<Nothing>()
    data class Success<out T>(
        val data: T,
        val contentDisplayState: ContentDisplayState? = null
    ) : DataResultState<T>()
    data class Error(val error: DataError) : DataResultState<Nothing>()
}