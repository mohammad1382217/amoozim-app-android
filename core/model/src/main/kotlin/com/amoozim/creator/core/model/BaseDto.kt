package com.amoozim.creator.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Standard response envelope used by every Amoozim endpoint
 * (`src/shared/models/BaseDto.ts`). `success` is the payload-level success flag and
 * is checked independently of the HTTP status.
 */
@Serializable
data class BaseDto<T>(
    val status: Int = 0,
    val success: Boolean = false,
    val data: T? = null,
    val message: String? = null,
    val errors: Map<String, List<String>>? = null,
    val pagination: Pagination? = null,
)

@Serializable
data class Pagination(
    @SerialName("current_page") val currentPage: Int = 1,
    @SerialName("per_page") val perPage: Int = 0,
    val total: Int = 0,
    @SerialName("last_page") val lastPage: Int = 1,
    val from: Int = 0,
    val to: Int = 0,
)
