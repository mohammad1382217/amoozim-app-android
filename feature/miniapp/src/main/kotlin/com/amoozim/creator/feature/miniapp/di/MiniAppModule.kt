package com.amoozim.creator.feature.miniapp.di

import com.amoozim.creator.core.network.di.DefaultRetrofit
import com.amoozim.creator.feature.miniapp.data.MiniAppApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MiniAppModule {

    @Provides
    @Singleton
    fun provideMiniAppApi(@DefaultRetrofit retrofit: Retrofit): MiniAppApi =
        retrofit.create(MiniAppApi::class.java)
}
