package com.amoozim.creator.feature.miniapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Aggregate metrics for a mini-app (`GET mini-apps/{id}/statistics`). */
@Serializable
data class MiniappStatistics(
    @SerialName("users_count") val usersCount: Int = 0,
    @SerialName("courses_count") val coursesCount: Int = 0,
    @SerialName("total_sales_count") val totalSalesCount: Int = 0,
    @SerialName("current_month_sales_count") val currentMonthSalesCount: Int = 0,
    @SerialName("current_month_sales_amount") val currentMonthSalesAmount: Long = 0,
    @SerialName("total_sales_amount") val totalSalesAmount: Long = 0,
)
