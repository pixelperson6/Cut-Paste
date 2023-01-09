package com.codingwithpix3l.photoonphoto.core.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable

@Composable
fun CustomAlertDialog(
    title:String,
    text:String,
    positiveButton:String,
    negativeButton:String,
    positiveAction:() ->Unit,
    negativeAction:() ->Unit,
    show:Boolean
){
    if (show)
    {
        AlertDialog(
            onDismissRequest =  negativeAction ,
            title = { Text(text = title, color = MaterialTheme.colors.onPrimary) },
            text = { Text(text = text, color = MaterialTheme.colors.onPrimary) },

            confirmButton = {
                TextButton(onClick = positiveAction) {
                    Text(text = positiveButton,color = MaterialTheme.colors.onPrimary)
                }

            },
            dismissButton = {
                TextButton(onClick = negativeAction) {
                    Text(text = negativeButton,color = MaterialTheme.colors.onPrimary)
                }
            },
        )
    }
}