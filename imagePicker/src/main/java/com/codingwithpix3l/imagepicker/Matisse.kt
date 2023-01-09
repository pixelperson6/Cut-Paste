package com.codingwithpix3l.imagepicker

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


data class Matisse(
    val theme: MatisseTheme = LightMatisseTheme,
    val supportedMimeTypes: List<MimeType> = ofImage(hasGif = true),
    val maxSelectable: Int = 1,
    val spanCount: Int = 4,
    val tips: MatisseTips = defaultMatisseTips,
    val captureStrategy: CaptureStrategy = NothingCaptureStrategy,
    val saveDFolderName:String = ""
) {

    companion object {

        fun ofImage(hasGif: Boolean = true): List<MimeType> {
            return if (hasGif) {
                listOf(*MimeType.values())
            } else {
                mutableListOf(*MimeType.values()).apply {
                    remove(MimeType.GIF)
                }
            }
        }

    }

}

@Parcelize
data class MediaResource(
    private val id: Long,
    val uri: Uri,
    val displayName: String,
    val mimeType: String,
    val width: Int,
    val height: Int,
    val orientation: Int,
    val size: Long,
    val path: String,
    val bucketId: String,
    val bucketDisplayName: String
) : Parcelable {

    @IgnoredOnParcel
    internal val key = id

}

enum class MimeType(val type: String) {
    JPEG("image/jpeg"),
    PNG("image/png"),
    HEIC("image/heic"),
    HEIF("image/heif"),
    BMP("image/x-ms-bmp"),
    WEBP("image/webp"),
    GIF("image/gif");
}

data class MatisseTips(
    val onReadExternalStorageDenied: String,
    val onWriteExternalStorageDenied: String,
    val onCameraDenied: String,
    val onSelectLimit: (selectedSize: Int, maxSelectable: Int) -> String,
)

private val defaultMatisseTips = MatisseTips(onReadExternalStorageDenied = "Please grant storage access and try again",
    onWriteExternalStorageDenied = "Please grant storage write permission and try again",
    onCameraDenied = "Please grant permission to take pictures and try again",
    onSelectLimit = { _: Int, maxSelectable: Int ->
        "Select at most ${maxSelectable}pictures"
    }
)