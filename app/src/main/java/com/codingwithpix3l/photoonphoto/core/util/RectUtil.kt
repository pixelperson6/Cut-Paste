package com.codingwithpix3l.photoonphoto.core.util

import android.graphics.Rect
import android.graphics.RectF
import kotlin.math.cos
import kotlin.math.sin

object RectUtil {
    private const val CHAR_MIN_HEIGHT = 60
    @JvmStatic
    fun scaleRect(rect: RectF, scale: Float) {
        val w = rect.width()
        val h = rect.height()
        val newW = scale * w
        val newH = scale * h
        val dx = (newW - w) / 2
        val dy = (newH - h) / 2
        rect.left -= dx
        rect.top -= dy
        rect.right += dx
        rect.bottom += dy
    }

    @JvmStatic
    fun rotateRect(
        rect: RectF, center_x: Float, center_y: Float,
        rotateAngle: Float
    ) {
        val x = rect.centerX()
        val y = rect.centerY()
        val sinA = sin(Math.toRadians(rotateAngle.toDouble()))
            .toFloat()
        val cosA = cos(Math.toRadians(rotateAngle.toDouble()))
            .toFloat()
        val newX = center_x + (x - center_x) * cosA - (y - center_y) * sinA
        val newY = center_y + (y - center_y) * cosA + (x - center_x) * sinA
        val dx = newX - x
        val dy = newY - y
        rect.offset(dx, dy)
    }

    fun rectAddV(srcRect: RectF, addRect: RectF, padding: Int) {
        val left = srcRect.left
        val top = srcRect.top
        var right = srcRect.right
        var bottom = srcRect.bottom
        if (srcRect.width() <= addRect.width()) {
            right = left + addRect.width()
        }
        bottom += padding + addRect.height()
        srcRect[left, top, right] = bottom
    }

    fun rectAddV(srcRect: Rect, addRect: Rect, padding: Int, charMinHeight: Int) {
        val left = srcRect.left
        val top = srcRect.top
        var right = srcRect.right
        var bottom = srcRect.bottom
        if (srcRect.width() <= addRect.width()) {
            right = left + addRect.width()
        }
        bottom += padding + addRect.height().coerceAtLeast(charMinHeight)
        srcRect[left, top, right] = bottom
    }

    /**
     * Rectangular addition operation in the Y-axis direction
     *
     * @param srcRect
     * @param addRect
     * @param padding
     */

    fun rectAddV(srcRect: Rect, addRect: Rect, padding: Int) {
        val left = srcRect.left
        val top = srcRect.top
        var right = srcRect.right
        var bottom = srcRect.bottom
        if (srcRect.width() <= addRect.width()) {
            right = left + addRect.width()
        }
        bottom += padding + addRect.height().coerceAtLeast(CHAR_MIN_HEIGHT)
        srcRect[left, top, right] = bottom
    }
}