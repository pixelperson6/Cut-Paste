package com.codingwithpix3l.photoonphoto.core.util.dialogs

import android.view.WindowManager.BadTokenException
import android.graphics.drawable.ColorDrawable
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import com.codingwithpix3l.photoonphoto.R

object LoadingDialog {


    fun showLoader(context: Context): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.setView(R.layout.progress_dialog)
        val dialog = builder.create()
        try {
            dialog.show()
        } catch (e: BadTokenException) {
        }
        dialog.setCancelable(false)
        dialog.window
            ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
        // dialog.setMessage(Message);

    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideLoader(dialog: AlertDialog?) {
        // To dismiss the dialog
        dialog?.dismiss()
    }
}