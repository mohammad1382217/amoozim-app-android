package com.amoozim.creator.feature.miniapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.amoozim.creator.core.common.formatNumber
import com.amoozim.creator.core.designsystem.component.AmoozimTopBar
import com.amoozim.creator.core.designsystem.component.ErrorState
import com.amoozim.creator.core.designsystem.component.LoadingState
import com.amoozim.creator.core.designsystem.theme.LocalAmoozimColors
import com.amoozim.creator.feature.miniapp.StatisticsViewModel
import com.amoozim.creator.feature.miniapp.model.MiniappStatistics

/**
 * Wallet tab: the mini-app statistics report ("گزارش‌ها"), matching the web wallet
 * screen's four-metric summary. A top-level tab, so no back action.
 */
@Composable
fun WalletStatsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        AmoozimTopBar(title = "کیف پول")

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> LoadingState()
                uiState.errorMessage != null ->
                    ErrorState(message = uiState.errorMessage!!, onRetry = viewModel::load)
                uiState.statistics != null -> StatisticsReport(stats = uiState.statistics!!)
            }
        }
    }
}

@Composable
private fun StatisticsReport(stats: MiniappStatistics) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "گزارش‌ها",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        StatRow(icon = Icons.Filled.MenuBook, label = "تعداد دوره‌ها", value = formatNumber(stats.coursesCount))
        StatRow(icon = Icons.Filled.Group, label = "تعداد کاربران", value = formatNumber(stats.usersCount))
        StatRow(icon = Icons.Filled.Payments, label = "میزان فروش", value = "${formatNumber(stats.totalSalesAmount)} تومان")
        StatRow(icon = Icons.Filled.ReceiptLong, label = "تعداد فروش", value = "${formatNumber(stats.totalSalesCount)} دوره")
    }
}

@Composable
private fun StatRow(icon: ImageVector, label: String, value: String) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = LocalAmoozimColors.current.content2,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}
