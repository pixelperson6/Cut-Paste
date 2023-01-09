package com.codingwithpix3l.imagepicker.internal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.codingwithpix3l.imagepicker.Matisse
import com.codingwithpix3l.imagepicker.internal.logic.MatisseBottomBarViewState
import com.codingwithpix3l.imagepicker.internal.logic.MediaBucket
import com.codingwithpix3l.imagepicker.internal.theme.LocalMatisseTheme


@Composable
internal fun MatisseTopBar(
    allBucket: List<MediaBucket>,
    selectedBucket: MediaBucket,
    onClickBackMenu: () -> Unit,
    onSelectBucket: (MediaBucket) -> Unit,
    onSureButtonClick: () -> Unit,
    viewState: MatisseBottomBarViewState,
    matisse: Matisse
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    val sureButtonTheme = LocalMatisseTheme.current.sureButtonTheme
    val alphaIfDisable = LocalMatisseTheme.current.alphaIfDisable
    val topAppBarTheme = LocalMatisseTheme.current.topAppBarTheme
    TopAppBar(
        backgroundColor = topAppBarTheme.backgroundColor,
        modifier = Modifier.statusBarsPadding(),
        navigationIcon = {
            Box {
                IconButton(
                    modifier = Modifier,
                    content = {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIos,
                            tint = topAppBarTheme.contentColor,
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        onClickBackMenu()
                    },
                )
                if(matisse.saveDFolderName.isEmpty()) {
                    BucketDropdownMenu(
                        allBucket = allBucket,
                        menuExpanded = menuExpanded,
                        onDismissRequest = {
                            menuExpanded = false
                        },
                        onSelectBucket = onSelectBucket,
                    )
                }
            }
        },
        title = {
            Row(
                modifier = Modifier
                    .padding(end = 30.dp)
                    .clickable {
                        menuExpanded = true
                    }
                    .padding(all = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(weight = 1f, fill = false),
                    text = selectedBucket.bucketDisplayName,
                    color = topAppBarTheme.contentColor,
                    fontSize = topAppBarTheme.fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start
                )
                if(matisse.saveDFolderName.isEmpty()) {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        tint = topAppBarTheme.contentColor,
                        contentDescription = null,
                    )
                }
            }
        },
        actions = {

            if(matisse.saveDFolderName.isEmpty()) {


                val sureButtonColor = if (viewState.sureButtonClickable) {
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
                )
            }
        }
    )
}

@Composable
private fun BucketDropdownMenu(
    allBucket: List<MediaBucket>,
    menuExpanded: Boolean,
    onDismissRequest: () -> Unit,
    onSelectBucket: (MediaBucket) -> Unit,
) {
    val dropdownMenuTheme = LocalMatisseTheme.current.dropdownMenuTheme
    DropdownMenu(
        modifier = Modifier
            .wrapContentSize(align = Alignment.TopStart)
            .background(color = dropdownMenuTheme.backgroundColor)
            .widthIn(min = 200.dp)
            .heightIn(max = 400.dp),
        offset = DpOffset(x = 10.dp, y = 0.dp),
        expanded = menuExpanded,
        onDismissRequest = onDismissRequest
    ) {
        allBucket.forEach { bucket ->
            DropdownMenuItem(
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .fillMaxWidth(),
                content = {
                    AsyncImage(
                        modifier = Modifier
                            .size(size = 50.dp)
                            .clip(shape = RoundedCornerShape(size = 2.dp))
                            .background(color = LocalMatisseTheme.current.imageBackgroundColor),
                        model = bucket.bucketDisplayIcon,
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                    )
                    Text(
                        modifier = Modifier
                            .weight(weight = 1f, fill = false)
                            .padding(start = 6.dp),
                        text = bucket.bucketDisplayName,
                        style = dropdownMenuTheme.textStyle,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                    )
                    Text(
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp),
                        text = "(${bucket.resources.size})",
                        style = dropdownMenuTheme.textStyle,
                        maxLines = 1,
                    )
                }, onClick = {
                    onDismissRequest()
                    onSelectBucket(bucket)
                })
        }
    }
}