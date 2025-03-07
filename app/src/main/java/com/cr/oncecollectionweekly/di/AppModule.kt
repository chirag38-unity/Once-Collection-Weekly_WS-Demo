package com.cr.oncecollectionweekly.di

import androidx.lifecycle.SavedStateHandle
import com.cr.oncecollectionweekly.data.remote.KtorClient
import com.cr.oncecollectionweekly.data.remote.RetrofitInterface
import com.cr.oncecollectionweekly.data.repository.KtorRepositoryImpl
import com.cr.oncecollectionweekly.data.repository.RetrofitRepositoryImpl
import com.cr.oncecollectionweekly.data.repository.StoreRepository
import com.cr.oncecollectionweekly.data.repository.Webservices.BASE_URL
import com.cr.oncecollectionweekly.domain.usecase.GetData
import com.cr.oncecollectionweekly.domain.usecase.StoreUseCase
import com.cr.oncecollectionweekly.presentation.features.productDetails.ProductDetailViewModel
import com.cr.oncecollectionweekly.utils.AndroidConnectivityObserver
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

val appModule = module {
    single<AndroidConnectivityObserver> { AndroidConnectivityObserver(get()) }

    single <HttpClient> { KtorClient.apiService }

    single {
        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()
        Retrofit.Builder()
            .baseUrl("$BASE_URL/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(RetrofitInterface::class.java)
    }.bind(RetrofitInterface::class)

    single (named("ktor")) { KtorRepositoryImpl(get()) }.bind(StoreRepository::class)
    single (named("retrofit")) { RetrofitRepositoryImpl(get()) }.bind(StoreRepository::class)

    single <StoreUseCase> {
        StoreUseCase(
            getData = GetData(get(named("ktor")))
        )
    }

    viewModel <ProductDetailViewModel> { (handle: SavedStateHandle) ->
        ProductDetailViewModel(get(), get())
    }



}