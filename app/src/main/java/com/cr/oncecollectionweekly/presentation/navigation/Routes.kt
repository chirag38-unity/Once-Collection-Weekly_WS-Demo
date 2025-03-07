package com.cr.oncecollectionweekly.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Routes {

    @Serializable
    data object StoreGraph: Routes

    @Serializable
    data object ProductDetailsPage: Routes

}