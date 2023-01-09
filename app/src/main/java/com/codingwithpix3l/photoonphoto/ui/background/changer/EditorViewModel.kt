package com.codingwithpix3l.photoonphoto.ui.background.changer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithpix3l.imagepicker.MediaResource
import com.codingwithpix3l.photoonphoto.R
import com.codingwithpix3l.photoonphoto.core.util.StorageHelper
import com.codingwithpix3l.photoonphoto.model.TemplateImage
import com.codingwithpix3l.photoonphoto.ui.background.changer.experiment.editorsdk.PhotoEditor
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference


class EditorViewModel : ViewModel() {

    // Initial value is false so the dialog is hidden
 /*   private val _mainBitmap = mutableStateOf(Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888))
    val mainBitmap: State<Bitmap> = _mainBitmap
    fun setMainBitmap(bitmap: Bitmap) {
        _mainBitmap.value = bitmap
    }*/


    private val db = Firebase.firestore

    @SuppressLint("StaticFieldLeak")
    var imageView :ImageView? = null

    @SuppressLint("StaticFieldLeak")
    var imageParent : View? = null

    fun setParentImageView(view: View){
        imageParent = view
        imageView = imageParent!!.findViewById(R.id.imgPhotoEditorImage)
    }

    //var selectedMediaResource:MediaResource? = null
    var selectedStickerImage : StickerImage? = null

    private val _isColorDialogVisible = mutableStateOf(false)
    val isColorDialogVisible = _isColorDialogVisible

    fun changeDialogVisibility(visibility: Boolean) {
        _isColorDialogVisible.value = visibility
    }

    private val _isImageOptionVisible = mutableStateOf(false)
    val isImageOptionVisible = _isImageOptionVisible

    fun changeImageOptionVisibility(isVisible: Boolean) {
        _isImageOptionVisible.value = isVisible
    }
/*
    private val _selectedMediaResources = mutableListOf<MediaResource>()

    val selectedMediaResources: List<MediaResource> = _selectedMediaResources

    fun removeMediaResource(){
        _selectedMediaResources.remove(selectedMediaResource)
    }

    fun addMediaResource(mediaResource: MediaResource){
        _selectedMediaResources.add(mediaResource)
    }*/

    private val _selectedStickerImages = mutableListOf<StickerImage>()

    val selectedStickerImages: List<StickerImage> = _selectedStickerImages

    fun removeStickerImage(){
        _selectedStickerImages.remove(selectedStickerImage)
    }
    fun removeStickerImage(stickerImage: StickerImage){
        _selectedStickerImages.remove(stickerImage)
    }

    fun addStickerImage(stickerImage: StickerImage){
        _selectedStickerImages.add(stickerImage)
    }



