package com.codingwithpix3l.photoonphoto.ui.collage.layout.slant;


import com.codingwithpix3l.photoonphoto.core.view.puzzle.slant.SlantPuzzleLayout;



public abstract class NumberSlantLayout extends SlantPuzzleLayout {

  static final String TAG = "NumberSlantLayout";
  protected int theme;

  public NumberSlantLayout(int theme) {
   /* if (theme >= getThemeCount()) {
      Log.e(TAG, "NumberSlantLayout: the most theme count is "
          + getThemeCount()
          + " ,you should let theme from 0 to "
          + (getThemeCount() - 1)
          + " .");
    }*/
    this.theme = theme;
  }

  public abstract int getThemeCount();

  public int getTheme() {
    return theme;
  }
}
