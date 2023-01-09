package com.codingwithpix3l.photoonphoto.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codingwithpix3l.photoonphoto.R
import com.codingwithpix3l.photoonphoto.ui.background.changer.EditorViewModel

@Composable
fun ChangerImageOption(
    modifier: Modifier = Modifier,
    onDonePressed: () -> Unit,
    onErasePressed: () -> Unit,
    onDeletePressed: () -> Unit,
    viewModel: EditorViewModel = viewModel()

    ) {

    var selectedOption by remember {
        mutableStateOf(ChangerImageMode.OPACITY)
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.Top) {

        val optionModifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f)

        when (selectedOption) {

            ChangerImageMode.OPACITY -> {
                SingleSlider(modifier = optionModifier, onSlider1Change = {
                    viewModel.imageView?.alpha = it/100
                }, label = "Opacity", position = 100f)
            }
            ChangerImageMode.FLIP -> {
                var scaleX by remember {
                    mutableStateOf(1f)
                }
                var scaleY by remember {
                    mutableStateOf(1f)
                }
                FlipOption(modifier = optionModifier, onVerticalFlip = {
                    scaleY =  if (scaleY == 1f){
                        -1f

                    }else{
                        1f
                    }
                    viewModel.imageView?.scaleY = scaleY

                }, onHorizontalFlip = {

                    scaleX = if (scaleX == 1f){
                        -1f
                    }else{
                        1f
                    }
                    viewModel.imageView?.scaleX = scaleX

                })
            }
            /* ChangerImageMode.STROKE -> {
              var color by remember {
                  mutableStateOf(Color.Black)
              }
              Row(
                  modifier = Modifier
                      .fillMaxSize()
              ) {
                  SingleSlider(onSlider1Change = {
                      viewModel.addStroke(stroke = it)
                  }, label = "Stroke")
                  ColorOptions(onColorSelected = {
                      color = it
                  })
              }
          }

          ChangerImageMode.SHADOW -> {
              SingleSlider(modifier = optionModifier, onSlider1Change = {
                  viewModel.addShadow(size = it.toInt())

              }, label = "Shadow")
          }
          */
            /*      ChangerImageMode.BRIGHTNESS -> {
                      SingleSlider(modifier = optionModifier,onSlider1Change = {}label = "Brightness")
                  }
                  ChangerImageMode.CONTRAST -> {
                      SingleSlider(modifier = optionModifier,onSlider1Change = {}label = "Contrast")
                  }
                  ChangerImageMode.SATURATION -> {
                      SingleSlider(modifier = optionModifier,onSlider1Change = {}label = "Saturation")
                  }
                  ChangerImageMode.BEAUTIFY -> {
                      SingleSlider(modifier = optionModifier,onSlider1Change = {}label = "Beautify")
                  }*/

        }

        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {

            TextImageButton(
                modifier = Modifier.clickable {
                    onDonePressed()
                },
                iconPainter = painterResource(id = R.drawable.done),
                label = "Done",
                description = "Done"
            )
            Divider(
                modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .width(2.dp)
                    .align(Alignment.CenterVertically)
            )
            if(viewModel.isFreeCollage){
                TextImageButton(
                    modifier = Modifier.clickable {
                        onErasePressed()
                    },
                    iconPainter = painterResource(id = R.drawable.edit_icon),
                    label = "Edit",
                    description = "eraser"
                )
                TextImageButton(
                    modifier = Modifier.clickable {
                        onDeletePressed()
                    },
                    iconPainter = painterResource(id = R.drawable.delete),
                    label = "Delete",
                    description = "delete"
                )
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(ChangerImageMode.values()) { mode ->
                    val selected = selectedOption == mode
                    TextImageButton(
                        modifier = Modifier
                            .selectable(
                                selected = selected,
                                onClick = {
                                    selectedOption = mode
                                }
                            ),
                        selected = selected,
                        iconPainter = painterResource(id = mode.resourceId),
                        label = mode.name,
                        description = mode.name
                    )
                }
            }
        }
    }
}


