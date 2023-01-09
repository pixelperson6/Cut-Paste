package com.codingwithpix3l.photoonphoto.ui.main


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    // Initial value is false so the dialog is hidden
    private val _showDialog = mutableStateOf(false)
    val showDialog: State<Boolean> = _showDialog

    var initialState =false


    fun changeInitialState(){
        initialState = true
    }

    fun onOpenDialogClicked() {
        _showDialog.value = true
    }

    fun onDialogConfirm() {
        _showDialog.value = false
        // Continue with executing the confirmed action
    }

    fun onDialogDismiss() {
        _showDialog.value = false
    }

}
