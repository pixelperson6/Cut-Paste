package com.codingwithpix3l.photoonphoto.model

sealed class CallbackStatus {
    object IDLE : CallbackStatus()
    object FETCHING : CallbackStatus()
    object SUCCESS : CallbackStatus()
    object Error : CallbackStatus()
}