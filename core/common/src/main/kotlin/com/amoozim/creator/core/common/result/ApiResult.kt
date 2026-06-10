package com.amoozim.creator.core.common.result

/**
 * Outcome of a single API call. Mirrors the web client's dual-layer success model
 * (`fetchInstanceNew.ts`): a call is a [Success] only when the transport succeeded
 * AND the response envelope reported `success == true`; everything else is a typed
 * [Error]. This keeps error handling exhaustive and avoids leaking transport
 * exceptions into the UI layer.
 */
sealed interface ApiResult<out T> {

    data class Success<out T>(val data: T) : ApiResult<T>

    data class Error(
        val category: ErrorCategory,
        val status: Int,
        val message: String,
        val fieldErrors: Map<String, List<String>>? = null,
        val cause: Throwable? = null,
    ) : ApiResult<Nothing>
}

/**
 * Surface category derived from the HTTP/payload status, matching the web app's
 * `categorizeByStatus` table. The UI reacts to the category (e.g. [PERMISSION] on a
 * lesson-contents call means "locked / not purchased") rather than to raw codes.
 */
enum class ErrorCategory {
    BUSINESS,
    AUTH,
    PERMISSION,
    NOT_FOUND,
    VALIDATION,
    CONFLICT,
    RATE_LIMIT,
    TIMEOUT,
    NETWORK,
    SERVER,
    UNEXPECTED,
}

/** `true` when a protected resource was refused because it is locked / not purchased (HTTP 403). */
val ApiResult.Error.isLocked: Boolean get() = category == ErrorCategory.PERMISSION

inline fun <T, R> ApiResult<T>.map(transform: (T) -> R): ApiResult<R> =
    when (this) {
        is ApiResult.Success -> ApiResult.Success(transform(data))
        is ApiResult.Error -> this
    }

inline fun <T> ApiResult<T>.onSuccess(action: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) action(data)
    return this
}

inline fun <T> ApiResult<T>.onError(action: (ApiResult.Error) -> Unit): ApiResult<T> {
    if (this is ApiResult.Error) action(this)
    return this
}

fun <T> ApiResult<T>.getOrNull(): T? = (this as? ApiResult.Success)?.data
