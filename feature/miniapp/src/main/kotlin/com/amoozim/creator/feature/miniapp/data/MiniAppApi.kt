package com.amoozim.creator.feature.miniapp.data

import com.amoozim.creator.core.model.BaseDto
import com.amoozim.creator.feature.miniapp.model.MiniApp
import com.amoozim.creator.feature.miniapp.model.MiniappStatistics
import retrofit2.http.GET
import retrofit2.http.Path

interface MiniAppApi {

    @GET("mini-apps/{miniappId}/details")
    suspend fun getDetails(@Path("miniappId") miniappId: String): BaseDto<MiniApp>

    @GET("mini-apps/{miniappId}/statistics")
    suspend fun getStatistics(@Path("miniappId") miniappId: String): BaseDto<MiniappStatistics>
}
