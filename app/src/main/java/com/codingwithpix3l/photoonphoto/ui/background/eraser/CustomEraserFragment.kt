package com.codingwithpix3l.photoonphoto.ui.background.eraser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.codingwithpix3l.photoonphoto.R
import com.codingwithpix3l.photoonphoto.databinding.FragmentCustomEraserBinding
import com.codingwithpix3l.photoonphoto.core.view.EraserView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class CustomEraserFragment(var size :Int, var offset :Int, private val eraserView : EraserView, val onChange:(size:Int, offset:Int)->Unit) : BottomSheetDialogFragment(), SeekBar.OnSeekBarChangeListener {
    private var _binding : FragmentCustomEraserBinding? = null
    private val binding get() = _binding!!

  /*  private lateinit var onSingleChangeListener: OnSingleChangeListener

    fun singleChangeListener(onSingleChangeListener: OnSingleChangeListener) {
        this.onSingleChangeListener = onSingleChangeListener
    }*/

    private var newSize = 0
    private var newOffset = 100

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomEraserBinding.inflate(inflater,container,false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.customBrushSize.setOnSeekBarChangeListener(this)
        binding.customBrushSize.progress = size

        binding.customBrushOffset.setOnSeekBarChangeListener(this)
        binding.customBrushOffset.progress = offset

      /*  binding.cancelSizeEdit.setOnClickListener {
            eraserView.setEraseOffset((size*20) + 40)
            dismiss()
        }
        binding.doneSizeEdit.setOnClickListener {
            onSingleChangeListener.onDone(newSize)
            dismiss()
        }*/

    }


    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

        when(seekBar.id){
            R.id.customBrushSize->{
                newSize = progress
                eraserView.setEraseOffset((newSize*20) + 40)
            }
            R.id.customBrushOffset ->{
                newOffset = progress
                eraserView.changePointerDistance(progress)
            }
        }


    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }
    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        onChange(newSize,newOffset)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}