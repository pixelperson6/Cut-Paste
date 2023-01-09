package com.codingwithpix3l.photoonphoto.core.util

import android.content.*
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.codingwithpix3l.photoonphoto.model.PrivateImage
import com.codingwithpix3l.photoonphoto.core.util.Constants.PNG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.lang.Exception

object StorageHelper {
/*    private val galleryFolders: MutableMap<String, ArrayList<Image>> = HashMap()
    private val ownImage = ArrayList<Image>()
    private val collection = sdk29AndUp {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    private const val sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " DESC"
    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.WIDTH,
        MediaStore.Images.Media.HEIGHT,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    )*/

    private inline fun <T> sdk29AndUp(onSdk29: () -> T): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            onSdk29()
        } else null
    }

    suspend fun deletePrivatePhoto(filename: String, context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                context.deleteFile(filename)
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    }

    suspend fun savePhotoToExternalStorage(picType:Int,folderName:String,
        displayName: String,
        bmp: Bitmap,
        contentResolver: ContentResolver, context: Context
    ): Boolean {
        return withContext(Dispatchers.IO) {
            val fos =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                        put(MediaStore.Images.Media.WIDTH, bmp.width)
                        put(MediaStore.Images.Media.HEIGHT, bmp.height)
                        put(
                            MediaStore.MediaColumns.RELATIVE_PATH,
                            Environment.DIRECTORY_PICTURES + File.separator + folderName
                        )
                    }
                    val imageUri = contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    )
                    imageUri?.let { contentResolver.openOutputStream(it) }
                } else {
                    val imagesDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + File.separator + folderName)
                    if (!imagesDir.exists()) {
                        imagesDir.mkdir()
                    }
                    val image = File(imagesDir, displayName)
                    refreshGallery(image, context)
                    FileOutputStream(image)
                }

            try {
                fos.use {
                    if (picType == PNG){
                        if (!bmp.compress(Bitmap.CompressFormat.PNG, 95, it)) {
                            throw IOException("couldn't save bitmap ")
                        }
                    }else{
                        if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, it)) {
                            throw IOException("couldn't save bitmap ")
                        }
                    }

                }
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun refreshGallery(file: File, mContext: Context) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        mediaScanIntent.data = Uri.fromFile(file)
        mContext.sendBroadcast(mediaScanIntent)
    }


    suspend fun savePhotoToExternalStorage(
        displayName: String,
        bmp: Bitmap,
        contentResolver: ContentResolver
    ): Boolean {
        return withContext(Dispatchers.IO) {
            val imageCollection = sdk29AndUp {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpt")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.WIDTH, bmp.width)
                put(MediaStore.Images.Media.HEIGHT, bmp.height)
            }


            try {
                contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                    contentResolver.openOutputStream(uri).use { outputStream ->
                        if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                            throw IOException("couldn't save bitmap ")
                        }
                    }
                } ?: throw IOException("couldn't create MediaStore entry")

                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun deletePhotoFromOwnFolder(contentResolver: ContentResolver,photoUri: String):Boolean{
        return withContext(Dispatchers.IO){
            try {
                contentResolver.delete(Uri.parse(photoUri),null,null)
                true
            }catch (e:SecurityException){
                false
            }

        }
    }
/*
    private suspend fun loadPhotoFromExternalStorage(contentResolver: ContentResolver) {
        return withContext(Dispatchers.IO) {
            contentResolver.query(
                collection,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    val id =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                    val imgUri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                            .toString()
                    val imgName =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                    val imgWidth =
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH))
                    val imgHeight =
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT))
                    val folderName: String =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))

                    if (galleryFolders.containsKey(folderName)) {
                        galleryFolders[folderName]!!.add(
                            Image(
                                id,
                                imgName,
                                imgUri,
                                imgWidth,
                                imgHeight
                            )
                        )
                    } else {
                        val images: ArrayList<Image> = ArrayList()
                        images.add(Image(id, imgName, imgUri, imgWidth, imgHeight))
                        galleryFolders[folderName] = images
                    }
                }
            }
        }
    }

    private suspend fun loadPhotoFromOwnFolder(contentResolver: ContentResolver) {
        return withContext(Dispatchers.IO) {

            contentResolver.query(
                collection,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    val id =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                    val imgUri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                            .toString()
                    val imgName =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                    val imgWidth =
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH))
                    val imgHeight =
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT))
                    val folderName: String =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))

                    if (folderName == "PhotoOnPhoto" || folderName =="Eraser") {
                        ownImage.add(
                            Image(
                                id,
                                imgName,
                                imgUri,
                                imgWidth,
                                imgHeight
                            )
                        )
                    }
                }
            }
        }
    }

    suspend fun getGalleryFolders(contentResolver: ContentResolver): Map<String, ArrayList<Image>> {
        loadPhotoFromExternalStorage(contentResolver)
        return galleryFolders
    }

    suspend fun loadLibrary(contentResolver: ContentResolver):  ArrayList<Image> {
       loadPhotoFromOwnFolder(contentResolver)
        return ownImage
    }
*/
    suspend fun getPhotoFromInternalStorage(context: Context,name:String): PrivateImage {
        val list = withContext(Dispatchers.IO) {
            val files = context.filesDir.listFiles()
            files?.filter { it.canRead() && it.isFile && it.name.endsWith(".png") && it.name.equals("$name.png") }?.map {
                val byte = it.readBytes()
                val bmp = BitmapFactory.decodeByteArray(byte, 0, byte.size)
                PrivateImage(it.name, bmp)
            } ?: listOf()
        }
        return list[0]
    }

    suspend fun savePhotoToInternalStorage(
        filename: String,
        bmp: Bitmap,
        context: Context
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                context.openFileOutput("$filename.png", MODE_PRIVATE).use { stream ->
                    if (!bmp.compress(Bitmap.CompressFormat.PNG, 95, stream)) {
                        throw IOException("Couldn't save bitmap")
                    }
                }
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }
    }

/*
    fun clear(){
        galleryFolders.clear()
    }
    fun clearOwnImage(){
        ownImage.clear()
    }*/

}
