package com.codingwithpix3l.photoonphoto.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.codingwithpix3l.imagepicker.*
import com.codingwithpix3l.photoonphoto.core.util.Constants
import com.codingwithpix3l.photoonphoto.core.util.Constants.CHOSEN_IMAGE
import com.codingwithpix3l.photoonphoto.core.util.Constants.EDIT_MODE
import com.codingwithpix3l.photoonphoto.ui.background.changer.EditorActivity
import com.codingwithpix3l.photoonphoto.ui.background.eraser.EraserActivity
import com.codingwithpix3l.photoonphoto.ui.collage.CollageActivity
import com.codingwithpix3l.photoonphoto.ui.main.nav.AboutActivity
import com.codingwithpix3l.photoonphoto.ui.main.nav.SupportActivity
import com.codingwithpix3l.photoonphoto.ui.main.nav.TipsActivity
import com.codingwithpix3l.photoonphoto.ui.theme.PhotoOnPhotoTheme

class MainActivity : ComponentActivity() {

    private lateinit var homeViewModel: MainViewModel

    private val imageList =arrayListOf<MediaResource>()
    private var mode = EditorMode.CHANGER

    private val activityResultCallback = ActivityResultCallback<List<MediaResource>> {
        if (it.isNotEmpty()) {
            imageList.clear()
            imageList.addAll(it)

            val intent =  when (mode) {
               EditorMode.COLLAGE -> {
                   Intent(this@MainActivity, CollageActivity::class.java)
                       .putParcelableArrayListExtra(CHOSEN_IMAGE, imageList)
               }
                EditorMode.FREE_COLLAGE -> {
                    Intent(this@MainActivity, EditorActivity::class.java)
                        .putExtra(EDIT_MODE,true)
                        .putParcelableArrayListExtra(CHOSEN_IMAGE, imageList)
                }
                EditorMode.CHANGER -> {
                    Intent(this@MainActivity, EraserActivity::class.java)
                        .putExtra(CHOSEN_IMAGE,imageList.first())
                }
            }
            startActivity(intent)

        }
    }

    private val matisseContractLauncher =
        registerForActivityResult(MatisseContract(), activityResultCallback)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(this)[MainViewModel::class.java]


        setContent {


            PhotoOnPhotoTheme {
                // A surface container using the 'background' color from the theme

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.surface
                ) {

                    MainScreen(
                        modifier = Modifier,
                        onTipsClicked = {
                            startActivity(Intent(this@MainActivity, TipsActivity::class.java))
                        },
                        onSupportClicked = {
                            startActivity(Intent(this@MainActivity, SupportActivity::class.java))
                        },
                        onAboutClicked = {
                            startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                        },
                        onGalleryCLicked = {
                            mode = EditorMode.CHANGER
                            val matisse = Matisse(
                                theme = DarkMatisseTheme,
                                supportedMimeTypes =  Matisse.ofImage(hasGif = false),
                                maxSelectable =1,
                                spanCount = 3,
                                captureStrategy = MediaStoreCaptureStrategy()
                            )
                            matisseContractLauncher.launch(matisse)
                        },
                        onCollageCLicked = {
                            mode = EditorMode.COLLAGE
                            val matisse = Matisse(
                                theme = DarkMatisseTheme,
                                supportedMimeTypes =  Matisse.ofImage(hasGif = false),
                                maxSelectable =9,
                                spanCount = 3,
                                captureStrategy = MediaStoreCaptureStrategy()
                            )
                            matisseContractLauncher.launch(matisse)

                        },
                        onExtraClicked = {
                            mode = EditorMode.FREE_COLLAGE
                            val matisse = Matisse(
                                theme = DarkMatisseTheme,
                                supportedMimeTypes =  Matisse.ofImage(hasGif = false),
                                maxSelectable =5,
                                spanCount = 3,
                                captureStrategy = MediaStoreCaptureStrategy()
                            )
                            matisseContractLauncher.launch(matisse)
                        },
                        onSavedClicked = {
                            val matisse = Matisse(
                                theme = DarkMatisseTheme,
                                supportedMimeTypes =  Matisse.ofImage(hasGif = false),
                                spanCount = 1,
                                saveDFolderName = Constants.SAVED_DIRECTORY
                            )
                            MatisseSavedFolder.startActivityPreviewFolder(this,matisse)
                        }
                    )
                }
            }
        }
    }

}

enum class EditorMode{
    CHANGER,COLLAGE,FREE_COLLAGE
}