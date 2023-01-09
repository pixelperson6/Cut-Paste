package com.codingwithpix3l.photoonphoto.ui.background.changer.experiment.editorsdk

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.UiThread
import com.codingwithpix3l.imagepicker.MediaResource
import com.codingwithpix3l.photoonphoto.R
import com.codingwithpix3l.photoonphoto.core.util.BindingAdapter
import com.codingwithpix3l.photoonphoto.ui.background.changer.StickerImage
import com.codingwithpix3l.photoonphoto.ui.background.changer.experiment.editorsdk.MultiTouchListener.OnGestureControl
import com.codingwithpix3l.photoonphoto.ui.background.changer.experiment.editorsdk.PhotoEditorView

class PhotoEditor private constructor(builder: Builder) {
    private val mLayoutInflater: LayoutInflater
    private val context: Context
    val parentView: PhotoEditorView?
    private val imageView: ImageView
    private val deleteView: View?

    // private BrushDrawingView brushDrawingView;
    private val addedViews: MutableList<View>
    private val redoViews: MutableList<View>
    private var mOnPhotoEditorListener: OnPhotoEditorListener? = null
    private val isTextPinchZoomable: Boolean

    //   private Typeface mDefaultTextTypeface;
    //    private Typeface mDefaultEmojiTypeface;
    init {
        context = builder.context
        parentView = builder.parentView
        parentView.setOnClickListener{
            builder.onImageClicked()
        }
        imageView = builder.imageView
        deleteView = builder.deleteView
        //   this.brushDrawingView = builder.brushDrawingView;
        isTextPinchZoomable = builder.isTextPinchZoomable
        //   this.mDefaultTextTypeface = builder.textTypeface;
        //    this.mDefaultEmojiTypeface = builder.emojiTypeface;
        mLayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        //    brushDrawingView.setBrushViewChangeListener(this);
        addedViews = ArrayList()
        redoViews = ArrayList()
    }

    /**
     * This will add image on [PhotoEditorView] which you drag,rotate and scale using pinch
     * if [Builder.setPinchTextScalable] enabled
     *
     * @param desiredImage bitmap image you want to add
     */
    fun addImage(desiredImage: Bitmap?,onImageClicked: (imageView:ImageView) -> Unit ) {
        val imageRootView = mLayoutInflater.inflate(R.layout.view_photo_editor_image, null)
        val imageView = imageRootView.findViewById<ImageView>(R.id.imgPhotoEditorImage)
        val frmBorder = imageRootView.findViewById<FrameLayout>(R.id.frmBorder)
        // final ImageView imgClose = imageRootView.findViewById(R.id.imgPhotoEditorClose);
        imageView.setImageBitmap(desiredImage)

        val multiTouchListener = multiTouchListener
        multiTouchListener.setOnGestureControl(object : OnGestureControl {
            override fun onClick() {
                onImageClicked(imageView)
                val isBackgroundVisible = frmBorder.tag != null && frmBorder.tag as Boolean
                frmBorder.setBackgroundResource(if (isBackgroundVisible) 0 else R.drawable.rounded_border_tv)
                //  imgClose.setVisibility(isBackgroundVisible ? View.GONE : View.VISIBLE);
                frmBorder.tag = !isBackgroundVisible
            }

            override fun onLongClick() {}
        })
        imageRootView.setOnTouchListener(multiTouchListener)
        addViewToParent(imageRootView)
    }

