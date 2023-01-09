package com.codingwithpix3l.photoonphoto.ui.background.eraser

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.codingwithpix3l.imagepicker.MediaResource
import com.codingwithpix3l.photoonphoto.R
import com.codingwithpix3l.photoonphoto.core.util.BindingAdapter
import com.codingwithpix3l.photoonphoto.core.util.Constants
import com.codingwithpix3l.photoonphoto.core.util.Constants.EDITED_IMAGE
import com.codingwithpix3l.photoonphoto.core.util.Constants.ERASER_MODE
import com.codingwithpix3l.photoonphoto.core.util.Constants.EXTERNAL
import com.codingwithpix3l.photoonphoto.core.util.Constants.INTERNAL
import com.codingwithpix3l.photoonphoto.core.util.Constants.PRIVATE_IMAGE_NAME
import com.codingwithpix3l.photoonphoto.core.util.StorageHelper
import com.codingwithpix3l.photoonphoto.core.util.dialogs.LoadingDialog
import com.codingwithpix3l.photoonphoto.core.view.EraserView
import com.codingwithpix3l.photoonphoto.core.view.EraserView.Companion.ERASE
import com.codingwithpix3l.photoonphoto.core.view.EraserView.Companion.MAGIC
import com.codingwithpix3l.photoonphoto.core.view.EraserView.Companion.MOVING
import com.codingwithpix3l.photoonphoto.core.view.EraserView.Companion.RESTORE
import com.codingwithpix3l.photoonphoto.databinding.ActivityEraserBinding
import com.codingwithpix3l.photoonphoto.ui.background.changer.EditorActivity
import com.google.android.gms.ads.AdRequest
import kotlinx.coroutines.launch
import java.util.*

class EraserActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityEraserBinding
    private lateinit var mBitmap: Bitmap

    private var mHoverView: EraserView? = null
    private var mDensity = 0.0

    private var viewWidth = 0
    private var viewHeight = 0
    private var bmWidth = 0
    private var bmHeight = 0

    private var mode = INTERNAL

    private var actionBarHeight = 0
    private var bottomBarHeight = 0
    private var bmRatio = 0.0
    private var viewRatio = 0.0
    private lateinit var image: MediaResource
    private var brushSize = 0
    private var brushOffset = 100

    private var currentView: TextView? = null
    private var currentViewBg: LinearLayout? = null
    private lateinit var alertDialog: AlertDialog


    companion object {
        val TAG = EraserActivity::class.simpleName
    }

    private lateinit var builder: AlertDialog.Builder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEraserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)

        image = intent.getParcelableExtra(Constants.CHOSEN_IMAGE)!!

        mode = intent.getIntExtra(ERASER_MODE, INTERNAL)


        mDensity = resources.displayMetrics.density.toDouble()
        actionBarHeight = (110 * mDensity).toInt()
        bottomBarHeight = (60 * mDensity).toInt()

        viewWidth = resources.displayMetrics.widthPixels
        viewHeight = resources.displayMetrics.heightPixels - actionBarHeight - bottomBarHeight
        viewRatio = viewHeight.toDouble() / viewWidth.toDouble()

        getScaledBitmap(image.uri)
        initButton()

    }

    private fun toggleView(activeView: TextView?, activeViewBg: LinearLayout?) {
        currentView?.setTextColor(ContextCompat.getColor(this@EraserActivity, R.color.black))
        currentView = activeView
        currentView?.setTextColor(ContextCompat.getColor(this@EraserActivity, R.color.cyan_700))

        currentViewBg?.setBackgroundResource(R.drawable.round_corner_secondary_box)
        currentViewBg = activeViewBg
        currentViewBg?.setBackgroundResource(R.drawable.round_corner_secondary_box2)

    }

    private fun getScaledBitmap(imgUri: Uri) {
        alertDialog = LoadingDialog.showLoader(this)
        try {
            mBitmap = BindingAdapter.uriToBitmap(imgUri, contentResolver)
        } catch (e: Exception) {
            finish()
        }

        bmRatio = mBitmap.height.toDouble() / mBitmap.width.toDouble()
        if (bmRatio < viewRatio) {
            bmWidth = viewWidth
            bmHeight =
                (viewWidth.toDouble() * (mBitmap.height.toDouble() / mBitmap.width.toDouble())).toInt()
        } else {
            bmHeight = viewHeight
            bmWidth =
                (viewHeight.toDouble() * (mBitmap.width.toDouble() / mBitmap.height.toDouble())).toInt()
        }
        mBitmap = Bitmap.createScaledBitmap(mBitmap, bmWidth, bmHeight, false)

        loadPhotoToHoverView(mBitmap)
    }

    private fun loadPhotoToHoverView(mainBitmap: Bitmap) {

        mHoverView = EraserView(this, mainBitmap, bmWidth, bmHeight, viewWidth, viewHeight,
            {
                updateUndoRedoButton()
            }, {
                LoadingDialog.hideLoader(alertDialog)
            })
        mHoverView!!.layoutParams = ViewGroup.LayoutParams(viewWidth, viewHeight)

        binding.mainLayout.removeAllViews()

        binding.mainLayout.addView(mHoverView)
        mHoverView!!.switchMode(MOVING)
        updateUndoRedoButton()
    }

    private fun initButton() {

        binding.apply {
            mirror.setOnClickListener(this@EraserActivity)
            zoom.setOnClickListener(this@EraserActivity)
            manualEraser.setOnClickListener(this@EraserActivity)
            restore.setOnClickListener(this@EraserActivity)
            magicEraser.setOnClickListener(this@EraserActivity)
            done.setOnClickListener(this@EraserActivity)
            undoEditSticker.setOnClickListener(this@EraserActivity)
            redoEditSticker.setOnClickListener(this@EraserActivity)
            colorButton.setOnClickListener(this@EraserActivity)
            optionBtn.setOnClickListener(this@EraserActivity)
            //  replace.setOnClickListener(this@EraserActivity)
            backBtn.setOnClickListener { onBackPressed() }
            adView.loadAd(AdRequest.Builder().build())
        }
        toggleView(binding.zoomTxt, binding.zoom)
    }

    private var currentColor = 0

    private fun setBackGroundColor(color: Int) {

        when (color) {
            0 -> {
                binding.mainLayout.setBackgroundResource(R.drawable.bg)
                binding.colorButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@EraserActivity,
                        R.drawable.white_board
                    )
                )
            }
            1 -> {
                binding.mainLayout.setBackgroundColor(Color.WHITE)
                binding.colorButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@EraserActivity,
                        R.drawable.black_board
                    )
                )
            }
            2 -> {
                binding.mainLayout.setBackgroundColor(Color.BLACK)
                binding.colorButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@EraserActivity,
                        R.drawable.bg_board
                    )
                )
                binding.colorButton.scaleType = ImageView.ScaleType.CENTER_CROP
            }
            else -> {
            }
        }
        currentColor = color
    }


    private fun updateUndoRedoButton() {
        if (mHoverView!!.checkUndoEnable()) {
            binding.undoEditSticker.isEnabled = true
            binding.undoEditSticker.alpha = 1.0f
        } else {
            binding.undoEditSticker.isEnabled = false
            binding.undoEditSticker.alpha = 0.3f
        }
        if (mHoverView!!.checkRedoEnable()) {
            binding.redoEditSticker.isEnabled = true
            binding.redoEditSticker.alpha = 1.0f
        } else {
            binding.redoEditSticker.isEnabled = false
            binding.redoEditSticker.alpha = 0.3f
        }
    }

    override fun onClick(v: View) {
        updateUndoRedoButton()
        when (v.id) {
            R.id.option_btn -> {
                val customEraserFragment =
                    CustomEraserFragment(brushSize, brushOffset, mHoverView!!) { size, offset ->
                        brushSize = size
                        brushOffset = offset
                    }
                customEraserFragment.show(supportFragmentManager, TAG)
            }
            R.id.mirror -> {
                mHoverView!!.mirrorImage()
            }
            R.id.zoom -> {
                mHoverView!!.switchMode(MOVING)
                toggleView(binding.zoomTxt, binding.zoom)
            }
            R.id.manual_eraser -> {
                mHoverView!!.switchMode(ERASE)
                toggleView(binding.manualTxt, binding.manualEraser)
            }
            R.id.restore -> {
                mHoverView!!.switchMode(RESTORE)
                toggleView(binding.restoreTxt, binding.restore)
            }
            R.id.magic_eraser -> {
                mHoverView!!.switchMode(MAGIC)
                toggleView(binding.magicTxt, binding.magicEraser)
            }
            R.id.colorButton -> setBackGroundColor((currentColor + 1) % 3)
            R.id.undo_edit_sticker -> {
                mHoverView!!.undo()
                updateUndoRedoButton()
            }

            R.id.redo_edit_sticker -> {
                mHoverView!!.redo()
                updateUndoRedoButton()
            }
            /*     R.id.replace -> {
                     replace()

                 }*/
            R.id.done -> {
                saveInInternal()
            }
        }
    }

