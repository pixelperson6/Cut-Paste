package com.codingwithpix3l.photoonphoto.core.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.codingwithpix3l.photoonphoto.R
import com.codingwithpix3l.photoonphoto.model.TemplateImage
import com.codingwithpix3l.photoonphoto.ui.background.changer.EditorViewModel
import com.codingwithpix3l.photoonphoto.ui.theme.backgroundColors
import com.google.accompanist.flowlayout.FlowRow


@Composable
fun ChangerBgOption(
    modifier: Modifier = Modifier,
    // photoEditor: PhotoEditor,
    viewModel: EditorViewModel = viewModel(),
    onGalleryClicked: () -> Unit,
    onCustomClicked: () -> Unit,
    onTemplateClicked: () -> Unit,
    onColorSelected: (color: Color) -> Unit,
    onImageSelected: (url: String, url2: String) -> Unit,
    onAddImageClicked: () -> Unit,
) {
    val context = LocalContext.current

    viewModel.fetchOptionList(context)
    /*   var selectedOption by remember {
           mutableStateOf(ChangerMode.CUSTOM)
       }*/

    var selectedIndex by remember {
        mutableStateOf("CUSTOM")
    }

    Column(modifier = modifier) {

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(viewModel.optionList.value) { item ->
                val selected = selectedIndex == item
                ChangerOption(
                    modifier = Modifier.selectable(
                        selected = selected,
                        onClick = {
                            selectedIndex = item
                            if (selectedIndex != "CUSTOM" && selectedIndex != "COLOR") {
                                viewModel.fetchImages(context, selectedIndex)
                            }

                        }
                    ),
                    selected = selected,
                    label = item
                )
            }
            /*items(ChangerMode.values()) { mode ->
                val selected = selectedOption == mode
                ChangerOption(
                    modifier = Modifier.selectable(
                        selected = selected,
                        onClick = {
                            selectedOption = mode
                        }
                    ),
                    selected = selected,
                    label = mode.name
                )

            }*/
        }


        when (selectedIndex) {

            "CUSTOM" -> {
                CustomOptions(
                    onNoBgClicked = {
                        onCustomClicked()
                    },
                    onGalleryClicked = {
                        onGalleryClicked()

                    },
                    onTemplateClicked = {
                        /*  photoEditor.clearAllViews()
                          photoEditor.setMainSource(viewModel.frontBitmap.value)
                          photoEditor.addImage(viewModel.frontBitmap.value)
                          photoEditor.addFrame(viewModel.frontBitmap.value)*/
                        onTemplateClicked()
                    },
                    isFreeCollage = viewModel.isFreeCollage,
                    onAddImageClicked = {
                        onAddImageClicked()
                    }
                )
            }
            "COLOR" -> {
                ColorOptions(onColorSelected = {
                    onColorSelected(it)

                })
            }
            else -> {
                ImageOptions(onImageClicked = { bg, fg ->
                    onImageSelected(bg, fg)
                }, imageList = viewModel.result.value)
            }
        }
    }
    /*   in 2..viewModel.optionList.value.size ->{
           viewModel.fetchImages(LocalContext.current,viewModel.optionList.value[selectedIndex])

           when (viewModel.result.value.status) {
               is CallbackStatus.SUCCESS -> {
                   ImageOptions(onImageClicked = { bg, fg ->
                       onImageSelected(bg, fg)
                   }, imageList = viewModel.result.value.images.toList())
               }
               else -> {
                   ImageOptions(onImageClicked = { bg, fg ->
                       onImageSelected(bg, fg)
                   })
               }
           }
       }*/

}


/*@Composable
private fun DialogButton(
    initialColor: Color,
    onColorChanged: (Color) -> Unit,
) {
    var showDialog by remember {
        mutableStateOf(false)
    }
    Button(
        onClick = {
            showDialog = true
        },
    ) {
        Text(text = "Show dialog")
    }
    val state = rememberAlwanState(initialColor = initialColor)
    if (showDialog) {
        AlwanDialog(
            onColorChanged = {},
            state = state,
            onDismissRequest = {
                showDialog = false
            },
            showAlphaSlider = true,
            positiveButtonText = "OK",
            onPositiveButtonClick = {
                showDialog = false
                onColorChanged(state.color)
            },
            negativeButtonText = "Cancel",
            onNegativeButtonClick = {
                showDialog = false
            },
        )
    }
}*/


@Composable
fun ImageOptions(
    onImageClicked: (image: String, image2: String) -> Unit,
    imageList: List<TemplateImage>
) {

    var selectedOption by remember { mutableStateOf("") }

    LazyRow(contentPadding = PaddingValues(8.dp)) {
        items(imageList) { item ->
            val selected = selectedOption == item.bgUrl
            ChangerImageItem(modifier = Modifier
                .size(80.dp)
                .padding(8.dp)
                .border(
                    width = 2.dp,
                    color = if (selected) Color.White else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                )
                .selectable(
                    selected = selected,
                    onClick = {
                        selectedOption = item.bgUrl
                        onImageClicked(item.bgUrl, item.fgUrl)
                    }
                ),
                imgUri = item.thumb.ifEmpty { item.bgUrl })
        }
    }
}

