package com.amoozim.creator.feature.course.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.amoozim.creator.core.common.formatNumber
import com.amoozim.creator.core.designsystem.component.AmoozimTopBar
import com.amoozim.creator.core.designsystem.component.ErrorState
import com.amoozim.creator.core.designsystem.component.LoadingState
import com.amoozim.creator.core.designsystem.theme.LocalAmoozimColors
import com.amoozim.creator.core.network.StorageUrls
import com.amoozim.creator.feature.course.CourseDetailViewModel
import com.amoozim.creator.feature.course.model.Course
import com.amoozim.creator.feature.course.model.Lesson

/** Course detail: cover/description/price header followed by the ordered lesson list. */
@Composable
fun CourseDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CourseDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        AmoozimTopBar(title = uiState.course?.title ?: "دوره", onBack = onBack)

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> LoadingState()
                uiState.errorMessage != null ->
                    ErrorState(message = uiState.errorMessage!!, onRetry = viewModel::load)
                else -> {
                    val course = uiState.course
                    if (course != null) {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            item { CourseHeaderSection(course) }
                            item {
                                Text(
                                    text = "جلسه‌ها",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(top = 8.dp),
                                )
                            }
                            if (uiState.lessons.isEmpty()) {
                                item {
                                    Text(
                                        text = "هیچ جلسه‌ای برای این دوره وجود ندارد",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            } else {
                                itemsIndexed(uiState.lessons, key = { _, lesson -> lesson.id }) { index, lesson ->
                                    LessonRow(
                                        lesson = lesson,
                                        index = index,
                                        locked = !uiState.canAccessContent && !lesson.isFree,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseHeaderSection(course: Course) {
    val coverModel = course.externalMedia?.mediaToken?.takeIf { it.isNotBlank() }?.let { StorageUrls.mediaFile(it) }
        ?: course.coverUrl?.takeIf { it.isNotBlank() }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(MaterialTheme.shapes.large)
                .background(LocalAmoozimColors.current.content4),
        ) {
            if (coverModel != null) {
                AsyncImage(
                    model = coverModel,
                    contentDescription = course.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        Text(text = course.title, style = MaterialTheme.typography.titleLarge)
        if (course.description.isNotBlank()) {
            Text(
                text = course.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = if (course.finalPrice <= 0) "رایگان" else "${formatNumber(course.finalPrice)} تومان",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun LessonRow(lesson: Lesson, index: Int, locked: Boolean) {
    val number = (lesson.sortOrder ?: index) + 1
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = LocalAmoozimColors.current.content2,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = if (locked) Icons.Filled.Lock else Icons.Filled.PlayCircleOutline,
                contentDescription = null,
                tint = if (locked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = "جلسه ${formatNumber(number)}: ${lesson.title}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (lesson.isFree) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Text(
                        text = "رایگان",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                }
            }
        }
    }
}
