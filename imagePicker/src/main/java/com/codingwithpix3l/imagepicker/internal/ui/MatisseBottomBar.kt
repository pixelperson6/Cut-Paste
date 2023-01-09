package com.codingwithpix3l.imagepicker.internal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.codingwithpix3l.imagepicker.MediaResource
import com.codingwithpix3l.imagepicker.internal.logic.MatisseBottomBarViewState
import com.codingwithpix3l.imagepicker.internal.theme.LocalMatisseTheme


@Composable
internal fun MatisseBottomBar(
    viewState: MatisseBottomBarViewState
/*, onSureButtonClick: () -> Unit*/,
    selectedResources: List<MediaResource>,
    onMediaCheckChanged: (MediaResource) -> Unit
) {
    val bottomNavigationTheme = LocalMatisseTheme.current.bottomNavigationTheme
    val previewButtonTheme = LocalMatisseTheme.current.previewButtonTheme
    // val sureButtonTheme = LocalMatisseTheme.current.sureButtonTheme
    val alphaIfDisable = LocalMatisseTheme.current.alphaIfDisable
    BottomNavigation(
        modifier=Modifier.height(84.dp),
        backgroundColor = bottomNavigationTheme.backgroundColor,
        elevation = 16.dp
    ) {
        if (viewState.previewText.isNotBlank()) {
            val previewTextStyle = previewButtonTheme.textStyle.let {
                if (viewState.previewButtonClickable) {
                    it
                } else {
                    it.copy(color = it.color.copy(alpha = alphaIfDisable))
                }
            }
            Text(
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .then(other = if (viewState.previewButtonClickable) {
                        Modifier.clickable {
                            viewState.onPreviewButtonClick()
                        }
                    } else {
                        Modifier
                    })
                    .fillMaxHeight()
                    .padding(horizontal = 8.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically),
                textAlign = TextAlign.Center,
                style = previewTextStyle,
                text = viewState.previewText,
            )
        }
        LazyRow(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
           horizontalArrangement = Arrangement.Center,
           // verticalAlignment = Alignment.CenterVertically
        ) {
            items(selectedResources) { resource ->

                PreviewMediaItem(
                    mediaResource = resource,
                  /*  onClickMedia = {
                        viewState.onPreviewButtonClick()
                    },*/
                    onMediaCheckChanged = onMediaCheckChanged
                )


            }

        }


        /*       val sureButtonColor = if (viewState.sureButtonClickable) {
                   sureButtonTheme.backgroundColor
               } else {
                   sureButtonTheme.backgroundColor.copy(alpha = alphaIfDisable)
               }
               val sureButtonTextStyle = sureButtonTheme.textStyle.let {
                   if (viewState.sureButtonClickable) {
                       it
                   } else {
                       it.copy(it.color.copy(alpha = alphaIfDisable))
                   }
               }
               Text(
                   modifier = Modifier
                       .align(alignment = Alignment.CenterVertically)
                       .weight(weight = 1f, fill = false)
                       .padding(end = 22.dp)
                       .clip(shape = RoundedCornerShape(size = 22.dp))
                       .background(color = sureButtonColor)
                       .then(other = if (viewState.sureButtonClickable) {
                           Modifier.clickable {
                               onSureButtonClick()
                           }
                       } else {
                           Modifier
                       })
                       .padding(horizontal = 22.dp, vertical = 8.dp),
                   text = viewState.sureText,
                   textAlign = TextAlign.Center,
                   style = sureButtonTextStyle
               )*/
    }
}

@Composable
private fun PreviewMediaItem(
    mediaResource: MediaResource,
    //onClickMedia: (MediaResource) -> Unit,
    onMediaCheckChanged: (MediaResource) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(all = 1.dp)
            .aspectRatio(ratio = 1f)
            .clip(shape = RoundedCornerShape(size = 2.dp))
            .background(color = LocalMatisseTheme.current.imageBackgroundColor)
            .drawFrame(color = LocalMatisseTheme.current.checkBoxTheme.frameColor)
        /*   .clickable {
               //onClickMedia(mediaResource)
               onMediaCheckChanged(mediaResource)
           }*/
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = mediaResource.uri,
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Icon(
            modifier = Modifier
                .align(alignment = Alignment.TopEnd)
                .padding(all = 3.dp)
                .clickable {
                    onMediaCheckChanged(mediaResource)
                },
            imageVector = Icons.Default.Cancel,
            contentDescription = "remove"
        )
    }
}