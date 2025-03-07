package com.cr.oncecollectionweekly.presentation.features.productDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cr.oncecollectionweekly.domain.models.FetchProductDetailsResponse
import com.cr.oncecollectionweekly.domain.usecase.StoreUseCase
import com.cr.oncecollectionweekly.utils.AndroidConnectivityObserver
import com.cr.oncecollectionweekly.utils.ContentDisplayState
import com.cr.oncecollectionweekly.utils.DataResultState
import com.cr.oncecollectionweekly.utils.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailViewModel constructor (
    private val storeUseCase: StoreUseCase,
    private val connectivityObserver: AndroidConnectivityObserver
): ViewModel() {

    private var getDataJob : Job? = null
    private var refreshDataJob : Job? = null

    private val _productData = MutableStateFlow<DataResultState<FetchProductDetailsResponse>>(DataResultState.Idle)
    val productData = _productData
        .onStart {
            getData()
        }
        .stateIn(
            scope =  viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue =  DataResultState.Idle
        )

    val isInternetConnected = connectivityObserver.isConnected
        .stateIn(
            scope =  viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue =  false
        )


    fun getData() {
        getDataJob?.cancel()
        getDataJob = viewModelScope.launch {

            _productData.update {
                DataResultState.Loading
            }

            val res = storeUseCase.getData(
                categoryId = "6701",
                productId = "253620",
                languageId = "en",
                storeId = "KWD"
            )

            when(res) {
                is Result.Error -> {
                    _productData.update {
                        DataResultState.Error(res.error)
                    }
                }
                is Result.Success -> {
                    _productData.update {
                        DataResultState.Success(res.data)
                    }
                }
            }

        }
    }

    fun refreshData() {
        refreshDataJob?.cancel()
        refreshDataJob = viewModelScope.launch {
            _productData.update { currentState ->
                when (currentState) {
                    is DataResultState.Success -> currentState.copy(contentDisplayState = ContentDisplayState.Refreshing)
                    else -> DataResultState.Loading
                }
            }

            val res = storeUseCase.getData(
                categoryId = "6701",
                productId = "253620",
                languageId = "en",
                storeId = "KWD"
            )

            when(res) {
                is Result.Error -> {
                    _productData.update { currentState ->
                        when (currentState) {
                            is DataResultState.Success -> currentState.copy(contentDisplayState = null)
                            else -> DataResultState.Error(res.error)
                        }
                    }
                }
                is Result.Success -> {
                    _productData.update {
                        DataResultState.Success(res.data)
                    }
                }
            }

        }
    }

    override fun onCleared() {
        super.onCleared()

        getDataJob?.cancel()
        refreshDataJob?.cancel()

        getDataJob = null
        refreshDataJob = null

    }
}