enum class ChangerImageMode(val resourceId: Int) {
    OPACITY(R.drawable.opacity),
  //  SHADOW(R.drawable.add_image),
  //  STROKE(R.drawable.manual_minus),
    FLIP(R.drawable.flip_horizontal),
    // BRIGHTNESS(R.drawable.flip_horizontal),
    //  CONTRAST(R.drawable.flip_vertical),
    //  SATURATION(R.drawable.reset),
    // BEAUTIFY(R.drawable.eraser)

}


@Composable
fun FlipOption(
    modifier: Modifier = Modifier,
    onVerticalFlip: () -> Unit,
    onHorizontalFlip: () -> Unit
) {

    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center

    ) {
        val customModifier = Modifier
            .size(90.dp)
            .padding(8.dp)
            .background(MaterialTheme.colors.secondary, RoundedCornerShape(15.dp))

        IconButton(
            onClick = {
                onVerticalFlip()
            },
            modifier = customModifier
        ) {
            Icon(modifier=Modifier.size(35.dp),painter = painterResource(id = R.drawable.flip_vertical), contentDescription = "Background")
        }
        IconButton(onClick = {
            onHorizontalFlip()
        }, modifier = customModifier) {
            Icon(modifier=Modifier.size(35.dp),painter = painterResource(id = R.drawable.flip_horizontal), contentDescription = "Background")
        }
    }

}


/*@Composable
fun DoubleSlider(
    modifier: Modifier = Modifier,
    label1: String = "Slider1",
    label2: String = "nice",
    onSlider1Change: (change: Float) -> Unit,
    onSlider2Change: (change: Float) -> Unit
) {
    var sliderPosition1 by remember {
        mutableStateOf(0f)
    }
    var sliderPosition2 by remember {
        mutableStateOf(0f)
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colors.surface)
            .padding(8.dp), verticalArrangement = Arrangement.SpaceEvenly
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(0.3f),
                text = label1,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(8.dp))
            Slider(
                value = sliderPosition1,
                onValueChange = {
                    sliderPosition1 = it
                    onSlider1Change(it)
                },
                valueRange = 0f..100f,
                onValueChangeFinished = {
                    // launch some business logic update with the state you hold
                    // viewModel.updateSelectedSliderValue(sliderPosition)
                },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colors.secondary,
                    activeTrackColor = MaterialTheme.colors.secondary
                )
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(0.3f),
                text = label2,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(8.dp))
            Slider(
                value = sliderPosition2,
                onValueChange = {
                    sliderPosition2 = it
                    onSlider2Change(it)
                },
                valueRange = 0f..100f,
                onValueChangeFinished = {
                    // launch some business logic update with the state you hold
                    // viewModel.updateSelectedSliderValue(sliderPosition)
                },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colors.secondary,
                    activeTrackColor = MaterialTheme.colors.secondary
                )
            )
        }

    }

}*/

@Composable
fun SingleSlider(
    modifier: Modifier = Modifier,
    label: String = "Slider",
    onSlider1Change: (change: Float) -> Unit,
    position: Float = 0f
) {
    var sliderPosition by remember {
        mutableStateOf(position)
    }
    Column(
        modifier = modifier
            .background(MaterialTheme.colors.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Text(modifier = Modifier.fillMaxWidth(0.3f), text = label, color = Color.White, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.width(8.dp))
            Slider(
                value = sliderPosition,
                onValueChange = {
                    sliderPosition = it
                    onSlider1Change(it)
                },
                valueRange = 0f..100f,
                onValueChangeFinished = {
                    // launch some business logic update with the state you hold
                    // viewModel.updateSelectedSliderValue(sliderPosition)
                },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colors.secondary,
                    activeTrackColor = MaterialTheme.colors.secondary
                )
            )
        }

    }

}


@Composable
fun TextImageButton(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    iconPainter: Painter,
    label: String,
    description: String
) {
    Column(
        modifier = modifier
            .size(70.dp)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier
                .size(30.dp),
            painter = iconPainter,
            tint = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSecondary,
            contentDescription = description
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.button,
            color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSecondary,
            textAlign = TextAlign.Center
        )
    }
}