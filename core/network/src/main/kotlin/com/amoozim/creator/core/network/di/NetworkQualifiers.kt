package com.amoozim.creator.core.network.di

import javax.inject.Qualifier

/** OkHttp client WITHOUT auth header/authenticator — used for the auth bootstrap (callback/refresh). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlainOkHttp

/** OkHttp client WITH the bearer-token interceptor + 401 refresh authenticator. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthedOkHttp

/** Retrofit on the plain client + default host — for unauthenticated auth-bootstrap calls. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlainRetrofit

/** Retrofit on the authed client + default host (`API_URL`). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultRetrofit

/** Retrofit on the authed client + analytics host (`API_URL_ANALYTICS`). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AnalyticsRetrofit

/** Retrofit on the authed client + storage host (`STORAGE_API_BASE`). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StorageRetrofit
