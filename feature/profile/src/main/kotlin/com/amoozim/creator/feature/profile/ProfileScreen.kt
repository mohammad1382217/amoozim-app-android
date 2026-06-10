package com.amoozim.creator.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.amoozim.creator.core.designsystem.component.AmoozimTopBar
import com.amoozim.creator.core.designsystem.component.ErrorState
import com.amoozim.creator.core.designsystem.component.LoadingState
import com.amoozim.creator.core.model.Profile

@Composable
fun ProfileScreen(
    onSignedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        AmoozimTopBar(title = "پروفایل")

        val profile = uiState.profile
        when {
            uiState.isLoading && profile == null -> LoadingState()
            profile == null -> ErrorState(
                message = uiState.errorMessage ?: "اطلاعات کاربر در دسترس نیست",
                onRetry = viewModel::refresh,
            )
            else -> ProfileContent(
                profile = profile,
                onSignOut = {
                    viewModel.signOut()
                    onSignedOut()
                },
            )
        }
    }
}

@Composable
private fun ProfileContent(profile: Profile, onSignOut: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val avatarModifier = Modifier
            .size(88.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
        if (profile.avatar.isNotBlank()) {
            AsyncImage(
                model = profile.avatar,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = avatarModifier,
            )
        } else {
            Box(modifier = avatarModifier)
        }

        Text(
            text = profile.displayName.ifBlank { profile.name.ifBlank { profile.username } },
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        if (profile.username.isNotBlank()) {
            Text(
                text = "@${profile.username}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = roleLabel(profile),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
        )
        profile.phone?.takeIf { it.isNotBlank() }?.let { phone ->
            Text(text = phone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Box(modifier = Modifier.weight(1f))

        // NOTE: the web app intentionally has no logout UI. This local sign-out is a
        // dev convenience for a standalone install (clears tokens, returns to entry).
        OutlinedButton(onClick = onSignOut, modifier = Modifier.fillMaxWidth()) {
            Text("خروج از حساب")
        }
    }
}

private fun roleLabel(profile: Profile): String = when {
    profile.isSuperAdmin == true -> "مدیر کل"
    profile.role == Profile.ROLE_OWNER -> "مالک"
    profile.role == Profile.ROLE_ADMIN -> "مدیر"
    else -> "کاربر"
}
