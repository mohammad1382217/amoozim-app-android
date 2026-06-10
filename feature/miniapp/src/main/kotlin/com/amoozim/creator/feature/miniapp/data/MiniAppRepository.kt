package com.amoozim.creator.feature.miniapp.data

import com.amoozim.creator.core.common.result.ApiResult
import com.amoozim.creator.core.common.result.onSuccess
import com.amoozim.creator.core.network.NetworkCaller
import com.amoozim.creator.feature.miniapp.model.MiniApp
import com.amoozim.creator.feature.miniapp.model.MiniappStatistics
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reads mini-app data. Details are cached in-memory per mini-app id so the header can
 * appear on every tab without refetching (≈ the web app's `["miniAppDetails", id]`
 * query cache). Pass `forceRefresh = true` to bypass the cache.
 */
@Singleton
class MiniAppRepository @Inject constructor(
    private val api: MiniAppApi,
    private val caller: NetworkCaller,
) {
    private val detailsCache = ConcurrentHashMap<String, MiniApp>()

    suspend fun getDetails(miniappId: String, forceRefresh: Boolean = false): ApiResult<MiniApp> {
        if (!forceRefresh) {
            detailsCache[miniappId]?.let { return ApiResult.Success(it) }
        }
        return caller.call { api.getDetails(miniappId) }
            .onSuccess { detailsCache[miniappId] = it }
    }

    suspend fun getStatistics(miniappId: String): ApiResult<MiniappStatistics> =
        caller.call { api.getStatistics(miniappId) }
}
