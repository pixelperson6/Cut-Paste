package com.codingwithpix3l.photoonphoto.ui.collage

import com.codingwithpix3l.photoonphoto.core.view.puzzle.PuzzleLayout
import android.graphics.Bitmap
import androidx.recyclerview.widget.RecyclerView
import com.codingwithpix3l.photoonphoto.ui.collage.PuzzleAdapter.PuzzleViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.codingwithpix3l.photoonphoto.R
import com.codingwithpix3l.photoonphoto.core.view.puzzle.SquarePuzzleView


class PuzzleAdapter(
    private val layoutData: List<PuzzleLayout>?,
    private val bitmapData: List<Bitmap>?,
    private val onItemClickListener: OnItemClickListener?
) : RecyclerView.Adapter<PuzzleViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PuzzleViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_puzzle, parent, false)
        return PuzzleViewHolder(itemView)
    }



    override fun onBindViewHolder(holder: PuzzleViewHolder, position: Int) {
        val puzzleLayout = layoutData!![position]

        holder.puzzleView.isNeedDrawLine = true
        holder.puzzleView.isNeedDrawOuterLine = true
        holder.puzzleView.isTouchEnable = false
        holder.puzzleView.puzzleLayout = puzzleLayout

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(
                layoutData[position]
            )
        }
        if (bitmapData == null) return
        val bitmapSize = bitmapData.size
        if (puzzleLayout.areaCount > bitmapSize) {
            for (i in 0 until puzzleLayout.areaCount) {
                holder.puzzleView.addPiece(bitmapData[i % bitmapSize])
            }
        } else {
            holder.puzzleView.addPieces(bitmapData)
        }

    }

    override fun getItemCount() =  layoutData?.size ?: 0

    /*  public void setData(List<PuzzleLayout> layoutData, List<Bitmap> bitmapData) {
    this.layoutData = layoutData;
    this.bitmapData = bitmapData;

   // notifyDataSetChanged();
  }

  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }*/

    class PuzzleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var puzzleView: SquarePuzzleView = itemView.findViewById(R.id.puzzle)
    }

    interface OnItemClickListener {
        fun onItemClick(puzzleLayout: PuzzleLayout?)
    }



}