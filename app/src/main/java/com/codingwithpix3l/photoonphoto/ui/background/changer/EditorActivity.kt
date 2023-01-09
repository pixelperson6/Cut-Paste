package com.codingwithpix3l.photoonphoto.ui.background.changer

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.codingwithpix3l.imagepicker.*
import com.codingwithpix3l.photoonphoto.R
import com.codingwithpix3l.photoonphoto.core.components.ChangerBgOption
import com.codingwithpix3l.photoonphoto.core.components.ChangerImageOption
import com.codingwithpix3l.photoonphoto.core.util.BindingAdapter
import com.codingwithpix3l.photoonphoto.core.util.Constants
import com.codingwithpix3l.photoonphoto.core.util.Constants.EDITED_IMAGE
import com.codingwithpix3l.photoonphoto.core.util.Constants.EDIT_MODE
import com.codingwithpix3l.photoonphoto.core.util.Constants.ERASER_MODE
import com.codingwithpix3l.photoonphoto.core.util.Constants.EXTERNAL
import com.codingwithpix3l.photoonphoto.core.util.Constants.PNG
import com.codingwithpix3l.photoonphoto.core.util.Constants.PRIVATE_IMAGE_NAME
import com.codingwithpix3l.photoonphoto.core.util.Constants.SAVED_DIRECTORY
import com.codingwithpix3l.photoonphoto.core.util.StorageHelper
import com.codingwithpix3l.photoonphoto.databinding.ActivityEditorBinding
import com.codingwithpix3l.photoonphoto.ui.background.changer.experiment.editorsdk.OnPhotoEditorListener
import com.codingwithpix3l.photoonphoto.ui.background.changer.experiment.editorsdk.PhotoEditor
import com.codingwithpix3l.photoonphoto.ui.background.changer.experiment.editorsdk.PhotoEditorView
import com.codingwithpix3l.photoonphoto.ui.background.changer.experiment.editorsdk.ViewType
import com.codingwithpix3l.photoonphoto.ui.background.eraser.EraserActivity
import com.codingwithpix3l.photoonphoto.ui.main.MainActivity
import com.codingwithpix3l.photoonphoto.ui.theme.PhotoOnPhotoTheme
import com.raedapps.alwan.rememberAlwanState
import com.raedapps.alwan.ui.AlwanDialog
import kotlinx.coroutines.launch


class EditorActivity : AppCompatActivity(), OnPhotoEditorListener {


    private lateinit var binding: ActivityEditorBinding
    private lateinit var editorVm: EditorViewModel

    private var isBackground = true

    lateinit var mPhotoEditor: PhotoEditor
    private lateinit var mPhotoEditorView: PhotoEditorView

    private lateinit var builder: AlertDialog.Builder

    private lateinit var image: MediaResource

    private var name: String?= null

    private val activityResultCallback = ActivityResultCallback<List<MediaResource>> {
        if (it.isNotEmpty()) {

            if(isBackground){
                mPhotoEditor.clearFrameView()
                mPhotoEditor.setMainSource(
                    BindingAdapter.uriToBitmap(
                        it.first().uri,
                        this.contentResolver
                    )
                )

            }else{

                for(media in it){
                    val sticker = StickerImage(media)
                    editorVm.addStickerImage(sticker)
                    mPhotoEditor.addImage(sticker){imageView,mediaResource ->
                        editorVm.setParentImageView( imageView)
                        editorVm.selectedStickerImage = mediaResource
                        editorVm.changeImageOptionVisibility(true)
                    }
                }
            }
        }
    }

   private val requestEditedSticker =
     //  registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {


                name = it.data?.getStringExtra(PRIVATE_IMAGE_NAME)

                image = it.data?.getParcelableExtra(EDITED_IMAGE)!!


