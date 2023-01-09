package com.codingwithpix3l.photoonphoto.core.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.codingwithpix3l.photoonphoto.R


fun Activity.openTtsSettings() {
    val intent = Intent().apply {
        action = "com.android.settings.TTS_SETTINGS"
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    try{
        startActivity(intent)
    } catch(e: ActivityNotFoundException) {
        showToast(R.string.toast_no_tts_settings)
    }
}

fun Activity.shareImage(uri:String){
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "image/*"
    shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse(uri))
    shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    try{
        startActivity(Intent.createChooser(shareIntent,"Share image"))
    } catch(e: ActivityNotFoundException) {
        showToast(R.string.toast_no_share_found)
    }
}

fun Activity.openUrl(url: String) {
    val uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        showToast(R.string.toast_no_browser)
    }
}

fun Activity.openEmailComposer(receiver: String) {
    val uri = Uri.fromParts("mailto", receiver, null)
    val intent = Intent(Intent.ACTION_SENDTO, uri)
    try{
        startActivity(intent)
    } catch(e: ActivityNotFoundException) {
        showToast(R.string.toast_no_email_client)
    }
}

fun Activity.openShareWithChooser(shareText: String) {
    val shareWithText = getString(R.string.share_with)
    val sharingIntent = Intent(Intent.ACTION_SEND)
        .setType("text/plain")
        .putExtra(Intent.EXTRA_TEXT, shareText)
    startActivity(Intent.createChooser(sharingIntent, shareWithText))
}

fun Activity.openFileChooser(requestCode: Int) {
    val mimeTypes = arrayOf(
        "text/plain", "text/txt",
        "text/comma-separated-values", "text/csv",
        "text/tab-separated-values", "text/tsv",
        "application/zip"
    )
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        .addCategory(Intent.CATEGORY_OPENABLE)
        .setType("*/*")
        .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    try {
        startActivityForResult(intent, requestCode)
    } catch (e: ActivityNotFoundException) {
        showToast(R.string.toast_no_file_manager_to_load)
    }
}

fun Activity.openDocumentTree(requestCode: Int) {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
    try {
        startActivityForResult(intent, requestCode)
    } catch (e: ActivityNotFoundException) {
        showToast(R.string.toast_no_file_manager_to_create)
    }
}


fun Activity.showToast(
    stringId: Int,
    duration: Int = Toast.LENGTH_SHORT
) = Toast.makeText(this, stringId, duration).show()

fun Activity.showToast(
    text: String,
    duration: Int = Toast.LENGTH_SHORT
) = Toast.makeText(this, text, duration).show()
