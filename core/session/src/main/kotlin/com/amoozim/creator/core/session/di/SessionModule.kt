package com.amoozim.creator.core.session.di

import com.amoozim.creator.core.network.auth.TokenProvider
import com.amoozim.creator.core.network.auth.TokenRefresher
import com.amoozim.creator.core.network.di.DefaultRetrofit
import com.amoozim.creator.core.network.di.PlainRetrofit
import com.amoozim.creator.core.session.SessionManager
import com.amoozim.creator.core.session.data.AuthBootstrapApi
import com.amoozim.creator.core.session.data.ProfileApi
import com.amoozim.creator.core.session.data.TokenStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Wires the session layer's implementations into the network layer's auth contracts
 * (DIP): [TokenStore] provides tokens, [SessionManager] performs refresh. Also exposes
 * the auth Retrofit services on their respective clients (plain for bootstrap, authed
 * for `auth/me`).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SessionModule {

    @Binds
    @Singleton
    abstract fun bindTokenProvider(tokenStore: TokenStore): TokenProvider

    @Binds
    @Singleton
    abstract fun bindTokenRefresher(sessionManager: SessionManager): TokenRefresher

    companion object {
        @Provides
        @Singleton
        fun provideAuthBootstrapApi(@PlainRetrofit retrofit: Retrofit): AuthBootstrapApi =
            retrofit.create(AuthBootstrapApi::class.java)

        @Provides
        @Singleton
        fun provideProfileApi(@DefaultRetrofit retrofit: Retrofit): ProfileApi =
            retrofit.create(ProfileApi::class.java)
    }
}
