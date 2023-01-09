package com.codingwithpix3l.photoonphoto.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(
    val id:Long,
    val name:String,
    val imgUri:String,
    val width:Int ,
    val height:Int,
    var bucketId: Long = 0,
    var bucketName: String = "",
    var isSelected: Boolean = false
):Parcelable
