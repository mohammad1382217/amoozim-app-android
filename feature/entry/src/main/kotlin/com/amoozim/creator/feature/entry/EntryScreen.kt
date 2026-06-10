package com.amoozim.creator.feature.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Session-bootstrap screen. There is no username/password login in Amoozim — the web
 * app receives an Eitaa `initData` blob and exchanges it for a JWT. On a standalone
 * Android install that blob comes from the host bridge or, in dev, is pasted here.
 */
@Composable
fun EntryScreen(
    onAuthenticated: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EntryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.authenticated) {
        if (uiState.authenticated) onAuthenticated()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "ورود به آموزیم", style = MaterialTheme.typography.titleLarge)
        Text(
            text = "برای ورود، شناسه برنامک و مقدار initData دریافتی از میزبان را وارد کنید.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ModeButton(
                text = "initData",
                selected = !uiState.useDevTokens,
                onClick = { viewModel.setUseDevTokens(false) },
            )
            ModeButton(
                text = "توکن (توسعه)",
                selected = uiState.useDevTokens,
                onClick = { viewModel.setUseDevTokens(true) },
            )
        }

        OutlinedTextField(
            value = uiState.miniAppId,
            onValueChange = viewModel::onMiniAppIdChange,
            label = { Text("شناسه برنامک (Miniapp UUID)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        if (uiState.useDevTokens) {
            OutlinedTextField(
                value = uiState.accessToken,
                onValueChange = viewModel::onAccessTokenChange,
                label = { Text("Access token") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 96.dp),
            )
            OutlinedTextField(
                value = uiState.refreshToken,
                onValueChange = viewModel::onRefreshTokenChange,
                label = { Text("Refresh token") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 96.dp),
            )
        } else {
            OutlinedTextField(
                value = uiState.initData,
                onValueChange = viewModel::onInitDataChange,
                label = { Text("initData") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
            )
        }

        OutlinedTextField(
            value = uiState.deviceId,
            onValueChange = viewModel::onDeviceIdChange,
            label = { Text("شناسه دستگاه (اختیاری)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth(),
        )

        uiState.errorMessage?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = viewModel::submit,
            enabled = !uiState.isSubmitting,
            modifier = Modifier.fillMaxWidth().height(48.dp),
        ) {
            if (uiState.isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.height(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
            } else {
                Text("ورود")
            }
        }
    }
}

@Composable
private fun ModeButton(text: String, selected: Boolean, onClick: () -> Unit) {
    if (selected) {
        Button(onClick = onClick) { Text(text) }
    } else {
        OutlinedButton(onClick = onClick) { Text(text) }
    }
}
