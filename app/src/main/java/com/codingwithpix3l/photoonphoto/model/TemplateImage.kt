package com.codingwithpix3l.photoonphoto.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TemplateImage(
    val id:Long=0,
    val name:String="",
    val bgUrl:String="",
    val fgUrl:String="",
    val thumb:String="",
): Parcelable
