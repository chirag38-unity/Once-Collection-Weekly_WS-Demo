package com.cr.oncecollectionweekly.utils

sealed interface ContentDisplayState {
    data object Refreshing : ContentDisplayState
    data object Paginating : ContentDisplayState
    data class PaginationError(val error: DataError) : ContentDisplayState
}