package com.codingwithpix3l.imagepicker.internal.logic

import android.app.Activity
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.codingwithpix3l.imagepicker.Matisse
import com.codingwithpix3l.imagepicker.MediaResource
import com.codingwithpix3l.imagepicker.R
import com.codingwithpix3l.imagepicker.internal.logic.MediaProvider.deleteImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class MatisseViewModel(application: Application, private val matisse: Matisse) :
    AndroidViewModel(application) {

    companion object {

        private const val DEFAULT_BUCKET_ID = "&__defaultBucketId__&"

    }

    private val _selectedMediaResources = mutableListOf<MediaResource>()

    val selectedMediaResources: List<MediaResource> = _selectedMediaResources

    private val permissionRequestingViewState = kotlin.run {
        val defaultBucket = MediaBucket(
            bucketId = DEFAULT_BUCKET_ID,
            bucketDisplayName = matisse.theme.topAppBarTheme.defaultBucketName,
            bucketDisplayIcon = Uri.EMPTY,
            resources = emptyList(),
            supportCapture = matisse.captureStrategy.isEnabled()
        )
        MatisseViewState(
            matisse = matisse,
            bottomBarViewState = buildBottomBarViewState(),
            lazyGridState = LazyGridState(
                firstVisibleItemIndex = 0,
                firstVisibleItemScrollOffset = 0
            ),
            state = MatisseState.PermissionRequesting,
            selectedResources = selectedMediaResources,
            allBucket = listOf(defaultBucket),
            selectedBucket = defaultBucket,
            onClickMedia = ::onClickMedia,
            onSelectBucket = ::onSelectBucket,
            onMediaCheckChanged = ::onMediaCheckChanged,
        )
    }

    private val permissionDeniedViewState =
        permissionRequestingViewState.copy(state = MatisseState.PermissionDenied)

    private val imageLoadingViewState =
        permissionRequestingViewState.copy(state = MatisseState.ImagesLoading)

    private val imageEmptyViewState =
        permissionRequestingViewState.copy(state = MatisseState.ImagesEmpty)

    private val _matisseViewState = MutableStateFlow(permissionRequestingViewState)

    val matisseViewState: StateFlow<MatisseViewState> = _matisseViewState

    private val _matissePreviewViewState = MutableStateFlow(
        MatissePreviewViewState(
            matisse = matisse,
            visible = false,
            initialPage = 0,
            selectedResources = emptyList(),
            previewResources = emptyList(),
            onMediaCheckChanged = ::onMediaCheckChanged,
            onDismissRequest = ::onDismissPreviewPageRequest,
            onDeleteFile = ::deleteImageFromOwnFile,
            onShareFile = ::shareImage
        )
    )

    val matissePreviewViewState: StateFlow<MatissePreviewViewState> = _matissePreviewViewState

    fun onRequestReadExternalStoragePermission() {
        viewModelScope.launch {
            _matisseViewState.emit(permissionRequestingViewState)
        }
    }

    fun onRequestReadExternalStoragePermissionResult(granted: Boolean) {
        viewModelScope.launch {
            if (granted) {
                _matisseViewState.emit(imageLoadingViewState)
                loadResources()
            } else {
                _matisseViewState.emit(permissionDeniedViewState)
                val tips = matisse.tips.onReadExternalStorageDenied
                if (tips.isNotBlank()) {
                    Toast.makeText(getApplication(), tips, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun loadResources() {
        val supportedMimeTypes = matisse.supportedMimeTypes
        if (supportedMimeTypes.isEmpty()) {
            _matisseViewState.emit(imageEmptyViewState)
        } else {
            val allResources = MediaProvider.loadResources(
                context = getApplication(),
                filterMimeTypes = matisse.supportedMimeTypes
            )
            if (allResources.isEmpty()) {
                _matisseViewState.emit(imageEmptyViewState)
            } else {
                val allBucket =
                    MediaProvider.groupByBucket(resources = allResources).toMutableList()
                val defaultBucket = MediaBucket(
                    bucketId = DEFAULT_BUCKET_ID,
                    bucketDisplayName = matisse.theme.topAppBarTheme.defaultBucketName,
                    bucketDisplayIcon = allResources[0].uri,
                    resources = allResources,
                    supportCapture = matisse.captureStrategy.isEnabled()
                )
                allBucket.add(0, defaultBucket)
                _matisseViewState.emit(
                    matisseViewState.value.copy(
                        state = MatisseState.ImagesLoaded,
                        allBucket = allBucket,
                        selectedBucket = defaultBucket,
                        selectedResources = selectedMediaResources,
                        bottomBarViewState = buildBottomBarViewState()
                    )
                )
            }
        }
    }

    fun findMediaBucket(name: String){
        var found = false
        for (bucket in matisseViewState.value.allBucket){
            if (bucket.bucketDisplayName == name){
                found = true
                viewModelScope.launch {
                    _matisseViewState.emit(
                        matisseViewState.value.copy(
                            selectedBucket = bucket,
                        )
                    )
                }
                break
            }
        }
        if (!found){
            viewModelScope.launch {
                _matisseViewState.emit(imageEmptyViewState)
            }
        }
    }

    private fun onSelectBucket(bucket: MediaBucket) {
        viewModelScope.launch {
            _matisseViewState.emit(
                matisseViewState.value.copy(
                    selectedBucket = bucket,
                    lazyGridState = LazyGridState(
                        firstVisibleItemIndex = 0,
                        firstVisibleItemScrollOffset = 0
                    ),
                )
            )
        }
    }

    private fun onMediaCheckChanged(mediaResource: MediaResource) {
        if (_selectedMediaResources.size >= matisse.maxSelectable && !_selectedMediaResources.contains(
                mediaResource
            )
        ) {
            val tips =
                matisse.tips.onSelectLimit(_selectedMediaResources.size, matisse.maxSelectable)
            if (tips.isNotBlank()) {
                Toast.makeText(getApplication(), tips, Toast.LENGTH_SHORT).show()
            }
            return
        }
        viewModelScope.launch {
            val contains = _selectedMediaResources.contains(mediaResource)
            if (contains) {
                _selectedMediaResources.remove(mediaResource)
            } else {
                _selectedMediaResources.add(mediaResource)
            }
            val viewState = matisseViewState.value
            _matisseViewState.emit(
                viewState.copy(
                    selectedResources = _selectedMediaResources,
                    bottomBarViewState = buildBottomBarViewState()
                )
            )
            val previewViewState = matissePreviewViewState.value
            if (previewViewState.visible) {
                _matissePreviewViewState.emit(
                    value = previewViewState.copy(
                        selectedResources = _selectedMediaResources,
                    )
                )
            }
        }
    }

    private fun onClickMedia(mediaResource: MediaResource) {
        viewModelScope.launch {
            val viewState = matisseViewState.value
            val previewResources = viewState.selectedBucket.resources
            val selectedResources = viewState.selectedResources
            val initialPage = previewResources.indexOf(mediaResource)
            _matissePreviewViewState.emit(
                value = matissePreviewViewState.value.copy(
                    visible = true,
                    initialPage = initialPage,
                    selectedResources = selectedResources,
                    previewResources = previewResources,
                )
            )
        }
    }

    private fun previewSelectedMedia() {
        viewModelScope.launch {
            val selectedResources = matisseViewState.value.selectedResources.toList()
            if (selectedResources.isEmpty()) {
                return@launch
            }
            _matissePreviewViewState.emit(
                value = matissePreviewViewState.value.copy(
                    visible = true,
                    initialPage = 0,
                    selectedResources = selectedResources,
                    previewResources = selectedResources,
                )
            )
        }
    }



    private fun onDismissPreviewPageRequest() {
        viewModelScope.launch {
            _matissePreviewViewState.emit(
                value = matissePreviewViewState.value.copy(
                    visible = false
                )
            )
        }
    }

    private fun deleteImageFromOwnFile(context: Context,mediaResource: MediaResource){
        viewModelScope.launch {
            deleteImage(context,mediaResource.uri)
        }
    }

    fun shareImage(activity:Activity,mediaResource: MediaResource){
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM,mediaResource.uri)
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        try{
            activity.startActivity(Intent.createChooser(shareIntent,"Share image"))
        } catch(e: ActivityNotFoundException) {
            activity.showToast(R.string.toast_no_share_found)
        }
    }

    fun Activity.showToast(
        stringId: Int,
        duration: Int = Toast.LENGTH_SHORT
    ) = Toast.makeText(this, stringId, duration).show()

    fun Activity.showToast(
        text: String,
        duration: Int = Toast.LENGTH_SHORT
    ) = Toast.makeText(this, text, duration).show()

    private fun buildBottomBarViewState(): MatisseBottomBarViewState {
        val selectedMedia = _selectedMediaResources
        val selectedMediaSize = selectedMedia.size
        val theme = matisse.theme
        return MatisseBottomBarViewState(
            previewText = theme.previewButtonTheme.textBuilder(
                selectedMediaSize,
                matisse.maxSelectable
            ),
            sureText = theme.sureButtonTheme.textBuilder(selectedMediaSize, matisse.maxSelectable),
            previewButtonClickable = selectedMedia.isNotEmpty(),
            sureButtonClickable = selectedMedia.isNotEmpty(),
            onPreviewButtonClick = ::previewSelectedMedia,
        )
    }

}