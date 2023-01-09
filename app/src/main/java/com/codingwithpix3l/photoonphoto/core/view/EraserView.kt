package com.codingwithpix3l.photoonphoto.core.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.ColorUtils
import com.codingwithpix3l.photoonphoto.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.Segmenter
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import java.nio.ByteBuffer
import kotlin.math.abs
import kotlin.math.sqrt

@SuppressLint("ViewConstructor")
class EraserView(
    private var mContext: Context,
    private var mainBitmap:Bitmap,
    private var bmWidth: Int,
    private var bmHeight: Int,
    private var viewWidth: Int,
    private var viewHeight: Int,
    val updateUndoRedoButton: () -> Unit,
    val hideLoadingDialog: () -> Unit
) : View(
    mContext
) {


    private lateinit var bm: Bitmap
    private lateinit var clippedBitmap: Bitmap
    private lateinit var magicPointer: Bitmap
    private lateinit var thumbPointer: Bitmap
    private lateinit var saveBitmapData: IntArray
    private lateinit var lastBitmapData: IntArray
    private lateinit var mBitmapPaint: Paint
    private lateinit var mMaskPaint: Paint
    private lateinit var touchPoint: PointF
    private lateinit var drawingPoint: PointF

    private lateinit var newCanvas: Canvas
    private lateinit var eraser: Paint
    private lateinit var mPath: Path

    var mMagicThreshold = 15

    private var mX = 0f
    private var mY = 0f
    var strokeWidth = 40
    var touchMode = NONE

    private lateinit var stackChange: ArrayList<IntArray>
    private lateinit var checkMirrorStep: ArrayList<Boolean>
    var currentIndex = -1

    private var mode = MOVING

    companion object {

        var ERASE = 0
        var RESTORE = 1
        var MAGIC = 2
        var MOVING = 4
        //  var MIRROR = 5

        private const val TOUCH_TOLERANCE = 4f
        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2
        var POINTER_DISTANCE = 0

        const val STACK_SIZE = 30
        val TAG = EraserView::class.simpleName
    }

    var downPt = PointF() // Record Mouse Position When Pressed Down

    // these PointF objects are used to record the point(s) the user is touching
    var start = PointF()
    var mid = PointF()
    private var oldDist = 1f
    private var mMatrix = Matrix()
    private var savedMatrix = Matrix()
    private var scale = 1.0f



    //segment helper
    private val segmenter: Segmenter
    private var maskBuffer = ByteBuffer.allocate(0)
    private var maskWidth = 0
    private var maskHeight = 0


    fun switchMode(_mode: Int) {
        mode = _mode
        mPath.reset()
        saveLastMaskData()
        if (mode == MAGIC) {
            magicPointer = BitmapFactory.decodeResource(resources, R.drawable.color_select)
        } else if (mode == ERASE || mode == RESTORE) {
            magicPointer = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.color_select
                ), strokeWidth + 5, strokeWidth + 5, false
            )
        }
        invalidate()
    }

    fun mirrorImage() {
        val matrix = Matrix()
        matrix.preScale(-1.0f, 1.0f)
        bm = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
        clippedBitmap = Bitmap.createBitmap(
            clippedBitmap,
            0,
            0,
            clippedBitmap.width,
            clippedBitmap.height,
            matrix,
            true
        )
        newCanvas = Canvas(clippedBitmap)
        newCanvas.save()
        invalidate()
        addToStack(true)
    }

    fun setEraseOffset(offSet: Int) {
        strokeWidth = offSet
        eraser.strokeWidth = offSet.toFloat()
        magicPointer = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                resources,
                R.drawable.color_select
            ), offSet + 5, offSet + 5, false
        )
        mPath.reset()
        invalidate()
    }

    fun changePointerDistance(distance: Int) {
        POINTER_DISTANCE = distance
        mPath.reset()
        invalidate()
    }

    private fun init(bitmap: Bitmap, w: Int, h: Int) {
        mPath = Path()
        eraser = Paint()
        eraser.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        eraser.isAntiAlias = true
        eraser.style = Paint.Style.STROKE
        eraser.strokeJoin = Paint.Join.ROUND
        eraser.strokeCap = Paint.Cap.ROUND
        eraser.strokeWidth = strokeWidth.toFloat()
        mMatrix.postTranslate(((viewWidth - w) / 2).toFloat(), ((viewHeight - h) / 2).toFloat())
        mBitmapPaint = Paint()
        mBitmapPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        mBitmapPaint.isAntiAlias = true
        mMaskPaint = Paint()
        mMaskPaint.isAntiAlias = true
        bm = bitmap
        bm = bm.copy(Bitmap.Config.ARGB_8888, true)
        clippedBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        newCanvas = Canvas(clippedBitmap)
        newCanvas.save()
        // Draws the mask photo.
        newCanvas.drawARGB(255, 255, 255, 255)
       // newCanvas.drawBitmap(maskBitmap, 0f, 0f, mMaskPaint)

        saveBitmapData = IntArray(w * h)
        bm.getPixels(saveBitmapData, 0, bm.width, 0, 0, bm.width, bm.height)
        lastBitmapData = IntArray(w * h)
        magicPointer = BitmapFactory.decodeResource(resources, R.drawable.color_select)
        thumbPointer = BitmapFactory.decodeResource(resources, R.drawable.color_select)
        touchPoint = PointF((w / 2).toFloat(), (h / 2).toFloat())
        drawingPoint = PointF((w / 2).toFloat(), (h / 2).toFloat())
        saveLastMaskData()
        stackChange = ArrayList()
        checkMirrorStep = ArrayList()
       // addToStack(false)
        POINTER_DISTANCE = (50 * mContext.resources.displayMetrics.density).toInt()
    }

    private fun addToStack(isMirror: Boolean) {
        if (stackChange.size >= STACK_SIZE) {
            stackChange.removeAt(0)
            if (currentIndex > 0) currentIndex--
        }
        if (currentIndex == 0) {
            val size = stackChange.size
            for (i in size - 1 downTo 1) {
                stackChange.removeAt(i)
                checkMirrorStep.removeAt(i)
            }
        }
        val pix = IntArray(clippedBitmap.width * clippedBitmap.height)
        clippedBitmap.getPixels(
            pix,
            0,
            clippedBitmap.width,
            0,
            0,
            clippedBitmap.width,
            clippedBitmap.height
        )
        stackChange.add(pix)
        checkMirrorStep.add(isMirror)
        currentIndex = stackChange.size - 1
    }

    fun redo() {
        mPath.reset()
        if (stackChange.size > 0 && currentIndex < stackChange.size - 1) {
            currentIndex++
            if (checkMirrorStep[currentIndex]) {
                val matrix = Matrix()
                matrix.preScale(-1.0f, 1.0f)
                bm = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
                bm.getPixels(saveBitmapData, 0, bm.width, 0, 0, bm.width, bm.height)
            }
            val pix = stackChange[currentIndex]
            clippedBitmap.setPixels(pix, 0, bmWidth, 0, 0, bmWidth, bmHeight)
            invalidate()
        }
    }

    fun undo() {
        mPath.reset()
        if (stackChange.size > 0 && currentIndex > 0) {
            currentIndex--
            if (checkMirrorStep[currentIndex + 1]) {
                val matrix = Matrix()
                matrix.preScale(-1.0f, 1.0f)
                bm = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
                bm.getPixels(saveBitmapData, 0, bm.width, 0, 0, bm.width, bm.height)
            }
            val pix = stackChange[currentIndex]
            clippedBitmap.setPixels(pix, 0, bmWidth, 0, 0, bmWidth, bmHeight)
            invalidate()
        }
    }

    fun checkUndoEnable(): Boolean {
        return stackChange.size > 0 && currentIndex > 0
    }

    fun checkRedoEnable(): Boolean {
        return stackChange.size > 0 && currentIndex < stackChange.size - 1
    }

    private fun drawBitmap(): Bitmap {
        if (mode == ERASE || mode == RESTORE) {
            val strokeRatio = scale
            eraser.strokeWidth = strokeWidth / strokeRatio
            if (mode == ERASE) {
                eraser.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            } else {
                eraser.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
            }
            newCanvas.drawPath(mPath, eraser)
        }
        return clippedBitmap
    }

    fun saveDrawnBitmap(): Bitmap {
        val saveBitmap = Bitmap.createBitmap(bm.width, bm.height, Bitmap.Config.ARGB_8888)
        val paint = Paint()
        val cv = Canvas(saveBitmap)
        cv.save()

        // Draws the photo.
        cv.drawBitmap(bm, 0f, 0f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        cv.drawBitmap(clippedBitmap, 0f, 0f, paint)
        return saveBitmap
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(bm, mMatrix, mMaskPaint)
        canvas.drawBitmap(drawBitmap(), mMatrix, mBitmapPaint)
        if (mode == MAGIC || mode == ERASE || mode == RESTORE) {
            canvas.drawBitmap(
                magicPointer,
                drawingPoint.x - magicPointer.width / 2,
                drawingPoint.y - magicPointer.height / 2,
                mMaskPaint
            )
            canvas.drawBitmap(
                thumbPointer,
                drawingPoint.x - thumbPointer.width / 2,
                drawingPoint.y + POINTER_DISTANCE - thumbPointer.height / 2,
                mMaskPaint
            )
        }
        super.onDraw(canvas)
    }

/*    private fun autoEraseBitmap():Bitmap{
        val mWidth = clippedBitmap.width
        val mHeight = clippedBitmap.height
        val pix = IntArray(clippedBitmap.width * clippedBitmap.height)
        clippedBitmap.getPixels(
            pix,
            0,
            clippedBitmap.width,
            0,
            0,
            clippedBitmap.width,
            clippedBitmap.height
        )
        return clippedBitmap
    }*/

    private fun magicEraseBitmap(): Bitmap {
        val mWidth = clippedBitmap.width
        val mHeight = clippedBitmap.height
        val pix = IntArray(clippedBitmap.width * clippedBitmap.height)
        clippedBitmap.getPixels(
            pix,
            0,
            clippedBitmap.width,
            0,
            0,
            clippedBitmap.width,
            clippedBitmap.height
        )
        val xTouch = touchPoint.x.toInt()
        val yTouch = touchPoint.y.toInt()
        if (xTouch > mWidth || xTouch < 0 || yTouch > mHeight || yTouch < 0) return clippedBitmap
        // val aT = pix[yTouch * mWidth + xTouch] shr 24 and 0xff
        val rT = saveBitmapData[yTouch * mWidth + xTouch] shr 16 and 0xff
        val gT = saveBitmapData[yTouch * mWidth + xTouch] shr 8 and 0xff
        val bT = saveBitmapData[yTouch * mWidth + xTouch] and 0xff

        val left = 0
        val right: Int = mWidth
        val top = 0
        val bottom: Int = mHeight
        for (y in top until bottom) {
            for (x in left until right) {
                val index = y * mWidth + x
                val aMask = pix[index] shr 24 and 0xff
                val a = saveBitmapData[index] shr 24 and 0xff
                val r = saveBitmapData[index] shr 16 and 0xff
                val g = saveBitmapData[index] shr 8 and 0xff
                val b = saveBitmapData[index] and 0xff
                val lastAlphaMask = lastBitmapData[index] shr 24 and 0xff
                if (aMask > 0 && abs(r - rT) < mMagicThreshold && abs(g - gT) < mMagicThreshold && abs(b - bT ) < mMagicThreshold) {
                    pix[index] = 0.shl(16) or (0 shl 8) or 0 or (0 shl 24)
                } else if (lastAlphaMask > 0 && aMask == 0 && (abs(r - rT) >= mMagicThreshold || abs(g - gT ) >= mMagicThreshold || abs(b - bT) >= mMagicThreshold)
                ) {
                    pix[index] = r shl 16 or (g shl 8) or b or (a shl 24)
                }
            }
        }
        clippedBitmap.setPixels(pix, 0, mWidth, 0, 0, mWidth, mHeight)
        return clippedBitmap

    }

    private fun saveLastMaskData() {
        clippedBitmap.getPixels(
            lastBitmapData,
            0,
            clippedBitmap.width,
            0,
            0,
            clippedBitmap.width,
            clippedBitmap.height
        )
    }

    /* fun invalidateView() {
         invalidate()
     }*/

    private fun touchStart(x: Float, y: Float) {
        mPath.reset()
        mPath.moveTo(x, y)
        mX = x
        mY = y
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = abs(x - mX)
        val dy = abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
    }

    private fun touchUp() {
        mPath.lineTo(mX, mY)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var x = event.x
        var y = event.y
        if (mode == MAGIC || mode == ERASE || mode == RESTORE) {
            y -= POINTER_DISTANCE
        }
        if (mode == MAGIC || mode == ERASE || mode == RESTORE) {
            drawingPoint.x = x
            drawingPoint.y = y
        }
        if (mode != MOVING) {
            val v = FloatArray(9)
            mMatrix.getValues(v)
            // translation 
            val mScalingFactor = v[Matrix.MSCALE_X]
            val r = RectF()
            mMatrix.mapRect(r)

            // mScalingFactor shall contain the scale/zoom factor
            var scaledX = x - r.left
            var scaledY = y - r.top
            scaledX /= mScalingFactor
            scaledY /= mScalingFactor
            x = scaledX
            y = scaledY
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(mMatrix)
                start[event.x] = event.y
                touchMode = DRAG
                when (mode) {
                    ERASE, RESTORE, MAGIC -> {
                        touchStart(x, y)
                    }
                    MOVING -> {
                        downPt.x = event.x
                        downPt.y = event.y
                    }
                }
                updateUndoRedoButton()
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> if (touchMode == DRAG) {
                when (mode) {
                    ERASE, RESTORE -> {
                        touchMove(x,y)
                    }
                    MOVING -> {
                        val mv = PointF(event.x - downPt.x, event.y - downPt.y)
                        mMatrix.postTranslate(mv.x, mv.y)
                        downPt.x = event.x
                        downPt.y = event.y
                    }
                    MAGIC -> {
                        touchPoint.x = x
                        touchPoint.y = y
                    }
                }
                invalidate()
            } else if (touchMode == ZOOM && mode == MOVING) {
                // pinch zooming
                val newDist = spacing(event)
                if (newDist > 5f) {
                    mMatrix.set(savedMatrix)
                    scale = newDist / oldDist
                    // setting the scaling of the
                    // matrix...if scale > 1 means
                    // zoom in...if scale < 1 means
                    // zoom out
                    mMatrix.postScale(scale, scale, mid.x, mid.y)
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                when (mode) {
                    ERASE, RESTORE -> {
                        touchUp()
                        addToStack(false)
                    }
                    MAGIC -> {
                        touchPoint.x = x
                        touchPoint.y = y
                        saveLastMaskData()
                        mMagicThreshold = 15
                        magicEraseBitmap()
                        addToStack(false)
                        invalidate()
                    }
                }
                updateUndoRedoButton()
                invalidate()
                mPath.reset()
            }
            MotionEvent.ACTION_POINTER_UP -> {
                touchMode = NONE
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                if (oldDist > 5f) {
                    savedMatrix.set(mMatrix)
                    midPoint(mid, event)
                    touchMode = ZOOM

                }
            }
        }
        return true
    }

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
     */
    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt((x * x + y * y).toDouble()).toFloat()
    }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */
    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point[x / 2] = y / 2
    }

    /**
     * Processes a bitmap and informs the listener on success
     */
    private fun processImage(image: Bitmap) {
        val input = InputImage.fromBitmap(image, 0)
        segmenter.process(input)
            .addOnSuccessListener { segmentationMask ->
                // Store all information so we can reuse it if e.g. a background images is chosen
                maskBuffer = segmentationMask.buffer
                maskWidth = segmentationMask.width
                maskHeight = segmentationMask.height
               // listener.imageProcessed()
                generateFrontMask(image)
                addToStack(false)
                invalidate()
                hideLoadingDialog()
            }
            .addOnFailureListener { e ->
                addToStack(false)
                hideLoadingDialog()
            }
    }

    private fun generateFrontMask(image: Bitmap): Bitmap {
        //val maskBitmap = Bitmap.createBitmap(image.width, image.height, image.config)

        val pix = IntArray(clippedBitmap.width * clippedBitmap.height)
        clippedBitmap.getPixels(
            pix,
            0,
            clippedBitmap.width,
            0,
            0,
            clippedBitmap.width,
            clippedBitmap.height
        )

        for (y in 0 until maskHeight) {
            for (x in 0 until maskWidth) {

                val index = y * maskWidth + x
                val a = saveBitmapData[index] shr 24 and 0xff
                val r = saveBitmapData[index] shr 16 and 0xff
                val g = saveBitmapData[index] shr 8 and 0xff
                val b = saveBitmapData[index] and 0xff
                when (val fgConfidence = (( maskBuffer.float) * 255).toInt()) {
                    0 -> {
                        pix[index] = 0.shl(16) or (0 shl 8) or 0 or (0 shl 24)
                    }
                    1 -> {
                        pix[index] = r shl 16 or (g shl 8) or b or (a shl 24)
                    }
                    else -> {
                        var fgPixel = image.getPixel(x, y)
                        fgPixel = ColorUtils.setAlphaComponent(fgPixel, fgConfidence)
                        pix[index] =fgPixel
                    }
                }
               // var fgPixel = image.getPixel(x, y)
              //  fgPixel = ColorUtils.setAlphaComponent(fgPixel, fgConfidence)
              //  maskBitmap.setPixel(x, y, fgPixel)
            }
        }
        maskBuffer.rewind()
        clippedBitmap.setPixels(pix, 0, maskWidth, 0, 0, maskWidth, maskHeight)
        return clippedBitmap
       // return maskBitmap
    }

    init {
        val options =
            SelfieSegmenterOptions.Builder()
                .setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE)
                .build()

        segmenter = Segmentation.getClient(options)
        processImage(mainBitmap)
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        init(mainBitmap, bmWidth, bmHeight)
    }
}