                if (name != null){
                    editorVm.erasedBitmapInMultiple(this,name!!,mPhotoEditor,StickerImage(image))
                }

            }
        }

    private val matisseContractLauncher =
        registerForActivityResult(MatisseContract(), activityResultCallback)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        builder = AlertDialog.Builder(this,android.R.style.Theme_Material_Dialog_Alert)
        editorVm = ViewModelProvider(this)[EditorViewModel::class.java]


        editorVm.isFreeCollage = intent.getBooleanExtra(EDIT_MODE,false)


        mPhotoEditorView = binding.workSpace


        mPhotoEditor = PhotoEditor.Builder(this, mPhotoEditorView) {
            editorVm.changeImageOptionVisibility(false)
        }
            .setPinchTextScalable(true) // set flag to make text scalable when pinch
            //.setDefaultTextTypeface(mTextRobotoTf)
            //.setDefaultEmojiTypeface(mEmojiTypeFace)
            .build() // build photo editor sdk


        mPhotoEditor.setOnPhotoEditorListener(this)

        if(editorVm.isFreeCollage){

            editorVm.setBgBitmap(editorVm.getBitmapFromColor(Color.Gray))
            mPhotoEditor.setMainSource(editorVm.backgroundBitmap.value)

            val images = intent.getParcelableArrayListExtra<MediaResource>(Constants.CHOSEN_IMAGE)

            if (images != null) {
                for (image in images){
                    val stickerImage = StickerImage(image)
                    editorVm.addStickerImage(stickerImage)

                    mPhotoEditor.addImage(stickerImage){imageView,selectedSticker ->
                        editorVm.setParentImageView( imageView)
                        editorVm.selectedStickerImage = selectedSticker
                        editorVm.changeImageOptionVisibility(true)
                    }
                }
            }
        }else{
            name = intent.getStringExtra(PRIVATE_IMAGE_NAME)

            image = intent.getParcelableExtra(EDITED_IMAGE)!!

            if (name != null) {
                editorVm.setErasedBitmap(this, name!!,mPhotoEditor, StickerImage(image))
            }



        }



        binding.changeBgOption.apply {
            setContent {

                PhotoOnPhotoTheme {


                    if (editorVm.isImageOptionVisible.value) {
                        ChangerImageOption(
                            modifier = Modifier
                                .zIndex(1f)
                                .background(MaterialTheme.colors.surface),
                          //  photoEditor = mPhotoEditor,
                            onDonePressed = {
                                editorVm.changeImageOptionVisibility(false)
                            },
                            onErasePressed = {
                                editorVm.changeImageOptionVisibility(false)
                                val intent = Intent(this@EditorActivity, EraserActivity::class.java)
                                intent.putExtra(Constants.CHOSEN_IMAGE, editorVm.selectedStickerImage!!.mediaResource)
                                intent.putExtra(ERASER_MODE, EXTERNAL)
                                requestEditedSticker.launch(intent)

                            },
                            onDeletePressed = {
                                editorVm.removeStickerImage()

                             //   mPhotoEditor.clearAllViews()

                               /* for(mediaImage in editorVm.selectedStickerImages){
                                    mPhotoEditor.addImage(mediaImage){imageView,mediaResource ->
                                        editorVm.setParentImageView( imageView)
                                        editorVm.imageView?.bringToFront()
                                        mPhotoEditor.frameView?.bringToFront()
                                        editorVm.selectedStickerImage = mediaResource
                                        editorVm.changeImageOptionVisibility(true)
                                    }
                                }*/
                                editorVm.changeImageOptionVisibility(false)

                                mPhotoEditor.parentView?.removeView(editorVm.imageParent)
                            }

                        )

                    } else {
                        ChangerBgOption(
                            modifier = Modifier
                                .zIndex(1f)
                                .background(MaterialTheme.colors.surface),
                           // photoEditor = mPhotoEditor,
                            onCustomClicked = {

                             //   mPhotoEditor.clearAllViews()
                                val transparent = BitmapFactory.decodeResource(context.resources, R.drawable.transparent)
                                mPhotoEditor.setMainSource(transparent)

                            },
                            onGalleryClicked = {


                                isBackground = true
                                val matisse = Matisse(
                                    theme = DarkMatisseTheme,
                                    supportedMimeTypes = Matisse.ofImage(hasGif = false),
                                    maxSelectable = 1,
                                    spanCount = 3,
                                    captureStrategy = MediaStoreCaptureStrategy()
                                )
                                matisseContractLauncher.launch(matisse)
                            },
                            onTemplateClicked = {

                                Toast.makeText(
                                    this@EditorActivity,
                                    "Unsplash will be available soon",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onAddImageClicked = {
                                isBackground = false
                                val matisse = Matisse(
                                    theme = DarkMatisseTheme,
                                    supportedMimeTypes = Matisse.ofImage(hasGif = false),
                                    maxSelectable = 5,
                                    spanCount = 3,
                                    captureStrategy = MediaStoreCaptureStrategy()
                                )
                                matisseContractLauncher.launch(matisse)
                            },
                            onColorSelected = {

                                editorVm.setBgBitmap(editorVm.getBitmapFromColor(it))

                              //  mPhotoEditor.clearAllViews()
                                mPhotoEditor.clearFrameView()
                                mPhotoEditor.setMainSource(editorVm.backgroundBitmap.value)

                            /*   for(stickerImage in editorVm.selectedStickerImages){
                                    mPhotoEditor.addImage(stickerImage){imageView,mediaResource ->
                                        editorVm.imageView = imageView
                                        editorVm.selectedStickerImage = mediaResource
                                        editorVm.changeImageOptionVisibility(true)
                                    }
                                }*/

                            },
                            onImageSelected = { bg, fg ->

                                if (bg.isEmpty()) {
                                    Toast.makeText(
                                        this@EditorActivity,
                                        "Couldn't load image, please check internet connection and try again",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    Glide.with(this)
                                        .asBitmap()
                                        .load(bg)
                                        .into(object : CustomTarget<Bitmap>() {
                                            override fun onResourceReady(
                                                resource: Bitmap,
                                                transition: Transition<in Bitmap>?
                                            ) {
                                                val btm = resource
                                                if (fg.isEmpty()) {
                                                    editorVm.setBgBitmap(btm)
                                                  //  mPhotoEditor.clearAllViews()
                                                    mPhotoEditor.setMainSource(editorVm.backgroundBitmap.value)
                                              /*      mPhotoEditor.addImage(editorVm.frontBitmap.value){imageView->
                                                        editorVm.imageView = imageView
                                                        editorVm.changeImageOptionVisibility(true)
                                                    }*/
                                               /*     for(mediaImage in editorVm.selectedMediaResources){
                                                        mPhotoEditor.addImage(mediaImage){imageView,mediaResource ->
                                                            editorVm.imageView = imageView
                                                            editorVm.selectedMediaResource = mediaResource
                                                            editorVm.changeImageOptionVisibility(true)
                                                        }
                                                    }*/

                                                } else {
                                                    Glide.with(this@EditorActivity)
                                                        .asBitmap()
                                                        .load(fg)
                                                        .into(object : CustomTarget<Bitmap>() {
                                                            override fun onResourceReady(
                                                                resource: Bitmap,
                                                                transition: Transition<in Bitmap>?
                                                            ) {
                                                                editorVm.setBgBitmap(btm)
                                                                editorVm.setFgBitmap(resource)
                                                         //       mPhotoEditor.clearAllViews()
                                                                mPhotoEditor.setMainSource(editorVm.backgroundBitmap.value)
                                                            /*    mPhotoEditor.addImage(editorVm.frontBitmap.value){imageView->
                                                                    editorVm.imageView = imageView
                                                                    editorVm.changeImageOptionVisibility(true)
                                                                }*/
                                                           /*     for(mediaImage in editorVm.selectedStickerImages){
                                                                    mPhotoEditor.addImage(mediaImage){imageView,mediaResource ->
                                                                        editorVm.imageView = imageView
                                                                        editorVm.imageView?.bringToFront()
                                                                        mPhotoEditor.frameView?.bringToFront()
                                                                        editorVm.selectedStickerImage = mediaResource
                                                                        editorVm.changeImageOptionVisibility(true)
                                                                    }
                                                                }*/
                                                                mPhotoEditor.addFrame(editorVm.fgBitmap.value)
                                                            }

                                                            override fun onLoadCleared(placeholder: Drawable?) {
                                                                // this is called when imageView is cleared on lifecycle call or for
                                                                // some other reason.
                                                                // if you are referencing the bitmap somewhere else too other than this imageView
                                                                // clear it here as you can no longer have the bitmap
                                                            }
                                                        })
                                                }

                                            }

                                            override fun onLoadCleared(placeholder: Drawable?) {
                                                // this is called when imageView is cleared on lifecycle call or for
                                                // some other reason.
                                                // if you are referencing the bitmap somewhere else too other than this imageView
                                                // clear it here as you can no longer have the bitmap
                                            }
                                        })
                                }
                            }

                        )
                    }

                }

            }
        }

        binding.colorDialog.apply {
            setContent {

                PhotoOnPhotoTheme {

                    val state = rememberAlwanState(initialColor = Color.White)
                    if (editorVm.isColorDialogVisible.value) {
                        visibility = View.VISIBLE
                        AlwanDialog(
                            onColorChanged = {},
                            state = state,
                            onDismissRequest = {
                                editorVm.isColorDialogVisible.value = false
                            },
                            showAlphaSlider = true,
                            positiveButtonText = "OK",
                            onPositiveButtonClick = {
                                editorVm.isColorDialogVisible.value = false
                                editorVm.setBgBitmap(editorVm.getBitmapFromColor(state.color))

                              //  mPhotoEditor.clearAllViews()
                                mPhotoEditor.setMainSource(editorVm.backgroundBitmap.value)
                          /*      mPhotoEditor.addImage(editorVm.frontBitmap.value){imageView->
                                    editorVm.imageView = imageView
                                    editorVm.changeImageOptionVisibility(true)
                                }*/
                          /*      for(mediaImage in editorVm.selectedMediaResources){
                                    mPhotoEditor.addImage(mediaImage){imageView,mediaResource ->
                                        editorVm.imageView = imageView
                                        editorVm.selectedMediaResource = mediaResource
                                        editorVm.changeImageOptionVisibility(true)
                                    }
                                }*/

                            },
                            negativeButtonText = "Cancel",
                            onNegativeButtonClick = {
                                editorVm.isColorDialogVisible.value = false
                            },
                        )
                    }

                }
            }
        }

        binding.done.setOnClickListener {
            saveToGallery()
        }
    }


    private fun saveToGallery() {

        lifecycleScope.launch {

            mPhotoEditor.saveBitmap()?.let {
                StorageHelper.savePhotoToExternalStorage(
                    PNG, SAVED_DIRECTORY,
                    "${System.currentTimeMillis()}.png",
                    it,
                    contentResolver, this@EditorActivity
                )
            }
        }
        finishAffinity()
        startActivity(Intent(this@EditorActivity, MainActivity::class.java))

    }

    override fun onEditTextChangeListener(rootView: View, text: String, colorCode: Int) {

    }

    override fun onAddViewListener(viewType: ViewType, numberOfAddedViews: Int) {

    }

    override fun onRemoveViewListener(viewType: ViewType, numberOfAddedViews: Int) {

    }

    override fun onStartViewChangeListener(viewType: ViewType) {

    }

    override fun onStopViewChangeListener(viewType: ViewType) {

    }
    override fun onBackPressed() {
        builder.setTitle(getString(R.string.exit_without_save))
            .setIcon(R.drawable.ic_announcement_24)
            .setMessage(getString(R.string.exit_body))
            .setCancelable(true)
            .setNeutralButton("Save"){_,_ ->
                saveToGallery()
            }
            .setPositiveButton("Confirm"){_,_ ->
                super.onBackPressed()
            }
            .setNegativeButton("Cancel"){dialogInterface,_ ->
                dialogInterface.dismiss()
            }
            .show()
    }


}
