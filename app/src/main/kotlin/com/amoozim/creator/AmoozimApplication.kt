package com.amoozim.creator

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.amoozim.creator.core.network.di.AuthedOkHttp
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient

/**
 * Application entry point. Hosts the Hilt component and configures Coil's image
 * loader to reuse the authed OkHttp client, so token-protected media (storage host)
 * loads with the bearer header. The client is pulled via an [EntryPoint] because
 * [newImageLoader] runs lazily — after the Hilt graph is ready.
 */
@HiltAndroidApp
class AmoozimApplication : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        val entryPoint = EntryPointAccessors.fromApplication(this, ImageLoaderEntryPoint::class.java)
        return ImageLoader.Builder(this)
            .okHttpClient(entryPoint.authedOkHttpClient())
            .crossfade(true)
            .build()
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ImageLoaderEntryPoint {
        @AuthedOkHttp
        fun authedOkHttpClient(): OkHttpClient
    }
}
