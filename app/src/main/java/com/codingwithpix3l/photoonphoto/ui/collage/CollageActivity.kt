package com.codingwithpix3l.photoonphoto.ui.collage

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingwithpix3l.imagepicker.*
import com.codingwithpix3l.photoonphoto.R
import com.codingwithpix3l.photoonphoto.core.util.BindingAdapter
import com.codingwithpix3l.photoonphoto.core.util.Constants
import com.codingwithpix3l.photoonphoto.core.util.Constants.JPEG
import com.codingwithpix3l.photoonphoto.core.util.Constants.SAVED_DIRECTORY
import com.codingwithpix3l.photoonphoto.core.util.PuzzleUtils
import com.codingwithpix3l.photoonphoto.core.util.StorageHelper
import com.codingwithpix3l.photoonphoto.core.util.dialogs.LoadingDialog
import com.codingwithpix3l.photoonphoto.core.view.DegreeSeekBar
import com.codingwithpix3l.photoonphoto.core.view.puzzle.PuzzleLayout
import com.codingwithpix3l.photoonphoto.core.view.puzzle.PuzzleView
import com.codingwithpix3l.photoonphoto.databinding.ActivityCollageBinding
import kotlinx.coroutines.launch


class CollageActivity : AppCompatActivity(), View.OnClickListener,
    PuzzleAdapter.OnItemClickListener {

    companion object {
        const val FLAG_CONTROL_LINE_SIZE = 1
        const val FLAG_CONTROL_CORNER = 1 shl 1
        var count = 0

    }
   // private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var alertDialog: AlertDialog
    private lateinit var builder: AlertDialog.Builder

    private var controlFlag: Int = 0

    private lateinit var binding: ActivityCollageBinding

    lateinit var puzzleView: PuzzleView
    lateinit var puzzleLayouts: List<PuzzleLayout>

    var pieces: MutableList<Bitmap> = ArrayList()

    lateinit var degreeSeekBar: DegreeSeekBar


    private val activityResultCallback = ActivityResultCallback<List<MediaResource>> {
        if (it.isNotEmpty()) {
            val bitmap = BindingAdapter.uriToBitmap(it[0].uri, contentResolver)
            puzzleView.replace(bitmap, "")
        }
    }

    private val matisseContractLauncher =
        registerForActivityResult(MatisseContract(), activityResultCallback)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        alertDialog = LoadingDialog.showLoader(this)
        builder = AlertDialog.Builder(this,android.R.style.Theme_Material_Dialog_Alert)

        val images = intent.getParcelableArrayListExtra<MediaResource>(Constants.CHOSEN_IMAGE)!!

        puzzleLayouts = PuzzleUtils.getPuzzleLayouts(images.size)
      //  initPerm()
        initView()
        count =
            if (images.size > puzzleLayouts[0].areaCount) puzzleLayouts[0].areaCount else images.size

        for (i in 0 until count) {
            val bitmap = BindingAdapter.uriToBitmap(images[i].uri, contentResolver)
            pieces.add(bitmap)
        }

        puzzleView.post { loadPhoto(pieces, count) }
        LoadingDialog.hideLoader(alertDialog)
    }

    private fun initView() {
        binding.backBtn.setOnClickListener { onBackPressed() }

        puzzleView = binding.puzzleView
        degreeSeekBar = binding.degreeSeekBar

        binding.layoutRV.setHasFixedSize(true)
        binding.layoutRV.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        puzzleView.puzzleLayout = puzzleLayouts[0]
        puzzleView.isTouchEnable = true
        puzzleView.isNeedDrawLine = false
        puzzleView.isNeedDrawOuterLine = false
        puzzleView.lineSize = 4
        puzzleView.lineColor = Color.BLACK
        puzzleView.selectedLineColor = Color.BLACK
        puzzleView.handleBarColor = Color.BLACK
        puzzleView.setAnimateDuration(300)
        puzzleView.setOnPieceSelectedListener { piece, position ->



            binding.bottomGalleryOption.visibility = View.INVISIBLE
            binding.operationBtn.visibility = View.VISIBLE


        }

        // currently the SlantPuzzleLayout do not support padding
        puzzleView.piecePadding = 10f

        binding.flipHorizontally.setOnClickListener(this)
        binding.rotate.setOnClickListener(this)
        binding.flipVertically.setOnClickListener(this)
        binding.border.setOnClickListener(this)
        binding.corner.setOnClickListener(this)
        binding.replace.setOnClickListener(this)
        binding.layouts.setOnClickListener(this)
        binding.save.setOnClickListener(this)


        degreeSeekBar.setCurrentDegrees(puzzleView.lineSize)
        degreeSeekBar.setDegreeRange(0, 30)

        degreeSeekBar.setScrollingListener(object : DegreeSeekBar.ScrollingListener {
            override fun onScrollStart() {}
            override fun onScroll(currentDegrees: Int) {
                when (controlFlag) {
                    FLAG_CONTROL_LINE_SIZE -> puzzleView.lineSize = currentDegrees
                    FLAG_CONTROL_CORNER -> puzzleView.pieceRadian = currentDegrees.toFloat()
                }
            }

            override fun onScrollEnd() {}
        })
    }

    private fun loadPhoto(data: MutableList<Bitmap>, count: Int) {
        if (data.size == count) {
            if (data.size < puzzleLayouts[0].areaCount) {
                for (i in 0 until puzzleLayouts[0].areaCount) {
                    puzzleView.addPiece(data[i % count])
                }
            } else {
                puzzleView.addPieces(data)
            }
        }

    }

    override fun onClick(view: View) {

        when (view.id) {
            R.id.layouts -> {
                binding.layoutRV.adapter = PuzzleAdapter(puzzleLayouts, pieces, this)
                binding.bottomGalleryOption.visibility = View.VISIBLE
                binding.operationBtn.visibility = View.INVISIBLE
                binding.layoutRV.visibility = View.VISIBLE
                binding.degreeSeekBar.visibility = View.GONE
            }
            R.id.replace -> showSelectedPhotoDialog()
            R.id.rotate -> puzzleView.rotate(90f)
            R.id.flip_horizontally -> puzzleView.flipHorizontally()
            R.id.flip_vertically -> puzzleView.flipVertically()
            R.id.border -> {
                binding.bottomGalleryOption.visibility = View.VISIBLE
                binding.operationBtn.visibility = View.INVISIBLE
                binding.layoutRV.visibility = View.GONE

                controlFlag = FLAG_CONTROL_LINE_SIZE
                puzzleView.isNeedDrawLine = !puzzleView.isNeedDrawLine

                if (puzzleView.isNeedDrawLine) {
                    degreeSeekBar.visibility = View.VISIBLE
                    degreeSeekBar.setCurrentDegrees(puzzleView.lineSize)
                    degreeSeekBar.setDegreeRange(0, 30)
                } else {
                    degreeSeekBar.visibility = View.INVISIBLE
                }

            }
            R.id.corner -> {
                binding.bottomGalleryOption.visibility = View.VISIBLE
                binding.operationBtn.visibility = View.INVISIBLE
                binding.layoutRV.visibility = View.GONE
                if (controlFlag == FLAG_CONTROL_CORNER && degreeSeekBar.visibility == View.VISIBLE) {
                    degreeSeekBar.visibility = View.INVISIBLE
                    return
                }
                degreeSeekBar.setCurrentDegrees(puzzleView.pieceRadian.toInt())
                controlFlag = FLAG_CONTROL_CORNER
                degreeSeekBar.visibility = View.VISIBLE
                degreeSeekBar.setDegreeRange(0, 100)

            }
            R.id.save -> {
               // PermissionUtil.updateOrRequestPermission(this,permissionLauncher)
                saveToGallery()
            }
        }
    }
   /* private fun initPerm() {
        if (!PermissionUtil.writePermissionGranted) {
            permissionLauncher =
                this.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                    PermissionUtil.writePermissionGranted =
                        permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE]
                            ?: PermissionUtil.writePermissionGranted
                    if (!PermissionUtil.writePermissionGranted) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        ) {
                            RationaleDialog.Builder(this, permissionLauncher).build()
                                .showCompatDialog()
                        } else {
                            SettingsDialog.Builder(this).build().show()
                        }
                    } else {
                        saveToGallery()

                    }
                }

        }
    }*/

    private fun createBitmapFromPuzzleView(puzzleView: PuzzleView): Bitmap {
        puzzleView.clearHandling()
        puzzleView.invalidate()
        val bitmap =
            Bitmap.createBitmap(puzzleView.width, puzzleView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        puzzleView.draw(canvas)
        return bitmap
    }

    private fun saveToGallery() {
       // if (PermissionUtil.writePermissionGranted) {
            lifecycleScope.launch {
                StorageHelper.savePhotoToExternalStorage(JPEG, SAVED_DIRECTORY,
                    "${System.currentTimeMillis()}.jpg",
                    createBitmapFromPuzzleView(puzzleView),
                    contentResolver,this@CollageActivity
                )
            }
     //   }
        finish()
    }

    private fun showSelectedPhotoDialog() {
        val matisse = Matisse(
            theme = DarkMatisseTheme,
            supportedMimeTypes =  Matisse.ofImage(hasGif = false),
            maxSelectable =1,
            spanCount = 3,
            captureStrategy = MediaStoreCaptureStrategy()
        )
        matisseContractLauncher.launch(matisse)
    }


    override fun onItemClick(puzzleLayout: PuzzleLayout?) {
        puzzleView.puzzleLayout = puzzleLayout
        puzzleView.post { loadPhoto(pieces, count) }
    }

    override fun onBackPressed() {
        builder.setTitle(getString(R.string.exit_without_save))
            .setIcon(R.drawable.ic_announcement_24)
            .setMessage(getString(R.string.exit_body))
            .setCancelable(true)
            .setNeutralButton("Save"){_,_ ->
                saveToGallery()
            }
            .setPositiveButton("Confirm"){_,_ ->
                super.onBackPressed()
            }
            .setNegativeButton("Cancel"){dialogInterface,_ ->
                dialogInterface.dismiss()
            }
            .show()
    }

}