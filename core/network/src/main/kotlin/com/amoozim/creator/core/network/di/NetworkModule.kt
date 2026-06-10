package com.amoozim.creator.core.network.di

import com.amoozim.creator.core.network.BuildConfig
import com.amoozim.creator.core.network.auth.TokenAuthenticator
import com.amoozim.creator.core.network.interceptor.AuthHeaderInterceptor
import com.amoozim.creator.core.network.interceptor.RequestIdInterceptor
import com.amoozim.creator.core.network.interceptor.RetryInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Provides the HTTP/JSON stack. Two OkHttp clients are built: a [PlainOkHttp] client
 * (no auth — used by the auth bootstrap) and an [AuthedOkHttp] client (adds the bearer
 * token + 401 refresh authenticator). One Retrofit per host (default / analytics /
 * storage) is exposed via qualifiers, matching the web client's three-host model.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val CALL_TIMEOUT_SECONDS = 60L
    private const val CONNECT_TIMEOUT_SECONDS = 30L
    private val JSON_MEDIA_TYPE = "application/json".toMediaType()

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideConverterFactory(json: Json): Converter.Factory =
        json.asConverterFactory(JSON_MEDIA_TYPE)

    @Provides
    @Singleton
    @PlainOkHttp
    fun providePlainClient(
        retryInterceptor: RetryInterceptor,
        requestIdInterceptor: RequestIdInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(retryInterceptor)
        .addInterceptor(requestIdInterceptor)
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC },
                )
            }
        }
        .build()

    @Provides
    @Singleton
    @AuthedOkHttp
    fun provideAuthedClient(
        @PlainOkHttp plainClient: OkHttpClient,
        authHeaderInterceptor: AuthHeaderInterceptor,
        tokenAuthenticator: TokenAuthenticator,
    ): OkHttpClient = plainClient.newBuilder()
        .addInterceptor(authHeaderInterceptor)
        .authenticator(tokenAuthenticator)
        .build()

    @Provides
    @Singleton
    @PlainRetrofit
    fun providePlainRetrofit(
        @PlainOkHttp client: OkHttpClient,
        converterFactory: Converter.Factory,
    ): Retrofit = buildRetrofit(BuildConfig.API_URL, client, converterFactory)

    @Provides
    @Singleton
    @DefaultRetrofit
    fun provideDefaultRetrofit(
        @AuthedOkHttp client: OkHttpClient,
        converterFactory: Converter.Factory,
    ): Retrofit = buildRetrofit(BuildConfig.API_URL, client, converterFactory)

    @Provides
    @Singleton
    @AnalyticsRetrofit
    fun provideAnalyticsRetrofit(
        @AuthedOkHttp client: OkHttpClient,
        converterFactory: Converter.Factory,
    ): Retrofit = buildRetrofit(BuildConfig.API_URL_ANALYTICS, client, converterFactory)

    @Provides
    @Singleton
    @StorageRetrofit
    fun provideStorageRetrofit(
        @AuthedOkHttp client: OkHttpClient,
        converterFactory: Converter.Factory,
    ): Retrofit = buildRetrofit(BuildConfig.STORAGE_API_BASE, client, converterFactory)

    private fun buildRetrofit(
        baseUrl: String,
        client: OkHttpClient,
        converterFactory: Converter.Factory,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(normalizeBaseUrl(baseUrl))
        .client(client)
        .addConverterFactory(converterFactory)
        .build()

    private fun normalizeBaseUrl(url: String): String = if (url.endsWith("/")) url else "$url/"
}
