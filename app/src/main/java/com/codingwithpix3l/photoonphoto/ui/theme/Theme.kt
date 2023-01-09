package com.codingwithpix3l.photoonphoto.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Teal_night,
    primaryVariant = Teal200,
    onPrimary = Color.White,
    secondaryVariant = Color.Black,
    secondary = Dark_light_night,
    surface = Dark_night,
    onSecondary = Gray_neutral

)

/*private val LightColorPalette = lightColors(
    primary = Teal_day,
    primaryVariant = Teal200,
    onPrimary = Color.Black,
    secondaryVariant = Color.White,
    secondary = Dark_day,
    surface = Gray_day,
    onSecondary = Gray_neutral

    *//* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    *//*
)*/

@Composable
fun PhotoOnPhotoTheme(/*darkTheme: Boolean = isSystemInDarkTheme(),*/ content: @Composable () -> Unit) {
    /* val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }*/

    MaterialTheme(
        colors = DarkColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}