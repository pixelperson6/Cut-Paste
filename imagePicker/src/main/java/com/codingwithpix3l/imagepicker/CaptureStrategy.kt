package com.codingwithpix3l.imagepicker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.codingwithpix3l.imagepicker.internal.logic.MediaProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.math.max

interface CaptureStrategy {

    /**
     * 是否启用拍照功能
     */
    fun isEnabled(): Boolean

    /**
     * 是否需要申请读取存储卡的权限
     */
    fun shouldRequestWriteExternalStoragePermission(context: Context): Boolean

    /**
     * 获取用于存储拍照结果的 Uri
     */
    suspend fun createImageUri(context: Context): Uri?

    /**
     * 获取拍照结果
     */
    suspend fun loadResource(context: Context, imageUri: Uri): MediaResource?

    /**
     * 当用户取消拍照时调用
     */
    suspend fun onTakePictureCanceled(context: Context, imageUri: Uri)

    /**
     * 生成图片文件名
     */
    fun createImageName(): String {
        val uuid = UUID.randomUUID().toString()
        val randomName = uuid.substring(0, 8)
        return "$randomName.jpg"
    }

}

/**
 *  什么也不做，即不开启拍照功能
 */
object NothingCaptureStrategy : CaptureStrategy {

    override fun isEnabled(): Boolean {
        return false
    }

    override fun shouldRequestWriteExternalStoragePermission(context: Context): Boolean {
        return false
    }

    override suspend fun createImageUri(context: Context): Uri? {
        return null
    }

    override suspend fun loadResource(context: Context, imageUri: Uri): MediaResource? {
        return null
    }

    override suspend fun onTakePictureCanceled(context: Context, imageUri: Uri) {

    }

}

/**
 *  通过 FileProvider 来生成拍照所需要的 ImageUri
 *  无需申请权限，所拍的照片不会保存在系统相册里
 *  外部必须配置 FileProvider，并在此处传入 authority
 */
class FileProviderCaptureStrategy(private val authority: String) : CaptureStrategy {

    private val uriFileMap = mutableMapOf<Uri, File>()

    override fun isEnabled(): Boolean {
        return true
    }

    override fun shouldRequestWriteExternalStoragePermission(context: Context): Boolean {
        return false
    }

    override suspend fun createImageUri(context: Context): Uri? {
        return withContext(context = Dispatchers.IO) {
            try {
                val tempFile = createTempFile(context = context)
                if (tempFile != null) {
                    val uri = FileProvider.getUriForFile(context, authority, tempFile)
                    uriFileMap[uri] = tempFile
                    return@withContext uri
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return@withContext null
        }
    }

    private fun createTempFile(context: Context): File? {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(storageDir, createImageName())
        if (file.createNewFile()) {
            return file
        }
        return null
    }

    override suspend fun loadResource(context: Context, imageUri: Uri): MediaResource {
        return withContext(context = Dispatchers.IO) {
            val imageFile = uriFileMap[imageUri]!!
            uriFileMap.remove(imageUri)
            val imageFilePath = imageFile.absolutePath
            val option = BitmapFactory.Options()
            option.inJustDecodeBounds = true
            BitmapFactory.decodeFile(imageFilePath, option)
            return@withContext MediaResource(
                id = 0,
                uri = imageUri,
                displayName = imageFile.name,
                mimeType = option.outMimeType ?: "",
                width = max(option.outWidth, 0),
                height = max(option.outHeight, 0),
                orientation = 0,
                size = imageFile.length(),
                path = imageFile.absolutePath,
                bucketId = "",
                bucketDisplayName = ""
            )
        }
    }

    override suspend fun onTakePictureCanceled(context: Context, imageUri: Uri) {
        withContext(context = Dispatchers.IO) {
            val imageFile = uriFileMap[imageUri]!!
            uriFileMap.remove(imageUri)
            if (imageFile.exists()) {
                imageFile.delete()
            }
        }
    }

}

/**
 *  通过 MediaStore 来生成拍照所需要的 ImageUri
 *  根据系统版本决定是否需要申请 WRITE_EXTERNAL_STORAGE 权限
 *  所拍的照片会保存在系统相册里
 */
class MediaStoreCaptureStrategy : CaptureStrategy {

    override fun isEnabled(): Boolean {
        return true
    }

    override fun shouldRequestWriteExternalStoragePermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return false
        }
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_DENIED
    }

    override suspend fun createImageUri(context: Context): Uri? {
        return MediaProvider.createImage(context = context, fileName = createImageName())
    }

    override suspend fun loadResource(context: Context, imageUri: Uri): MediaResource? {
        return MediaProvider.loadResources(
            context = context,
            uri = imageUri
        )
    }

    override suspend fun onTakePictureCanceled(context: Context, imageUri: Uri) {
        MediaProvider.deleteImage(context = context, imageUri = imageUri)
    }

}