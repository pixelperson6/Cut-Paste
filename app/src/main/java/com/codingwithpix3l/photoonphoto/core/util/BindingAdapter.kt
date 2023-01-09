package com.codingwithpix3l.photoonphoto.core.util

import android.content.ContentResolver
import android.graphics.*
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide


class BindingAdapter {

    companion object {


        fun uriToBitmap(imgUri: Uri, c:ContentResolver): Bitmap {
            return MediaStore.Images.Media.getBitmap(c, imgUri)
        }

        private fun getRoundedCornerBitmap(bitmap: Bitmap): Bitmap {
            val paint = Paint()
            val roundBitmapPaint = Paint()
            val srcRect = Rect(0, 0, bitmap.width, bitmap.height)
            val rectF = RectF(srcRect)
            paint.isAntiAlias = true
            roundBitmapPaint.isAntiAlias = true
            roundBitmapPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            val roundPx = 50f
            val roundCanvas = Canvas()
            val output: Bitmap = Bitmap.createBitmap(
                bitmap.width,
                bitmap.height, Bitmap.Config.ARGB_8888
            )
            roundCanvas.setBitmap(output)
            roundCanvas.drawRoundRect(rectF, roundPx, roundPx, paint)
            roundCanvas.drawBitmap(bitmap, srcRect, srcRect, roundBitmapPaint)
            return output
        }

    }


}