@Composable
fun ChangerImageItem(
    modifier: Modifier = Modifier,
    imgUri: String,
    proFeature: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
    ) {
        val painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current).data(data = imgUri)
                .apply(block = fun ImageRequest.Builder.() {
                    placeholder(R.drawable.gallery)
                    error(R.drawable.gallery)
                }).build()
        )
        Image(
            painter = painter,
            contentDescription = "image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )
        if (proFeature) {
            Icon(
                modifier = Modifier
                    .size(25.dp)
                    .padding(top = 8.dp, end = 8.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(MaterialTheme.colors.primary)
                    .align(Alignment.TopEnd),
                imageVector = Icons.Default.KingBed,
                contentDescription = "Pro"
            )
        }

    }


}

@Composable
fun CustomOptions(
    modifier: Modifier = Modifier,
    onNoBgClicked: () -> Unit,
    onGalleryClicked: () -> Unit,
    onTemplateClicked: () -> Unit,
    isFreeCollage:Boolean = false,
    onAddImageClicked: () -> Unit,
) {

    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly

    ) {
        val customModifier = Modifier
            .size(90.dp)
            .padding(8.dp)
            .background(MaterialTheme.colors.secondary, RoundedCornerShape(15.dp))

        if (isFreeCollage){
            IconButton(
                onClick = {
                    onAddImageClicked()
                },
                modifier = customModifier
            ) {
                Icon(modifier=Modifier.size(35.dp),painter = painterResource(id = R.drawable.add_image), contentDescription = "Background")
            }
        }else{
            IconButton(
                onClick = {
                    onNoBgClicked()
                },
                modifier = customModifier
            ) {
                Icon(modifier=Modifier.size(35.dp), imageVector=Icons.Default.NotInterested, contentDescription = "Background")
            }
        }
        IconButton(
            onClick = {
                onGalleryClicked()
            },
            modifier = customModifier
        ) {
            Icon(modifier=Modifier.size(35.dp),painter = painterResource(id = R.drawable.gallery), contentDescription = "Background")
        }
        IconButton(
            onClick = {
                onTemplateClicked()
            },
            modifier = customModifier
        ) {
            Icon(modifier=Modifier.size(35.dp),painter = painterResource(id = R.drawable.unsplash_icon), contentDescription = "Background")
        }
    }

}

@Composable
fun ColorOptions(
    modifier: Modifier = Modifier,
    onColorSelected: (color: Color) -> Unit,
    viewModel: EditorViewModel = viewModel(),
) {
    var selectedOption by remember {
        mutableStateOf(Color.White)
    }
    val verticalScrollState = rememberScrollState()

    FlowRow(
        modifier = modifier
            .padding(8.dp)
            .selectableGroup()
            .verticalScroll(verticalScrollState),
        mainAxisSpacing = 8.dp,
        crossAxisSpacing = 8.dp
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .shadow(5.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .border(
                    width = 2.dp,
                    color = Color.Black,
                    shape = CircleShape
                )
                .selectable(
                    selected = selectedOption == Color.Transparent,
                    onClick = {
                        // selectedOption = Color.White
                        //open color picker and pick 1 color and pass it to the onColorSelected lambda function
                        viewModel.changeDialogVisibility(true)
                        selectedOption = Color.Transparent


                    }
                ),
            contentAlignment = Alignment.Center
        )
        {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
        }

        backgroundColors.forEach { color ->
            val selected = selectedOption == color
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .shadow(5.dp, CircleShape)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = 2.dp,
                        color = if (selected) {
                            Color.White
                        } else {
                            Color.Transparent
                        },
                        shape = CircleShape
                    )
                    .selectable(
                        selected = selected,
                        onClick = {
                            selectedOption = color
                            onColorSelected(color)
                        }
                    )
            ) {
                if (selected) {
                    Icon(
                        modifier = Modifier.align(Alignment.Center),
                        imageVector = Icons.Default.Done,
                        contentDescription = "Edit"
                    )
                }
            }
        }
    }
}

@Composable
fun ChangerOption(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    label: String,
) {
    Text(
        modifier = modifier
            .padding(8.dp),
        text = label,
        style = MaterialTheme.typography.button,
        color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSecondary,
        textAlign = TextAlign.Center,
    )
}

/*
enum class ChangerMode {
    CUSTOM, COLOR, GRADIENT, BIRTHDAY, ANNIVERSARY,
    FRAME, VALENTINES, LOVE, QUOTES,
    NEON, DRIP, FLAME, SKETCH,
    IPL, FESTIVAL, FLAG,
    WATERFALL, FLOWER, NATURE, CLOUD,
    PLACE, CITY, SELFIE,
    CARANDBIKE, ANIMAL,
    WALL, PAPER, PATTERN, PAINTING

}*/
