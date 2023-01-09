package com.codingwithpix3l.imagepicker.internal.ui

import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.codingwithpix3l.imagepicker.CheckBoxTheme
import com.codingwithpix3l.imagepicker.internal.theme.LocalMatisseTheme


private val defaultSize = 22.dp
private val defaultStokeWidth = 2.dp

@Composable
internal fun MatisseCheckbox(
    modifier: Modifier,
    theme: CheckBoxTheme,
    text: String,
    enabled: Boolean,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val localDensity = LocalDensity.current
    val textPaint = remember {
        Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            isDither = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textAlign = android.graphics.Paint.Align.CENTER
            with(localDensity) {
                textSize = theme.fontSize.toPx()
                color = theme.textColor.toArgb()
            }
        }
    }
    val alphaIfDisable = LocalMatisseTheme.current.alphaIfDisable
    Canvas(
        modifier = modifier
            .wrapContentSize(align = Alignment.Center)
            .requiredSize(size = defaultSize)
            .triStateToggleable(
                state = ToggleableState(value = checked),
                onClick = {
                    onCheckedChange(!checked)
                },
                enabled = true,
                role = Role.Checkbox,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = false,
                    radius = defaultSize
                )
            )
    ) {
        val checkBoxSide = minOf(size.width, size.height)
        val stokeWidth = defaultStokeWidth.toPx()
        val outRadius = (checkBoxSide - stokeWidth) / 2f
        drawCircle(
            color = if (enabled) {
                theme.circleColor
            } else {
                theme.circleColor.copy(alpha = alphaIfDisable)
            },
            radius = outRadius,
            style = Stroke(width = stokeWidth)
        )
        if (checked) {
            drawCircle(
                color = theme.circleFillColor,
                radius = outRadius
            )
        }
        if (theme.countable && text.isNotBlank()) {
            drawTextToCenter(
                text = text,
                textPaint = textPaint,
            )
        }
    }
}

private fun DrawScope.drawTextToCenter(text: String, textPaint: android.graphics.Paint) {
    val fontMetrics = textPaint.fontMetrics
    val x = size.width / 2f
    val y = size.height / 2f - (fontMetrics.top + fontMetrics.bottom) / 2f
    drawContext.canvas.nativeCanvas.drawText(text, x, y, textPaint)
}