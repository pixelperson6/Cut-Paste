package com.codingwithpix3l.photoonphoto.core.view

import android.annotation.SuppressLint
import android.graphics.Paint.FontMetricsInt
import kotlin.jvm.JvmOverloads
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class DegreeSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val mTextPaint= Paint()
    private val mCirclePaint = Paint()
    private var mFontMetrics: FontMetricsInt
    private var mBaseLine = 0
    private var mTextWidths = FloatArray(1)
    private val mCanvasClipBounds = Rect()
    private var mScrollingListener: ScrollingListener? = null
    private var mLastTouchedPosition = 0f
    private var mPointPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mPointMargin = 0f
    private var mScrollStarted = false
    private var mTotalScrollDistance = 0
    private val mIndicatorPath = Path()
    private var mCurrentDegrees = 0
    private val mPointCount = 51
    private var mPointColor = Color.BLACK
    private var mTextColor = Color.BLACK
    private var mCenterTextColor = Color.BLACK

    //阻尼系数的倒数
    private var dragFactor = 2.1f
    private var mMinReachableDegrees = -45
    private var mMaxReachableDegrees = 45
    private var suffix = ""


     init{
         mPointPaint.style = Paint.Style.STROKE
        mPointPaint.color = mPointColor
        mPointPaint.strokeWidth = 2f
        mTextPaint.color = mTextColor
        mTextPaint.style = Paint.Style.STROKE
        mTextPaint.isAntiAlias = true
        mTextPaint.textSize = 24f
        mTextPaint.textAlign = Paint.Align.LEFT
        mTextPaint.alpha = 100
        mFontMetrics = mTextPaint.fontMetricsInt
        mTextPaint.getTextWidths("0", mTextWidths)
        mCirclePaint.style = Paint.Style.FILL
        mCirclePaint.alpha = 255
        mCirclePaint.isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mPointMargin = w.toFloat() / mPointCount
        mBaseLine = (h - mFontMetrics.bottom + mFontMetrics.top) / 2 - mFontMetrics.top
        mIndicatorPath.moveTo((w / 2).toFloat(), (h / 2 + mFontMetrics.top / 2 - 18).toFloat())
        mIndicatorPath.rLineTo(-8f, -8f)
        mIndicatorPath.rLineTo(16f, 0f)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastTouchedPosition = event.x
                if (!mScrollStarted) {
                    mScrollStarted = true
                    if (mScrollingListener != null) {
                        mScrollingListener!!.onScrollStart()
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                mScrollStarted = false
                if (mScrollingListener != null) {
                    mScrollingListener!!.onScrollEnd()
                }
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                val distance = event.x - mLastTouchedPosition
                if (mCurrentDegrees >= mMaxReachableDegrees && distance < 0) {
                    mCurrentDegrees = mMaxReachableDegrees
                    invalidate()
                    return true
                }
                if (mCurrentDegrees <= mMinReachableDegrees && distance > 0) {
                    mCurrentDegrees = mMinReachableDegrees
                    invalidate()
                    return true
                }
                if (distance != 0f) {
                    onScrollEvent(event, distance)
                }
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.getClipBounds(mCanvasClipBounds)
        val zeroIndex = mPointCount / 2 + (0 - mCurrentDegrees) / 2
        mPointPaint.color = mPointColor
        for (i in 0 until mPointCount) {
            if (i > zeroIndex - abs(mMinReachableDegrees) / 2 && i < zeroIndex + abs(
                    mMaxReachableDegrees
                ) / 2 && mScrollStarted
            ) {
                mPointPaint.alpha = 255
            } else {
                mPointPaint.alpha = 100
            }
            if (i > mPointCount / 2 - 8 && i < mPointCount / 2 + 8 && i > zeroIndex - abs(
                    mMinReachableDegrees
                ) / 2 && i < zeroIndex + abs(mMaxReachableDegrees) / 2
            ) {
                if (mScrollStarted) {
                    mPointPaint.alpha = abs(mPointCount / 2 - i) * 255 / 8
                } else {
                    mPointPaint.alpha = abs(mPointCount / 2 - i) * 100 / 8
                }
            }
            canvas.drawPoint(
                mCanvasClipBounds.centerX() + (i - mPointCount / 2) * mPointMargin,
                mCanvasClipBounds.centerY().toFloat(), mPointPaint
            )
            if (mCurrentDegrees != 0 && i == zeroIndex) {
                if (mScrollStarted) {
                    mTextPaint.alpha = 255
                } else {
                    mTextPaint.alpha = 192
                }
                mPointPaint.strokeWidth = 4f
                canvas.drawPoint(
                    mCanvasClipBounds.centerX() + (i - mPointCount / 2) * mPointMargin,
                    mCanvasClipBounds.centerY().toFloat(), mPointPaint
                )
                mPointPaint.strokeWidth = 2f
                mTextPaint.alpha = 100
            }
        }
        var i = -180
        while (i <= 180) {
            if (i in mMinReachableDegrees..mMaxReachableDegrees) {
                drawDegreeText(i, canvas, true)
            } else {
                drawDegreeText(i, canvas, false)
            }
            i += 15
        }
        mTextPaint.textSize = 28f
        mTextPaint.alpha = 255
        mTextPaint.color = mCenterTextColor
        when {
            mCurrentDegrees >= 10 -> {
                canvas.drawText(
                    mCurrentDegrees.toString() + suffix,
                    width / 2 - mTextWidths[0],
                    mBaseLine.toFloat(),
                    mTextPaint
                )
            }
            mCurrentDegrees <= -10 -> {
                canvas.drawText(
                    mCurrentDegrees.toString() + suffix,
                    width / 2 - mTextWidths[0] / 2 * 3,
                    mBaseLine.toFloat(),
                    mTextPaint
                )
            }
            mCurrentDegrees < 0 -> {
                canvas.drawText(
                    mCurrentDegrees.toString() + suffix,
                    width / 2 - mTextWidths[0],
                    mBaseLine.toFloat(),
                    mTextPaint
                )
            }
            else -> {
                canvas.drawText(
                    mCurrentDegrees.toString() + suffix,
                    width / 2 - mTextWidths[0] / 2,
                    mBaseLine.toFloat(),
                    mTextPaint
                )
            }
        }
        mTextPaint.alpha = 100
        mTextPaint.textSize = 24f
        mTextPaint.color = mTextColor
        //画中心三角
        mCirclePaint.color = mCenterTextColor
        canvas.drawPath(mIndicatorPath, mCirclePaint)
        mCirclePaint.color = mCenterTextColor
    }

    private fun drawDegreeText(degrees: Int, canvas: Canvas, canReach: Boolean) {
        if (canReach) {
            if (mScrollStarted) {
                mTextPaint.alpha =
                    255.coerceAtMost(abs(degrees - mCurrentDegrees) * 255 / 15)
                if (abs(degrees - mCurrentDegrees) <= 7) {
                    mTextPaint.alpha = 0
                }
            } else {
                mTextPaint.alpha = 100
                if (abs(degrees - mCurrentDegrees) <= 7) {
                    mTextPaint.alpha = 0
                }
            }
        } else {
            mTextPaint.alpha = 100
        }
        if (degrees == 0) {
            if (abs(mCurrentDegrees) >= 15 && !mScrollStarted) {
                mTextPaint.alpha = 180
            }
            canvas.drawText(
                "0°",
                width / 2 - mTextWidths[0] / 2 - mCurrentDegrees / 2 * mPointMargin, (
                        height / 2 - 10).toFloat(), mTextPaint
            )
        } else {
            canvas.drawText(
                degrees.toString() + suffix,
                width / 2 + mPointMargin * degrees / 2 - mTextWidths[0] / 2 * 3 - mCurrentDegrees / 2 * mPointMargin,
                (height / 2 - 10).toFloat(),
                mTextPaint
            )
        }
    }

    private fun onScrollEvent(event: MotionEvent, distance: Float) {
        mTotalScrollDistance -= distance.toInt()
        postInvalidate()
        mLastTouchedPosition = event.x
        mCurrentDegrees = (mTotalScrollDistance * dragFactor / mPointMargin).toInt()
        if (mScrollingListener != null) {
            mScrollingListener!!.onScroll(mCurrentDegrees)
        }
    }

    fun setDegreeRange(min: Int, max: Int) {
        if (min <= max) {
            mMinReachableDegrees = min
            mMaxReachableDegrees = max
            if (mCurrentDegrees > mMaxReachableDegrees || mCurrentDegrees < mMinReachableDegrees) {
                mCurrentDegrees = (mMinReachableDegrees + mMaxReachableDegrees) / 2
            }
            mTotalScrollDistance = (mCurrentDegrees * mPointMargin / dragFactor).toInt()
            invalidate()
        }
    }

    fun setCurrentDegrees(degrees: Int) {
        if (degrees in mMinReachableDegrees..mMaxReachableDegrees) {
            mCurrentDegrees = degrees
            mTotalScrollDistance = (degrees * mPointMargin / dragFactor).toInt()
            invalidate()
        }
    }

    fun setScrollingListener(scrollingListener: ScrollingListener?) {
        mScrollingListener = scrollingListener
    }

    var pointColor: Int
        get() = mPointColor
        set(pointColor) {
            mPointColor = pointColor
            mPointPaint.color = mPointColor
            postInvalidate()
        }
    var textColor: Int
        get() = mTextColor
        set(textColor) {
            mTextColor = textColor
            mTextPaint.color = textColor
            postInvalidate()
        }
    var centerTextColor: Int
        get() = mCenterTextColor
        set(centerTextColor) {
            mCenterTextColor = centerTextColor
            postInvalidate()
        }

    fun setSuffix(suffix: String) {
        this.suffix = suffix
    }

    interface ScrollingListener {
        fun onScrollStart()
        fun onScroll(currentDegrees: Int)
        fun onScrollEnd()
    }

    companion object {
        private const val TAG = "DegreeSeekBar"
    }
}