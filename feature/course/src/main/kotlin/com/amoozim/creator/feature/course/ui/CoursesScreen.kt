package com.amoozim.creator.feature.course.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.amoozim.creator.core.designsystem.component.EmptyState
import com.amoozim.creator.core.designsystem.component.ErrorState
import com.amoozim.creator.core.designsystem.component.LoadingState
import com.amoozim.creator.feature.course.CoursesViewModel
import com.amoozim.creator.feature.miniapp.ui.MiniAppHeader

/**
 * Home tab: the mini-app header, a title search, and an infinite-scrolling course
 * list. The header + search are fixed; only the list scrolls (the project's
 * "fixed header + separate scroll" convention).
 */
@Composable
fun CoursesScreen(
    onCourseClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CoursesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            uiState.courses.isNotEmpty() && lastVisible >= uiState.courses.size - 3
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadNextPage()
    }

    Column(modifier = modifier.fillMaxSize()) {
        MiniAppHeader()

        OutlinedTextField(
            value = uiState.query,
            onValueChange = viewModel::onQueryChange,
            placeholder = { Text("جستجوی دوره") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading && uiState.courses.isEmpty() -> LoadingState()
                uiState.errorMessage != null && uiState.courses.isEmpty() ->
                    ErrorState(message = uiState.errorMessage!!, onRetry = viewModel::loadFirstPage)
                uiState.courses.isEmpty() -> EmptyState(message = "هیچ دوره‌ای یافت نشد")
                else -> LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(items = uiState.courses, key = { it.id }) { course ->
                        CourseCard(course = course, onClick = { onCourseClick(course.id) })
                    }
                    if (uiState.isLoadingMore) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}
