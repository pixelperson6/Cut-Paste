package com.codingwithpix3l.imagepicker.internal.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.codingwithpix3l.imagepicker.internal.logic.MatissePreviewViewState
import com.codingwithpix3l.imagepicker.internal.theme.LocalMatisseTheme
import kotlin.math.absoluteValue


@Composable
internal fun MatissePreviewPage(viewState: MatissePreviewViewState, activity: Activity) {
    BackHandler(enabled = viewState.visible, onBack = viewState.onDismissRequest)
    AnimatedVisibility(
        modifier = Modifier,
        visible = viewState.visible,
        enter = slideInHorizontally(animationSpec = tween(
            durationMillis = 300,
            easing = LinearEasing
        ), initialOffsetX = { it }),
        exit = slideOutHorizontally(animationSpec = tween(
            durationMillis = 300,
            easing = LinearEasing
        ), targetOffsetX = { it }),
    ) {
        val matisse = viewState.matisse
        val initialPage = viewState.initialPage
        val previewResources = viewState.previewResources
        val selectedMediaResources = viewState.selectedResources
        val onMediaCheckChanged = viewState.onMediaCheckChanged
        val pagerState = rememberPagerState(initialPage = initialPage)
        var currentPageIndex by remember {
            mutableStateOf(initialPage)
        }

        val onShareFile = viewState.onShareFile
        val onDeleteFile = viewState.onDeleteFile

        LaunchedEffect(key1 = Unit) {
            snapshotFlow {
                pagerState.currentPage
            }.collect {
                currentPageIndex = it
            }
        }
        Scaffold(
            modifier = Modifier,
            backgroundColor = LocalMatisseTheme.current.onPreviewSurfaceColor
        ) { paddingValues ->
            if (previewResources.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    HorizontalPager(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = paddingValues,
                        count = previewResources.size,
                        state = pagerState,
                        key = { index ->
                            previewResources[index].key
                        },
                    ) { pageIndex ->
                        val mediaResource = previewResources[pageIndex]
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(alignment = Alignment.Center)
                                .verticalScroll(state = rememberScrollState())
                                .graphicsLayer {
                                    val pageOffset =
                                        calculateCurrentOffsetForPage(pageIndex).absoluteValue
                                    lerp(
                                        start = 0.85f,
                                        stop = 1f,
                                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                    ).also { scale ->
                                        scaleX = scale
                                        scaleY = scale
                                    }
                                    alpha = lerp(
                                        start = 0.5f,
                                        stop = 1f,
                                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                    )
                                },
                            model = mediaResource.uri,
                            contentScale = ContentScale.FillWidth,
                            contentDescription = null
                        )
                    }
                    val index = selectedMediaResources.indexOf(previewResources[currentPageIndex])
                    val isSelected = index > -1
                    val enabled = isSelected || selectedMediaResources.size < matisse.maxSelectable
                    if (matisse.saveDFolderName.isEmpty()) {
                        MatisseCheckbox(
                            modifier = Modifier
                                .align(alignment = Alignment.TopEnd)
                                .statusBarsPadding()
                                .padding(top = 20.dp, end = 30.dp),
                            theme = LocalMatisseTheme.current.checkBoxTheme,
                            text = if (isSelected) {
                                (index + 1).toString()
                            } else {
                                ""
                            },
                            checked = isSelected,
                            enabled = enabled,
                            onCheckedChange = {
                                onMediaCheckChanged(previewResources[currentPageIndex])
                            }
                        )
                    } else {

                        IconButton(modifier = Modifier
                            .align(alignment = Alignment.TopEnd)
                            .statusBarsPadding()
                            .padding(top = 20.dp, end = 30.dp),
                            onClick = {
                                onShareFile(activity, previewResources[currentPageIndex])

                            }) {
                            Icon(imageVector = Icons.Default.Share, tint = Color.White, contentDescription = "share")

                        }
                    }
                }
            }
        }
    }
}