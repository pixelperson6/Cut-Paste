package com.codingwithpix3l.imagepicker.internal.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.codingwithpix3l.imagepicker.internal.theme.LocalMatisseTheme


@Composable
internal fun SetSystemUi(previewVisible: Boolean) {
    if (previewVisible) {
        SetSystemUi(
            statusBarColor = Color.Transparent,
            navigationBarColor = Color.Transparent,
            statusBarColorDarkIcons = false,
            navigationBarColorDarkIcons = false
        )
    } else {
        val systemBarsTheme = LocalMatisseTheme.current.systemBarsTheme
        SetSystemUi(
            statusBarColor = systemBarsTheme.statusBarColor,
            navigationBarColor = systemBarsTheme.navigationBarColor,
            statusBarColorDarkIcons = systemBarsTheme.statusBarDarkIcons,
            navigationBarColorDarkIcons = systemBarsTheme.navigationBarDarkIcons
        )
    }
}

@Composable
internal fun SetSystemUi(
    statusBarColor: Color = Color.Transparent,
    navigationBarColor: Color = Color.Transparent,
    statusBarColorDarkIcons: Boolean = true,
    navigationBarColorDarkIcons: Boolean = true
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(
        color = statusBarColor,
        darkIcons = statusBarColorDarkIcons
    )
    systemUiController.setNavigationBarColor(
        color = navigationBarColor,
        darkIcons = navigationBarColorDarkIcons
    )
}