    fun addImage(stickerImage: StickerImage,onImageClicked: (imageView:View,stickerImage: StickerImage) -> Unit ) {
        val desiredImage =
            stickerImage.bitmap ?: BindingAdapter.uriToBitmap(stickerImage.mediaResource.uri,context.contentResolver)
        val imageRootView = mLayoutInflater.inflate(R.layout.view_photo_editor_image, null)
        val imageView = imageRootView.findViewById<ImageView>(R.id.imgPhotoEditorImage)
        val frmBorder = imageRootView.findViewById<FrameLayout>(R.id.frmBorder)
        // final ImageView imgClose = imageRootView.findViewById(R.id.imgPhotoEditorClose);
        imageView.setImageBitmap(desiredImage)

        val multiTouchListener = multiTouchListener
        multiTouchListener.setOnGestureControl(object : OnGestureControl {
            override fun onClick() {

                onImageClicked(imageRootView,stickerImage)
                val isBackgroundVisible = frmBorder.tag != null && frmBorder.tag as Boolean
                frmBorder.setBackgroundResource(if (isBackgroundVisible) 0 else R.drawable.rounded_border_tv)
                //  imgClose.setVisibility(isBackgroundVisible ? View.GONE : View.VISIBLE);
                frmBorder.tag = !isBackgroundVisible
                imageRootView.bringToFront()
                imageRootView.invalidate()
                frameView?.bringToFront()
                frameView?.invalidate()
            }

            override fun onLongClick() {}
        })
        imageRootView.setOnTouchListener(multiTouchListener)
        addViewToParent(imageRootView)
    }
    var frameView: View? = null

    fun addFrame(frameBitmap: Bitmap?) {
        val imageFrameView = mLayoutInflater.inflate(R.layout.view_frame_image, null)
        imageFrameView.tag = ViewType.IMAGE
        val imageView = imageFrameView.findViewById<ImageView>(R.id.imgFrameView)
        imageView.setImageBitmap(frameBitmap)
        frameView = imageFrameView
        addViewToFrame(imageFrameView)
    }

    private fun addViewToFrame(rootView: View, viewType: ViewType=ViewType.IMAGE) {
        val frameSrcParam = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        frameSrcParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        parentView!!.addView(rootView, frameSrcParam)
        addedViews.add(rootView)
        if (mOnPhotoEditorListener != null) mOnPhotoEditorListener!!.onAddViewListener(
            viewType,
            addedViews.size
        )
    }

    /**
     * Add to root view from image,emoji and text to our parent view
     *
     * @param rootView rootview of image,text and emoji
     */
    private fun addViewToParent(rootView: View, viewType: ViewType =ViewType.IMAGE) {
        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        parentView!!.addView(rootView, params)
        addedViews.add(rootView)
        if (mOnPhotoEditorListener != null) mOnPhotoEditorListener!!.onAddViewListener(
            viewType,
            addedViews.size
        )
    }

    fun setMainSource(mainSource: Bitmap?) {
        parentView!!.source.setImageBitmap(mainSource)
    }//multiTouchListener.setOnMultiTouchListener(this);

    /**
     * Create a new instance and scalable touchview
     *
     * @return scalable multitouch listener
     */
    private val multiTouchListener: MultiTouchListener
        private get() =//multiTouchListener.setOnMultiTouchListener(this);
            MultiTouchListener(
                deleteView,
                parentView,
                imageView,
                isTextPinchZoomable,
                mOnPhotoEditorListener
            )

