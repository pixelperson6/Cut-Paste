package com.codingwithpix3l.photoonphoto

import android.app.Application
import android.content.Context
import android.os.Build
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder

class CutPasteApp : Application() {

    override fun onCreate() {
        super.onCreate()
        init(context = this)
    }

    private fun init(context: Context) {
        val imageLoader = ImageLoader.Builder(context)
            .crossfade(false)
            .allowHardware(false)
            .components {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
        Coil.setImageLoader(imageLoader)
    }

}