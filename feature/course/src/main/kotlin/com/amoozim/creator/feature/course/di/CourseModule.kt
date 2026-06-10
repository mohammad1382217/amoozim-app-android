package com.amoozim.creator.feature.course.di

import com.amoozim.creator.core.network.di.DefaultRetrofit
import com.amoozim.creator.feature.course.data.CourseApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CourseModule {

    @Provides
    @Singleton
    fun provideCourseApi(@DefaultRetrofit retrofit: Retrofit): CourseApi =
        retrofit.create(CourseApi::class.java)
}
