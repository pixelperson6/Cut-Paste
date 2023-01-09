package com.codingwithpix3l.photoonphoto.core.util.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import com.codingwithpix3l.photoonphoto.R
import com.codingwithpix3l.photoonphoto.core.util.PermissionUtil


/**
 * Dialog to prompt the user to go to ask permission again in case user denied the requested permission. If the user
 * clicks 'OK' on the dialog, user will be requested to ask permission again.
 * if the user clicks 'Cancel' on the dialog, it simply dismisses the alert dialog
 * And user wont be able to use the functionality without the permission
 */
class RationaleDialog(
    private val context: Context,
    private val permissionLauncher: ActivityResultLauncher<Array<String>>,
    @StyleRes
    private var theme: Int,
   // private var title: String?,
    private var rationale: String?,
    private var positiveButtonText: String?,
    private var negativeButtonText: String?
) : DialogInterface.OnClickListener {

    private var dialog: AlertDialog? = null
   // var dialog : android.app.AlertDialog? = null

    /**
     * Display the dialog.
     */
    fun showCompatDialog() {
        dialog = AlertDialog.Builder(context,theme)
            .setMessage(rationale)
           // .setTitle(title)
            .setPositiveButton(positiveButtonText, this)
            .setNegativeButton(negativeButtonText, this)
            .show()
    }

    override fun onClick(dialogInterface: DialogInterface?, buttonType: Int) {
        when (buttonType) {
            Dialog.BUTTON_POSITIVE -> {
                PermissionUtil.updateOrRequestPermission(context, permissionLauncher)
            }
            Dialog.BUTTON_NEGATIVE -> dialog?.dismiss()
        }
    }

    @Suppress("UNUSED")
    class Builder(private var context: Context, private val permissionLauncher: ActivityResultLauncher<Array<String>>) {
        @StyleRes
        private var theme = 0
      //  private var title = context.getString(R.string.title_settings_dialog)
        private var rationale = context.getString(R.string.rationale_ask)
        private var positiveButtonText = context.getString(android.R.string.ok)
        private var negativeButtonText = context.getString(android.R.string.cancel)

        fun theme(@StyleRes theme: Int) = apply { this.theme = theme }
        /*fun title(title: String) = apply { this.title = title }
        fun title(@StringRes resId: Int) = apply { this.title = context.getString(resId) }*/
        fun rationale(rationale: String) = apply { this.rationale = rationale }
        fun rationale(@StringRes resId: Int) = apply { this.rationale = context.getString(resId) }
        fun positiveButtonText(positiveButtonText: String) =
            apply { this.positiveButtonText = positiveButtonText }

        fun positiveButtonText(@StringRes resId: Int) =
            apply { this.positiveButtonText = context.getString(resId) }

        fun negativeButtonText(negativeButtonText: String) =
            apply { this.negativeButtonText = negativeButtonText }

        fun negativeButtonText(@StringRes resId: Int) =
            apply { this.negativeButtonText = context.getString(resId) }

        fun build(): RationaleDialog {
            return RationaleDialog(
                context = context,
                permissionLauncher,
                theme = theme,
               // title = title,
                rationale = rationale,
                positiveButtonText = positiveButtonText,
                negativeButtonText = negativeButtonText
            )
        }
    }
}
