package com.amoozim.creator.core.network

import com.amoozim.creator.core.common.AppMessages
import com.amoozim.creator.core.common.result.ApiResult
import com.amoozim.creator.core.common.result.ErrorCategory
import com.amoozim.creator.core.common.result.Paged
import com.amoozim.creator.core.model.BaseDto
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wraps Retrofit calls into typed [ApiResult]s, replicating the web client's
 * dual-layer success check (HTTP ok AND envelope `success`) and its status→category
 * mapping (`categorizeByStatus`). Repositories depend on this instead of catching
 * transport exceptions themselves.
 */
@Singleton
class NetworkCaller @Inject constructor(
    private val json: Json,
) {

    /** For single-object endpoints returning `BaseDto<T>`. */
    suspend fun <T> call(block: suspend () -> BaseDto<T>): ApiResult<T> =
        try {
            val dto = block()
            val data = dto.data
            if (dto.success && data != null) ApiResult.Success(data) else businessError(dto)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            mapThrowable(e)
        }

    /** For list endpoints returning `BaseDto<List<E>>`; folds the envelope pagination into [Paged]. */
    suspend fun <E> callPaged(block: suspend () -> BaseDto<List<E>>): ApiResult<Paged<E>> =
        try {
            val dto = block()
            val items = dto.data
            if (dto.success && items != null) {
                val p = dto.pagination
                ApiResult.Success(
                    Paged(
                        items = items,
                        page = p?.currentPage ?: 1,
                        perPage = p?.perPage ?: items.size,
                        total = p?.total ?: items.size,
                        lastPage = p?.lastPage ?: 1,
                    ),
                )
            } else {
                businessError(dto)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            mapThrowable(e)
        }

    private fun businessError(dto: BaseDto<*>): ApiResult.Error =
        ApiResult.Error(
            category = if (!dto.errors.isNullOrEmpty()) ErrorCategory.VALIDATION else ErrorCategory.BUSINESS,
            status = dto.status,
            message = dto.message?.takeIf { it.isNotBlank() } ?: AppMessages.GENERIC,
            fieldErrors = dto.errors,
        )

    private fun mapThrowable(e: Throwable): ApiResult.Error = when (e) {
        is HttpException -> mapHttpException(e)
        is SocketTimeoutException -> ApiResult.Error(ErrorCategory.TIMEOUT, 408, AppMessages.TIMEOUT, cause = e)
        is IOException -> ApiResult.Error(ErrorCategory.NETWORK, -1, AppMessages.NETWORK, cause = e)
        else -> ApiResult.Error(ErrorCategory.UNEXPECTED, 500, AppMessages.GENERIC, cause = e)
    }

    private fun mapHttpException(e: HttpException): ApiResult.Error {
        val code = e.code()
        val parsed = runCatching { e.response()?.errorBody()?.string() }
            .getOrNull()
            ?.takeIf { it.isNotBlank() }
            ?.let { body ->
                runCatching {
                    json.decodeFromString(BaseDto.serializer(JsonElement.serializer()), body)
                }.getOrNull()
            }
        return ApiResult.Error(
            category = categorize(code),
            status = code,
            message = parsed?.message?.takeIf { it.isNotBlank() } ?: defaultMessage(code),
            fieldErrors = parsed?.errors,
            cause = e,
        )
    }

    private fun defaultMessage(code: Int): String = when {
        code <= 0 -> AppMessages.NETWORK
        code == 408 -> AppMessages.TIMEOUT
        else -> AppMessages.GENERIC
    }

    private fun categorize(code: Int): ErrorCategory = when {
        code <= 0 -> ErrorCategory.NETWORK
        code == 401 -> ErrorCategory.AUTH
        code in setOf(402, 403, 405, 451) -> ErrorCategory.PERMISSION
        code == 404 || code == 410 -> ErrorCategory.NOT_FOUND
        code == 408 -> ErrorCategory.TIMEOUT
        code == 400 || code == 422 -> ErrorCategory.VALIDATION
        code in setOf(409, 412, 423) -> ErrorCategory.CONFLICT
        code == 429 -> ErrorCategory.RATE_LIMIT
        code >= 500 -> ErrorCategory.SERVER
        code >= 400 -> ErrorCategory.BUSINESS
        else -> ErrorCategory.UNEXPECTED
    }
}
