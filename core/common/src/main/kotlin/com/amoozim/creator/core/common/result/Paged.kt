package com.amoozim.creator.core.common.result

/**
 * A single page of a paginated list, decoupled from the network envelope so the UI
 * and domain layers never depend on the transport's `pagination` shape.
 */
data class Paged<out T>(
    val items: List<T>,
    val page: Int,
    val perPage: Int,
    val total: Int,
    val lastPage: Int,
) {
    val hasNextPage: Boolean get() = page < lastPage

    companion object {
        fun <T> single(items: List<T>): Paged<T> =
            Paged(items = items, page = 1, perPage = items.size, total = items.size, lastPage = 1)
    }
}
