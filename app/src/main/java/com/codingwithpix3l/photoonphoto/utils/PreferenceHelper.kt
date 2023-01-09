package com.codingwithpix3l.photoonphoto.utils

import android.content.Context

object PreferenceHelper {

    private const val PREFS_FILE_NAME = "GalleryImagePicker"

    @JvmStatic
    fun firstTimeAskingPermission(context: Context, permission: String, isFirstTime: Boolean) {
        val preferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
        preferences.edit()
            .putBoolean(permission, isFirstTime)
            .apply()
    }

    @JvmStatic
    fun isFirstTimeAskingPermission(context: Context, permission: String): Boolean {
        return context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
            .getBoolean(permission, true)
    }
}