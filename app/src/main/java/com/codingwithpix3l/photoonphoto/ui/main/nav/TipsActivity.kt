package com.codingwithpix3l.photoonphoto.ui.main.nav

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReplyAll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.codingwithpix3l.photoonphoto.R
import com.codingwithpix3l.photoonphoto.ui.theme.PhotoOnPhotoTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

class TipsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhotoOnPhotoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    TipsScreen(onBackPressed ={
                        //onBackPressed()
                        onBackPressedDispatcher.addCallback(this /* lifecycle owner */, object : OnBackPressedCallback(true) {
                            override fun handleOnBackPressed() {
                                // Back is pressed... Finishing the activity
                                finish()
                            }
                        })
                    })

                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TipsScreen(onBackPressed :() -> Unit) {

    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally


    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight(0.1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomEnd = 20.dp, bottomStart = 20.dp))
                .background(MaterialTheme.colors.primary),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                onClick = {
                    onBackPressed()
                }
            ) {
                Icon(
                    modifier = Modifier.size(35.dp),
                    imageVector = Icons.Default.ReplyAll,
                    contentDescription = "back button"
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            Text(text = "Tips", style = MaterialTheme.typography.h5)
        }
        HorizontalPager(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.95f),
            count = 3,
            state = pagerState,
            ) { page ->
            when(page){
                0 ->{

                   EraserTips()

                }
                1 ->{
                    PhotoOnPhotoTips()

                }
                2 ->{
                    CollageTIps()

                }

            }

        }

        HorizontalPagerIndicator(pagerState = pagerState)

    }
}

@Composable
fun EraserTips() {
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)){

        item {
            Text(modifier = Modifier.fillMaxWidth(),text = "Background Eraser", style = MaterialTheme.typography.h6, textAlign = TextAlign.Center)
            Text(text = "1. Image location with zoom button:", style = MaterialTheme.typography.h6)
            Text(text = "pinch in/out with two fingers to zoom image", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "2. Image location with zoom button:", style = MaterialTheme.typography.h6)
            Text(text = "pinch in/out with two fingers to zoom image", style = MaterialTheme.typography.body1)
            Text(text = "drag the image to adjust image position", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "3. Brush size:", style = MaterialTheme.typography.h6)
            Text(text = "Change brush size while erasing or restoring", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(8.dp))
            Image(modifier = Modifier.fillMaxWidth(),painter = painterResource(id = R.drawable.size_option) , contentDescription = "size button tips image", alignment = Alignment.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "4. Magic eraser:", style = MaterialTheme.typography.h6)
            Text(text = "Magic option erases the color you tap on, restore the unnecessary erased parts with restore button.", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "5. Background:", style = MaterialTheme.typography.h6)
            Text(text = "Consider changing the background for clarification and better view.", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(8.dp))
            Image(modifier = Modifier.fillMaxWidth(),painter = painterResource(id = R.drawable.bg_option) , contentDescription = "background button tips image", alignment = Alignment.Center)
            Spacer(modifier = Modifier.height(8.dp))
         //   Text(text = "6. Replace image:", style = MaterialTheme.typography.h6)
         //   Text(text = "Easily replace image in case you want to erase background of different image instead.", style = MaterialTheme.typography.body1)
          //  Spacer(modifier = Modifier.height(8.dp))
          //  Image(modifier = Modifier.fillMaxWidth(),painter = painterResource(id = R.drawable.replace_option) , contentDescription = "replace button tips image", alignment = Alignment.Center)
          //  Spacer(modifier = Modifier.height(8.dp))
            Text(text = "6. Undo and redo:", style = MaterialTheme.typography.h6)
            Text(text = "Undo and Redo to goo back and forth in progress.", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "7.Saved directory:", style = MaterialTheme.typography.h6)
            Text(text = "The image will be saved in directory named CutPaste Photos.", style = MaterialTheme.typography.body1)
        }

    }
}
@Composable
fun PhotoOnPhotoTips() {

    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)){

        item {
            Text(modifier = Modifier.fillMaxWidth(),text = "Background changer/Photo on photo", style = MaterialTheme.typography.h6, textAlign = TextAlign.Center)
            Text(text = "1. Zoom:", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Pinch in/out with two fingers to zoom image.", style = MaterialTheme.typography.body1)
           // Spacer(modifier = Modifier.height(8.dp))
         //   Text(text = "Double tap to zoom in , double tap again to zoom out.", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "2. Foreground image click:", style = MaterialTheme.typography.h6)
            Text(text = "Tap on foreground image to flip or change opacity", style = MaterialTheme.typography.body1)
           /// Spacer(modifier = Modifier.height(8.dp))
        //    Text(text = "The edit icon will take image to the background eraser, customise them accordingly to put over background.", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "3. Overflow:", style = MaterialTheme.typography.h6)
            Text(text = "The overflowed part over background will be cropped out.", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "4. Background:", style = MaterialTheme.typography.h6)
            Text(text = "There's multiple categories of background to choose. Once loaded they can be used offline too", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "5. Add photos:", style = MaterialTheme.typography.h6)
            Text(text = "If you chose photo on photo option , then you can add as many image on the background with add photos button in custom.", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "6. Connection:", style = MaterialTheme.typography.h6)
            Text(text = "Internet connection is required to get latest templates or it will show last loaded templates ", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Saved directory:", style = MaterialTheme.typography.h6)
            Text(text = "The image will be saved in directory named CutPaste Photos.", style = MaterialTheme.typography.body1)
        }

    }

}

@Composable
fun CollageTIps() {
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)){

        item {
            Text(modifier = Modifier.fillMaxWidth(),text = "Collage", style = MaterialTheme.typography.h6, textAlign = TextAlign.Center)
            Text(text = "1. Each image size:", style = MaterialTheme.typography.h6)
            Text(text = "Change the image size individually.", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(8.dp))
            Image(modifier = Modifier.fillMaxWidth(),painter = painterResource(id = R.drawable.resize_collage) , contentDescription = "size button tips image", alignment = Alignment.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "2. Change image position:", style = MaterialTheme.typography.h6)
            Text(text = "Hold drag and drop to change position", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "3. Replace/flip/rotate:", style = MaterialTheme.typography.h6)
            Text(text = "Tap image to replace,flip or rotate image.", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "4. Corner:", style = MaterialTheme.typography.h6)
            Text(text = "Make round corner to give more style.", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "5. Border:", style = MaterialTheme.typography.h6)
            Text(text = "Turn on/off black border.", style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "6. Saved directory:", style = MaterialTheme.typography.h6)
            Text(text = "The image will be saved in directory named CutPaste Photos.", style = MaterialTheme.typography.body1)
        }

    }

}
