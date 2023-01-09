package com.codingwithpix3l.photoonphoto.ui.background.changer.experiment.editorsdk

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.codingwithpix3l.photoonphoto.R

class PhotoEditorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var mImgSource=FilterImageView(context)
    private var frameImageSource = FilterImageView(context)


    init {
        //Setup image attributes

        mImgSource.id = imgSrcId
        mImgSource.adjustViewBounds = true
        val imgSrcParam = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        imgSrcParam.addRule(CENTER_IN_PARENT, TRUE)

        val a = context.obtainStyledAttributes(
            attrs, R.styleable.PhotoEditorView, defStyleAttr, 0
        )
        val imgSrcDrawable = a.getDrawable(R.styleable.PhotoEditorView_photo_src)
        if (imgSrcDrawable != null) {
            mImgSource.setImageDrawable(imgSrcDrawable)
        }

        //Setup image attributes

        frameImageSource.id = frameSrcId
        frameImageSource.adjustViewBounds = true

        //Add image source
        addView(mImgSource, imgSrcParam)

        a.recycle()


    }


    val source: ImageView
        get() = mImgSource

    fun saveBitmap(onSaveBitmap: OnSaveBitmap) {

        onSaveBitmap.onBitmapReady(mImgSource.bitmap)
    }

    companion object {
        private const val TAG = "PhotoEditorView"

        private const val imgSrcId = 1
        private const val frameSrcId = 2
    }
}