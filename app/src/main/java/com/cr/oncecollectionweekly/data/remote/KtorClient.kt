package com.cr.oncecollectionweekly.data.remote

import com.cr.oncecollectionweekly.BuildConfig
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.accept
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.serialization.kotlinx.xml.xml
import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.serialization.XML

interface KtorClient {

    companion object {

        val apiService by lazy { create() }

        private fun create(): HttpClient {
            return HttpClient(CIO) {

                if (!BuildConfig.BUILD_TYPE.equals("release", true)) {
                    install(Logging) {
                        logger = object : Logger {
                            override fun log(message: String) {
                                Napier.d(message,null,"KTOR Response")
                            }
                        }
                        level = LogLevel.ALL
                    }
                }

                install(ContentNegotiation) {
                    json(
                        Json {
                            encodeDefaults = true
                            ignoreUnknownKeys = true
                            isLenient = true
                            prettyPrint = true
                        }
                    )
                    // Register a transformer that treats text/html as JSON when it contains valid JSON
                    xml(
                        XML {

                        }
                    )

                }

                install(HttpTimeout) {
                    requestTimeoutMillis = 15000L
                    connectTimeoutMillis = 15000L
                    socketTimeoutMillis = 15000L
                }

                install(ResponseObserver) {
                    onResponse { response ->
                        Napier.d(response.status.value.toString(),null,"HTTP status:")
                    }
                }

                // Apply to all requests
                defaultRequest {
                    headers {
//                        append("Authorization",BuildConfig.authorizationKey)
                    }
                    contentType(ContentType.Application.Json)
                    accept(ContentType.Application.Json)
                }

            }
        }

    }

}