package com.amoozim.creator.feature.miniapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.amoozim.creator.feature.miniapp.MiniAppHeaderState
import com.amoozim.creator.feature.miniapp.MiniAppViewModel
import com.amoozim.creator.feature.miniapp.model.MiniApp

/**
 * Mini-app identity header (avatar + title + channel handle), the analogue of the
 * web `MiniAppBar`. Self-contained: it loads its own data via [MiniAppViewModel], so
 * any tab screen can drop it in. Non-critical, so it stays silent on error.
 */
@Composable
fun MiniAppHeader(
    modifier: Modifier = Modifier,
    viewModel: MiniAppViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (val current = state) {
        MiniAppHeaderState.Loading -> HeaderRow(image = null, title = "در حال بارگذاری…", subtitle = null, modifier = modifier)
        is MiniAppHeaderState.Ready -> HeaderContent(miniApp = current.miniApp, modifier = modifier)
        is MiniAppHeaderState.Failed -> Unit
    }
}

@Composable
private fun HeaderContent(miniApp: MiniApp, modifier: Modifier = Modifier) {
    HeaderRow(
        image = miniApp.displayImage,
        title = miniApp.title.ifBlank { "برنامک" },
        subtitle = miniApp.channelUsername?.takeIf { it.isNotBlank() },
        modifier = modifier,
    )
}

@Composable
private fun HeaderRow(
    image: String?,
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val avatarModifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
        if (image != null) {
            AsyncImage(
                model = image,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = avatarModifier,
            )
        } else {
            Box(modifier = avatarModifier)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