    private fun viewUndo(removedView: View, viewType: ViewType) {
        if (addedViews.size > 0) {
            if (addedViews.contains(removedView)) {
                parentView!!.removeView(removedView)
                addedViews.remove(removedView)
                redoViews.add(removedView)
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener!!.onRemoveViewListener(viewType, addedViews.size)
                }
            }
        }
    }
    /**
     * Undo the last operation perform on the [PhotoEditor]
     *
     * @return true if there nothing more to undo
     */
    /*    public boolean undo() {
        if (addedViews.size() > 0) {
            View removeView = addedViews.get(addedViews.size() - 1);
            if (removeView instanceof BrushDrawingView) {
                return brushDrawingView != null && brushDrawingView.undo();
            } else {
                addedViews.remove(addedViews.size() - 1);
                parentView.removeView(removeView);
                redoViews.add(removeView);
            }
            if (mOnPhotoEditorListener != null) {
                Object viewTag = removeView.getTag();
                if (viewTag != null && viewTag instanceof ViewType) {
                    mOnPhotoEditorListener.onRemoveViewListener(((ViewType) viewTag), addedViews.size());
                }
            }
        }
        return addedViews.size() != 0;
    }*/
    /**
     * Redo the last operation perform on the [PhotoEditor]
     *
     * @return true if there nothing more to redo
     */
    /*  public boolean redo() {
        if (redoViews.size() > 0) {
            View redoView = redoViews.get(redoViews.size() - 1);
            if (redoView instanceof BrushDrawingView) {
                return brushDrawingView != null && brushDrawingView.redo();
            } else {
                redoViews.remove(redoViews.size() - 1);
                parentView.addView(redoView);
                addedViews.add(redoView);
            }
            Object viewTag = redoView.getTag();
            if (mOnPhotoEditorListener != null && viewTag != null && viewTag instanceof ViewType) {
                mOnPhotoEditorListener.onAddViewListener(((ViewType) viewTag), addedViews.size());
            }
        }
        return redoViews.size() != 0;
    }

    private void clearBrushAllViews() {
        if (brushDrawingView != null)
            brushDrawingView.clearAll();
    }*/
    /**
     * Removes all the edited operations performed [PhotoEditorView]
     * This will also clear the undo and redo stack
     */
    fun clearAllViews() {
        for (i in addedViews.indices) {
            parentView!!.removeView(addedViews[i])
        }
        /*  if (addedViews.contains(brushDrawingView)) {
            parentView.addView(brushDrawingView);
        }*/addedViews.clear()
        redoViews.clear()
        frameView = null
        //  clearBrushAllViews();
    }
    fun clearFrameView() {
        parentView?.removeView(frameView)
        addedViews.remove(frameView)
        frameView = null
    }

    /**
     * Remove all helper boxes from views
     */
    @UiThread
    fun clearHelperBox() {
        for (i in 0 until parentView!!.childCount) {
            val childAt = parentView.getChildAt(i)
            val frmBorder = childAt.findViewById<FrameLayout>(R.id.frmBorder)
            frmBorder?.setBackgroundResource(0)
            /*  ImageView imgClose = childAt.findViewById(R.id.imgPhotoEditorClose);
            if (imgClose != null) {
                imgClose.setVisibility(View.GONE);
            }*/
        }
    }

    /**
     * A callback to save the edited image asynchronously
     */
    interface OnSaveListener {
        /**
         * Call when edited image is saved successfully on given path
         *
         * @param imagePath path on which image is saved
         */
        fun onSuccess(imagePath: String)

        /**
         * Call when failed to saved image on given path
         *
         * @param exception exception thrown while saving image
         */
        fun onFailure(exception: Exception)
    }

    /*  @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void saveAsFile(@NonNull final String imagePath, @NonNull final OnSaveListener onSaveListener) {
        saveAsFile(imagePath, new SaveSettings.Builder().build(), onSaveListener);
    }*/
    /*    @SuppressLint("StaticFieldLeak")
    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void saveAsFile(@NonNull final String imagePath,
                           @NonNull final SaveSettings saveSettings,
                           @NonNull final OnSaveListener onSaveListener) {
        Log.d(TAG, "Image Path: " + imagePath);
        parentView.saveBitmap(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {
                new AsyncTask<String, String, Exception>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        clearHelperBox();
                        parentView.setDrawingCacheEnabled(false);
                    }

                    @SuppressLint("MissingPermission")
                    @Override
                    protected Exception doInBackground(String... strings) {
                        // Create a media file name
                        File file = new File(imagePath);
                        try {
                            FileOutputStream out = new FileOutputStream(file, false);
                            if (parentView != null) {
                                parentView.setDrawingCacheEnabled(true);
                                Bitmap drawingCache = saveSettings.isTransparencyEnabled()
                                        ? BitmapUtil.removeTransparency(parentView.getDrawingCache())
                                        : parentView.getDrawingCache();
                                drawingCache.compress(saveSettings.getCompressFormat(), saveSettings.getCompressQuality(), out);
                            }
                            out.flush();
                            out.close();
                            Log.d(TAG, "Filed Saved Successfully");
                            return null;
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d(TAG, "Failed to save File");
                            return e;
                        }
                    }

                    @Override
                    protected void onPostExecute(Exception e) {
                        super.onPostExecute(e);
                        if (e == null) {
                            //Clear all views if its enabled in save settings
                            if (saveSettings.isClearViewsEnabled()) clearAllViews();
                            onSaveListener.onSuccess(imagePath);
                        } else {
                            onSaveListener.onFailure(e);
                        }
                    }

                }.execute();
            }

            @Override
            public void onFailure(Exception e) {
                onSaveListener.onFailure(e);
            }
        });
    }*/
    /*   @SuppressLint("StaticFieldLeak")
    public void saveAsBitmap(@NonNull final OnSaveBitmap onSaveBitmap) {
        saveAsBitmap(new SaveSettings.Builder().build(), onSaveBitmap);
    }*/
    /*

    @SuppressLint("StaticFieldLeak")
    public void saveAsBitmap(@NonNull final SaveSettings saveSettings,
                             @NonNull final OnSaveBitmap onSaveBitmap) {
        parentView.saveBitmap(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {
                new AsyncTask<String, String, Bitmap>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        clearHelperBox();
                        parentView.setDrawingCacheEnabled(false);
                    }

                    @Override
                    protected Bitmap doInBackground(String... strings) {
                        if (parentView != null) {
                            parentView.setDrawingCacheEnabled(true);
                            return saveSettings.isTransparencyEnabled() ?
                                    BitmapUtil.removeTransparency(parentView.getDrawingCache())
                                    : parentView.getDrawingCache();
                        } else {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        if (bitmap != null) {
                            if (saveSettings.isClearViewsEnabled()) clearAllViews();
                            onSaveBitmap.onBitmapReady(bitmap);
                        } else {
                            onSaveBitmap.onFailure(new Exception("Failed to load the bitmap"));
                        }
                    }

                }.execute();
            }

            @Override
            public void onFailure(Exception e) {
                onSaveBitmap.onFailure(e);
            }
        });
    }*/
    fun saveBitmap(): Bitmap? {
        clearHelperBox()
        if (parentView != null) {
            parentView.isDrawingCacheEnabled = true
            return if (SaveSettings.Builder()
                    .build().isTransparencyEnabled
            ) BitmapUtil.removeTransparency(parentView.drawingCache) else parentView.drawingCache
        }
        return null
    }



    /**
     * Callback on editing operation perform on [PhotoEditorView]
     *
     * @param onPhotoEditorListener [OnPhotoEditorListener]
     */
    fun setOnPhotoEditorListener(onPhotoEditorListener: OnPhotoEditorListener) {
        mOnPhotoEditorListener = onPhotoEditorListener
    }

    /**
     * Check if any changes made need to save
     *
     * @return true if nothing is there to change
     */
    val isCacheEmpty: Boolean
        get() = addedViews.size == 0 && redoViews.size == 0

    /**
     * Builder pattern to define [PhotoEditor] Instance
     */
    class Builder(val context: Context, val parentView: PhotoEditorView,val onImageClicked: () -> Unit) {
       val imageView: ImageView = parentView.source
       var deleteView: View? = null

        //   private BrushDrawingView brushDrawingView;
        //    private Typeface textTypeface;
        //    private Typeface emojiTypeface;
        //By Default pinch zoom on text is enabled
        var isTextPinchZoomable = true

        /**
         * Building a PhotoEditor which requires a Context and PhotoEditorView
         * which we have setup in our xml layout
         *
         * @param context         context
         * @param photoEditorView [PhotoEditorView]
         */
        init {
            // brushDrawingView = photoEditorView.getBrushDrawingView();
        }

        fun setDeleteView(deleteView: View?): Builder {
            this.deleteView = deleteView
            return this
        }

        /**
         * set false to disable pinch to zoom on text insertion.By deafult its true
         *
         * @param isTextPinchZoomable flag to make pinch to zoom
         * @return [Builder] instant to build [PhotoEditor]
         */
        fun setPinchTextScalable(isTextPinchZoomable: Boolean): Builder {
            this.isTextPinchZoomable = isTextPinchZoomable
            return this
        }


        /**
         * @return build PhotoEditor instance
         */
        fun build(): PhotoEditor {
            return PhotoEditor(this)
        }
    }

    companion object {
        private const val TAG = "PhotoEditor"
    }
}