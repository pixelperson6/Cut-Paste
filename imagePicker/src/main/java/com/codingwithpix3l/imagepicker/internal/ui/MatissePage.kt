package com.codingwithpix3l.imagepicker.internal.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.codingwithpix3l.imagepicker.MediaResource
import com.codingwithpix3l.imagepicker.internal.logic.MatissePageAction
import com.codingwithpix3l.imagepicker.internal.logic.MatisseViewModel
import com.codingwithpix3l.imagepicker.internal.logic.MatisseViewState
import com.codingwithpix3l.imagepicker.internal.theme.LocalMatisseTheme


@Composable
internal fun MatissePage(
    viewState: MatisseViewState,
    action: MatissePageAction,
    vm: MatisseViewModel,
    activity:Activity
) {


    val matisse = viewState.matisse
    if (matisse.saveDFolderName.isNotEmpty()){
        vm.findMediaBucket("CutPaste Photos")
    }
    val selectedMediaResources = viewState.selectedResources
    val resources = viewState.selectedBucket.resources
    val supportCapture = viewState.selectedBucket.supportCapture
   // val onShareFile = viewState.onShareFile

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        backgroundColor = LocalMatisseTheme.current.surfaceColor,
        topBar = {
            MatisseTopBar(
                allBucket = viewState.allBucket,
                selectedBucket = viewState.selectedBucket,
                onSelectBucket = viewState.onSelectBucket,
                onClickBackMenu = action.onClickBackMenu,
                viewState = viewState.bottomBarViewState,
                onSureButtonClick = action.onSureButtonClick,
                matisse= matisse
            )
        },

        bottomBar = {
            if(matisse.saveDFolderName.isEmpty()){
                MatisseBottomBar(
                    viewState = viewState.bottomBarViewState,
                    selectedResources = selectedMediaResources,
                    onMediaCheckChanged = viewState.onMediaCheckChanged
                    //  onSureButtonClick = action.onSureButtonClick,
                )
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(count = matisse.spanCount),
            state = viewState.lazyGridState,
            contentPadding = PaddingValues(
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                top = innerPadding.calculateTopPadding(),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                bottom = innerPadding.calculateBottomPadding() + 60.dp
            )
        ) {
            if (supportCapture) {
                item(key = "Capture", contentType = "Capture", content = {
                    CaptureItem(onClick = action.onRequestCapture)
                })
            }
            items(
                count = resources.size,
                key = {
                    resources[it].key
                },
                contentType = {
                    "Album"
                },
                itemContent = { itemIndex ->
                    val resource = resources[itemIndex]
                    val index = selectedMediaResources.indexOf(resource)
                    val isSelected = index > -1
                    val enabled = isSelected || selectedMediaResources.size < matisse.maxSelectable
                    AlbumItem(
                        mediaResource = resource,
                        isSelected = isSelected,
                        enabled = enabled,
                        position = if (isSelected) {
                            (index + 1).toString()
                        } else {
                            ""
                        },
                        onClickMedia = viewState.onClickMedia,
                        onMediaCheckChanged = viewState.onMediaCheckChanged,
                        savedFolder = matisse.saveDFolderName,
                        vm = vm,
                        activity=activity
                    )
                }
            )
        }
    }

}




@Composable
private fun LazyGridItemScope.AlbumItem(
    mediaResource: MediaResource,
    isSelected: Boolean,
    enabled: Boolean,
    position: String,
    onClickMedia: (MediaResource) -> Unit,
    onMediaCheckChanged: (MediaResource) -> Unit,
    savedFolder: String,
    vm: MatisseViewModel,
    activity: Activity
) {
    Box(
        modifier = Modifier
            .animateItemPlacement()
            .padding(all = 1.dp)
            .aspectRatio(ratio = 1f)
            .clip(shape = RoundedCornerShape(size = 2.dp))
            .background(color = LocalMatisseTheme.current.imageBackgroundColor)
            .then(
                other = if (isSelected) {
                    Modifier.drawFrame(color = LocalMatisseTheme.current.checkBoxTheme.frameColor)
                } else {
                    Modifier
                }
            )
            .clickable {
                onClickMedia(mediaResource)
                //onMediaCheckChanged(mediaResource)
            }
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = mediaResource.uri,
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        if(savedFolder.isEmpty()) {
            MatisseCheckbox(
                modifier = Modifier
                    .align(alignment = Alignment.TopEnd)
                    .padding(all = 3.dp),
                theme = LocalMatisseTheme.current.checkBoxTheme,
                text = position,
                checked = isSelected,
                enabled = enabled,
                onCheckedChange = {
                    onMediaCheckChanged(mediaResource)
                    //onClickMedia(mediaResource)
                }
            )
        }else{

            IconButton( modifier = Modifier
                .align(alignment = Alignment.TopEnd)
                .padding(all = 3.dp),
                onClick = {
                    vm.shareImage(activity,mediaResource)

                }) {
                Icon( imageVector = Icons.Default.Share, tint = Color.White, contentDescription = "share")

            }
        }
    }
}

@Composable
private fun LazyGridItemScope.CaptureItem(onClick: () -> Unit) {
    val captureIconTheme = LocalMatisseTheme.current.captureIconTheme
    Box(
        modifier = Modifier
            .animateItemPlacement()
            .padding(all = 1.dp)
            .aspectRatio(ratio = 1f)
            .clip(shape = RoundedCornerShape(size = 2.dp))
            .background(color = captureIconTheme.backgroundColor)
            .clickable {
                onClick()
            }
    ) {
        Icon(
            modifier = Modifier
                .fillMaxSize(fraction = 0.5f)
                .align(alignment = Alignment.Center),
            imageVector = captureIconTheme.icon,
            tint = captureIconTheme.tint,
            contentDescription = null,
        )
    }
}

fun Modifier.drawFrame(color: Color): Modifier {
    return drawWithCache {
        val lineWidth = 3.dp.toPx()
        val topLeftPoint = lineWidth / 2f
        val rectSize = size.width - lineWidth
        onDrawWithContent {
            drawContent()
            drawRect(
                color = color,
                topLeft = Offset(topLeftPoint, topLeftPoint),
                size = Size(width = rectSize, height = rectSize),
                style = Stroke(width = lineWidth)
            )
        }
    }
}