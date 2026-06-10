package com.amoozim.creator.feature.course.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.amoozim.creator.core.common.formatNumber
import com.amoozim.creator.core.designsystem.theme.LocalAmoozimColors
import com.amoozim.creator.core.network.StorageUrls
import com.amoozim.creator.feature.course.model.Course

/**
 * Learner course card: a 16:9 cover with a bottom gradient and overlaid title +
 * meta (lessons count, participants). Mirrors the web `CourseCard`.
 */
@Composable
fun CourseCard(
    course: Course,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coverModel = remember(course.id, course.coverUrl) {
        course.externalMedia?.mediaToken?.takeIf { it.isNotBlank() }?.let { StorageUrls.mediaFile(it) }
            ?: course.coverUrl?.takeIf { it.isNotBlank() }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(MaterialTheme.shapes.large)
            .background(LocalAmoozimColors.current.content4)
            .clickable(onClick = onClick),
    ) {
        if (coverModel != null) {
            AsyncImage(
                model = coverModel,
                contentDescription = course.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xB3000000)))),
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = course.title,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                MetaItem(icon = Icons.Filled.MenuBook, text = "${formatNumber(course.lessonsCount ?: 0)} جلسه")
                if (course.settings?.showParticipantsCount == true) {
                    MetaItem(icon = Icons.Filled.Group, text = formatNumber(course.usersCount))
                }
            }
        }
    }
}

@Composable
private fun MetaItem(icon: ImageVector, text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = Color.White)
    }
}
