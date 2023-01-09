package com.codingwithpix3l.photoonphoto.core.view.puzzle;

import android.content.Context;
import android.util.AttributeSet;


public class SquarePuzzleView extends PuzzleView {
  public SquarePuzzleView(Context context) {
    super(context);
  }

  public SquarePuzzleView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SquarePuzzleView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    int width = getMeasuredWidth();
    int height = getMeasuredHeight();
    int length = Math.min(width, height);

    setMeasuredDimension(length, length);
  }
}
