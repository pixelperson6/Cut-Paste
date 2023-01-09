package com.codingwithpix3l.photoonphoto.core.util

interface OnSingleChangeListener {
    fun onDone(changedValue: Int)
}

interface OnMultipleChangeListener {
    fun onDone(top:Int,left:Int,right:Int,bottom:Int,changedValue: Int)
}
interface OnDoubleChangeListener {
    fun onDone(width:Int,height:Int)
}

interface OnDeleteListener{
    fun onDone(imgUri : String)
}