/*    private fun replace() {
        val matisse = Matisse(
            theme = DarkMatisseTheme,
            supportedMimeTypes =  Matisse.ofImage(hasGif = false),
            maxSelectable =1,
            spanCount = 3,
            captureStrategy = MediaStoreCaptureStrategy()
        )
        matisseContractLauncher.launch(matisse)
    }*/

    private fun saveInInternal() {
        val name = UUID.randomUUID().toString()
        lifecycleScope.launch {
            val bitmap = mHoverView?.saveDrawnBitmap()!!
            StorageHelper.savePhotoToInternalStorage(
                name,
                bitmap,
                this@EraserActivity
            )



            if (mode == INTERNAL) {
                val intent = Intent(this@EraserActivity, EditorActivity::class.java)
                intent.putExtra(PRIVATE_IMAGE_NAME, name)
                intent.putExtra(EDITED_IMAGE, image)
                startActivity(intent)
            } else {
                val intent = Intent()
                intent.putExtra(PRIVATE_IMAGE_NAME, name)
                intent.putExtra(EDITED_IMAGE, image)
                setResult(RESULT_OK, intent)
                finish()
                //startActivity(intent)

            }

        }
    }


    override fun onBackPressed() {

        if (mHoverView!!.checkUndoEnable()) {

            builder.setTitle(getString(R.string.exit_without_save))
                .setIcon(R.drawable.ic_announcement_24)
                .setMessage(getString(R.string.exit_body))
                .setCancelable(true)
                .setNeutralButton(getString(R.string.save)) { _, _ ->
                    saveInInternal()
                }
                .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                    val intent = Intent()
                    intent.putExtra(EDITED_IMAGE, image)
                    setResult(RESULT_CANCELED, intent)
                    finish()
                    super.onBackPressed()
                }
                .setNegativeButton(getString(R.string.cancel_disc)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .show()

        } else {
            finish()
            super.onBackPressed()
        }

    }

}