    private val _frontBitmap = mutableStateOf(Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888))
    val frontBitmap: State<Bitmap> = _frontBitmap

    fun setFrontBitmap(bitmap:Bitmap) {
      //  viewModelScope.launch {
      //      val privateImage = StorageHelper.getPhotoFromInternalStorage(context, name)
            _frontBitmap.value = bitmap
       //     StorageHelper.deletePrivatePhoto(name, context)
      //      mPhotoEditor.setMainSource(frontBitmap.value)
      //  }
    }

    fun setErasedBitmap(context: Context, name: String,mPhotoEditor:PhotoEditor, stickerImage: StickerImage){
        viewModelScope.launch {
            val privateImage = StorageHelper.getPhotoFromInternalStorage(context, name)
            _frontBitmap.value = privateImage.bitmap
            StorageHelper.deletePrivatePhoto(name, context)
            addStickerImage(stickerImage.copy(bitmap = privateImage.bitmap))

            for (mediaImage in selectedStickerImages){
                mPhotoEditor.addImage(mediaImage){ig,mediaResource ->
                    setParentImageView( ig)
                    selectedStickerImage = mediaResource
                    changeImageOptionVisibility(true)
                }
            }
        }
    }

    fun erasedBitmapInMultiple(context: Context,name: String,mPhotoEditor: PhotoEditor,stickerImage: StickerImage){
        viewModelScope.launch {
            removeStickerImage(stickerImage)
            val privateImage = StorageHelper.getPhotoFromInternalStorage(context, name)
            _frontBitmap.value = privateImage.bitmap
            StorageHelper.deletePrivatePhoto(name, context)
            addStickerImage(stickerImage.copy(bitmap = privateImage.bitmap))
            mPhotoEditor.parentView?.removeView(imageParent)

          //  for (mediaImage in selectedStickerImages){
                mPhotoEditor.addImage(stickerImage.copy(bitmap = privateImage.bitmap)){ig,mediaResource ->
                    setParentImageView( ig)
                    selectedStickerImage = mediaResource
                    changeImageOptionVisibility(true)
                }
          //  }
        }

    }

    private val _backgroundBitmap =
        mutableStateOf(Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888))
    val backgroundBitmap: State<Bitmap> = _backgroundBitmap

    fun setBgBitmap(bitmap: Bitmap) {
        _backgroundBitmap.value = bitmap
    }

    private val _fgBitmap = mutableStateOf(Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888))
    val fgBitmap: State<Bitmap> = _fgBitmap

    fun setFgBitmap(bitmap: Bitmap) {
        _fgBitmap.value = bitmap
    }

    private val _imageSize = mutableStateOf(ImageSize(1080, 1080))
    val imageSize: State<ImageSize> = _imageSize

  /*  fun setImageSize(size: ImageSize) {
        _imageSize.value = size
    }*/

    fun getBitmapFromColor(color: Color): Bitmap {
        val btm = Bitmap.createBitmap(
            imageSize.value.width,
            imageSize.value.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(btm)
        canvas.drawColor(color.toArgb())
        return btm
    }
    var isFreeCollage = false

    private val emptyTemplateList =listOf(
        TemplateImage(),
        TemplateImage(),
        TemplateImage(),
        TemplateImage(),
        TemplateImage(),
        TemplateImage(),
        TemplateImage(),
        TemplateImage(),
    )

    private val _result = mutableStateOf(
        emptyTemplateList
    )
    val result : State<List<TemplateImage>> = _result

    private val _optionList = mutableStateOf(
        listOf(
        "CUSTOM",
        "COLOR",
        "GRADIENT",
        "BIRTHDAY",
        "ANNIVERSARY",
        "FRAME",
        "VALENTINES",
        "HEART",
        "QUOTES",
        "NEON",
        "DRIP",
        "FLAME",
        "SKETCH",
        "IPL",
        "FESTIVAL",
        "FLAG",
        "WATERFALL",
        "FLOWER",
        "NATURE",
        "CLOUD",
        "PLACE",
        "CITY",
        "VEHICLE",
        "ANIMAL",
        "WALL",
        "PAPER",
        "PATTERN",
        "PAINTING")
    )
    val optionList: State<List<String>> = _optionList

    fun fetchOptionList(applicationCtx: Context){
        val contextRef = WeakReference(applicationCtx)
        if (contextRef.get() == null) return
        val fbOptionList= arrayListOf<String>()
        db.collection("OptionList")
            .orderBy("Id")
            .get()
            .addOnSuccessListener { data ->
                for (document in data) {
                    fbOptionList.add(
                        document.get("Name").toString()
                    )
                }
                if(fbOptionList.isNotEmpty()){
                    _optionList.value = fbOptionList
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(contextRef.get(), "${exception.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    fun fetchImages(applicationCtx: Context, collectionName: String) {
        viewModelScope.launch {
            try {
                fetchImagesFromFirebase(applicationCtx, collectionName)
            } catch (e: Exception) {
                _result.value =emptyTemplateList
                Toast.makeText(applicationCtx, "${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun fetchImagesFromFirebase(applicationCtx: Context, collectionName: String) {
        val contextRef = WeakReference(applicationCtx)
        if (contextRef.get() == null) return
        val templateImages = arrayListOf<TemplateImage>()
        db.collection(collectionName)
            .orderBy("Id")
            .get()
            .addOnSuccessListener { data ->
                for (document in data) {
                    templateImages.add(
                        TemplateImage(
                            id = document.get("Id") as Long,
                            name = document.get("Name").toString(),
                            bgUrl = document.get("BgUrl").toString(),
                            fgUrl = document.get("FgUrl").toString(),
                            thumb = document.get("Thumb").toString()
                        )
                    )
                }
                _result.value = templateImages
            }
            .addOnFailureListener { exception ->
                _result.value =emptyTemplateList
                Toast.makeText(contextRef.get(), "${exception.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

/*    fun addStroke(
        bitmap: Bitmap = frontBitmap.value,
        color: Int = Color.Black.toArgb(),
        stroke: Float= 20f,
    ) {

        val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        //Overlay target bitmap with black color.
        //Overlay target bitmap with black color.
        val filter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        paint.colorFilter = filter
        //Draw target bitmap over new bitmap.
        //Draw target bitmap over new bitmap.
        canvas.drawBitmap(bitmap, -stroke, 0f, paint) //left

        canvas.drawBitmap(bitmap, 0f, -stroke, paint) //top

        canvas.drawBitmap(bitmap, stroke, 0f, paint) //right

        canvas.drawBitmap(bitmap, 0f, stroke, paint) //bottom

        //Remove overlay.
        //Remove overlay.
        paint.colorFilter = null
        //Draw target bitmap.
        //Draw target bitmap.
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        _frontBitmap.value = newBitmap
        imageView?.setImageBitmap(frontBitmap.value)
    }

    fun addShadow(
        bm: Bitmap = frontBitmap.value,
        dstHeight: Int = frontBitmap.value.height,
        dstWidth: Int = frontBitmap.value.width,
        color: Int = Color.Black.toArgb(),
        size: Int = 20,
        dx: Float = 0f,
        dy: Float =0f
    ) {
        val mask = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ALPHA_8)
        val scaleToFit = Matrix()
        val src = RectF(0f, 0f, bm.width.toFloat(), bm.height.toFloat())
        val dst = RectF(0f, 0f, dstWidth - dx, dstHeight - dy)
        scaleToFit.setRectToRect(src, dst, ScaleToFit.CENTER)
        val dropShadow = Matrix(scaleToFit)
        dropShadow.postTranslate(dx, dy)
        val maskCanvas = Canvas(mask)
        val paint = Paint()
        paint.isAntiAlias = true
        maskCanvas.drawBitmap(bm, scaleToFit, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
        maskCanvas.drawBitmap(bm, dropShadow, paint)
        val filter = BlurMaskFilter(size.toFloat(), Blur.NORMAL)
        paint.reset()
        paint.isAntiAlias = true
        paint.color = color
        paint.maskFilter = filter
        paint.isFilterBitmap = true
        val ret = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888)
        val retCanvas = Canvas(ret)
        retCanvas.drawBitmap(mask, 0f, 0f, paint)
        retCanvas.drawBitmap(bm, scaleToFit, null)
        mask.recycle()
        _frontBitmap.value = ret
        imageView?.setImageBitmap(frontBitmap.value)
    }*/


}

data class StickerImage(val mediaResource: MediaResource, val bitmap: Bitmap? = null)

data class ImageSize(val width: Int, val height: Int)