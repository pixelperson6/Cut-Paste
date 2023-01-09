package com.codingwithpix3l.photoonphoto.ui.collage.layout.straight;



import com.codingwithpix3l.photoonphoto.core.view.puzzle.straight.StraightPuzzleLayout;



public abstract class NumberStraightLayout extends StraightPuzzleLayout {
  static final String TAG = "NumberStraightLayout";
  protected int theme;

  public NumberStraightLayout(int theme) {
  /* Log.e(TAG, "NumberStraightLayout: the most theme count is "
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
