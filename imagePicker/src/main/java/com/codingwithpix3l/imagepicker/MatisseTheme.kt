package com.codingwithpix3l.imagepicker

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp


data class MatisseTheme(
    val surfaceColor: Color,
    val onPreviewSurfaceColor: Color,
    val imageBackgroundColor: Color,
    val alphaIfDisable: Float,
    val topAppBarTheme: TopAppBarTheme,
    val bottomNavigationTheme: BottomNavigationTheme,
    val captureIconTheme: CaptureIconTheme,
    val dropdownMenuTheme: DropdownMenuTheme,
    val checkBoxTheme: CheckBoxTheme,
    val previewButtonTheme: PreviewButtonTheme,
    val sureButtonTheme: SureButtonTheme,
    val systemBarsTheme: SystemBarsTheme,
)

data class CaptureIconTheme(
    val backgroundColor: Color,
    val icon: ImageVector,
    val tint: Color,
)

data class TopAppBarTheme(
    val defaultBucketName: String,
    val backgroundColor: Color,
    val contentColor: Color,
    val fontSize: TextUnit,
)

data class BottomNavigationTheme(
    val backgroundColor: Color
)

data class DropdownMenuTheme(
    val backgroundColor: Color,
    val textStyle: TextStyle,
)

data class PreviewButtonTheme(
    val textBuilder: (selectedSize: Int, maxSelectable: Int) -> String,
    val textStyle: TextStyle,
)

data class SureButtonTheme(
    val backgroundColor: Color,
    val textBuilder: (selectedSize: Int, maxSelectable: Int) -> String,
    val textStyle: TextStyle,
)

data class CheckBoxTheme(
    val countable: Boolean,
    val frameColor: Color,
    val circleColor: Color,
    val circleFillColor: Color,
    val fontSize: TextUnit,
    val textColor: Color,
)

data class SystemBarsTheme(
    val statusBarColor: Color,
    val statusBarDarkIcons: Boolean,
    val navigationBarColor: Color,
    val navigationBarDarkIcons: Boolean
)

private val whiteColo = Color.White
private val blackColor = Color.Black
private val darkColor = Color(color = 0xFF22202A)
private val blueColor = Color(color = 0xFF2196F3)
private val greenColor = Color(color = 0xFF009688)
private val cameraIcon = Icons.Filled.PhotoCamera
private val previewTextBuilder = { selectedSize: Int, maxSelectable: Int ->
    "Preview($selectedSize/$maxSelectable)"
}
private val sureTextBuilder = { _: Int, _: Int ->
    "Sure"
}
private const val alphaIfDisable = 0.5f
private const val defaultBucketName = "All Pictures"

val LightMatisseTheme = MatisseTheme(
    surfaceColor = whiteColo,
    onPreviewSurfaceColor = darkColor,
    imageBackgroundColor = Color.LightGray.copy(alpha = 0.4f),
    alphaIfDisable = alphaIfDisable,
    captureIconTheme = CaptureIconTheme(
        backgroundColor = Color(0xFFF1F1F1),
        icon = cameraIcon,
        tint = blackColor.copy(alpha = 0.4f),
    ),
    topAppBarTheme = TopAppBarTheme(
        defaultBucketName = defaultBucketName,
        backgroundColor = whiteColo,
        contentColor = blackColor,
        fontSize = 18.sp,
    ),
    bottomNavigationTheme = BottomNavigationTheme(
        backgroundColor = whiteColo,
    ),
    dropdownMenuTheme = DropdownMenuTheme(
        backgroundColor = whiteColo,
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = blackColor,
        ),
    ),
    checkBoxTheme = CheckBoxTheme(
        frameColor = blueColor,
        circleColor = whiteColo,
        circleFillColor = blueColor,
        textColor = whiteColo,
        countable = true,
        fontSize = 14.sp,
    ),
    previewButtonTheme = PreviewButtonTheme(
        textBuilder = previewTextBuilder,
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = blackColor,
        ),
    ),
    sureButtonTheme = SureButtonTheme(
        textBuilder = sureTextBuilder,
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = whiteColo,
        ),
        backgroundColor = blueColor,
    ),
    systemBarsTheme = SystemBarsTheme(
        statusBarColor = whiteColo,
        statusBarDarkIcons = true,
        navigationBarColor = whiteColo,
        navigationBarDarkIcons = true
    ),
)

val DarkMatisseTheme = MatisseTheme(
    surfaceColor = darkColor,
    onPreviewSurfaceColor = darkColor,
    imageBackgroundColor = Color.LightGray.copy(alpha = 0.4f),
    alphaIfDisable = alphaIfDisable,
    captureIconTheme = CaptureIconTheme(
        backgroundColor = Color.LightGray.copy(alpha = 0.4f),
        icon = cameraIcon,
        tint = whiteColo,
    ),
    topAppBarTheme = TopAppBarTheme(
        defaultBucketName = defaultBucketName,
        backgroundColor = darkColor,
        contentColor = whiteColo,
        fontSize = 18.sp,
    ),
    bottomNavigationTheme = BottomNavigationTheme(
        backgroundColor = darkColor,
    ),
    dropdownMenuTheme = DropdownMenuTheme(
        backgroundColor = darkColor,
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = whiteColo,
        ),
    ),
    checkBoxTheme = CheckBoxTheme(
        countable = true,
        frameColor = greenColor,
        circleColor = whiteColo,
        circleFillColor = greenColor,
        fontSize = 14.sp,
        textColor = whiteColo,
    ),
    previewButtonTheme = PreviewButtonTheme(
        textBuilder = previewTextBuilder,
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = whiteColo,
        ),
    ),
    sureButtonTheme = SureButtonTheme(
        textBuilder = sureTextBuilder,
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = whiteColo,
        ),
        backgroundColor = greenColor,
    ),
    systemBarsTheme = SystemBarsTheme(
        statusBarColor = darkColor,
        statusBarDarkIcons = false,
        navigationBarColor = darkColor,
        navigationBarDarkIcons = false
    ),
)