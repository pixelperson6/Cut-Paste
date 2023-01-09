package com.codingwithpix3l.photoonphoto.core.util;

import com.codingwithpix3l.photoonphoto.ui.collage.layout.slant.OneSlantLayout;
import com.codingwithpix3l.photoonphoto.ui.collage.layout.slant.SlantLayoutHelper;
import com.codingwithpix3l.photoonphoto.ui.collage.layout.slant.ThreeSlantLayout;
import com.codingwithpix3l.photoonphoto.ui.collage.layout.slant.TwoSlantLayout;
import com.codingwithpix3l.photoonphoto.ui.collage.layout.straight.EightStraightLayout;
import com.codingwithpix3l.photoonphoto.ui.collage.layout.straight.FiveStraightLayout;
import com.codingwithpix3l.photoonphoto.ui.collage.layout.straight.FourStraightLayout;
import com.codingwithpix3l.photoonphoto.ui.collage.layout.straight.NineStraightLayout;
import com.codingwithpix3l.photoonphoto.ui.collage.layout.straight.OneStraightLayout;
import com.codingwithpix3l.photoonphoto.ui.collage.layout.straight.SevenStraightLayout;
import com.codingwithpix3l.photoonphoto.ui.collage.layout.straight.SixStraightLayout;
import com.codingwithpix3l.photoonphoto.ui.collage.layout.straight.StraightLayoutHelper;
import com.codingwithpix3l.photoonphoto.ui.collage.layout.straight.ThreeStraightLayout;
import com.codingwithpix3l.photoonphoto.ui.collage.layout.straight.TwoStraightLayout;
import com.codingwithpix3l.photoonphoto.core.view.puzzle.PuzzleLayout;
import java.util.ArrayList;
import java.util.List;


public class PuzzleUtils {
  private static final String TAG = "PuzzleUtils";

  private PuzzleUtils() {
    //no instance
  }

  public static PuzzleLayout getPuzzleLayout(int type, int borderSize, int themeId) {
    if (type == 0) {
      switch (borderSize) {
        case 1:
          return new OneSlantLayout(themeId);
        case 2:
          return new TwoSlantLayout(themeId);
        case 3:
          return new ThreeSlantLayout(themeId);
        default:
          return new OneSlantLayout(themeId);
      }
    } else {
      switch (borderSize) {
        case 1:
          return new OneStraightLayout(themeId);
        case 2:
          return new TwoStraightLayout(themeId);
        case 3:
          return new ThreeStraightLayout(themeId);
        case 4:
          return new FourStraightLayout(themeId);
        case 5:
          return new FiveStraightLayout(themeId);
        case 6:
          return new SixStraightLayout(themeId);
        case 7:
          return new SevenStraightLayout(themeId);
        case 8:
          return new EightStraightLayout(themeId);
        case 9:
          return new NineStraightLayout(themeId);
        default:
          return new OneStraightLayout(themeId);
      }
    }
  }

  public static List<PuzzleLayout> getAllPuzzleLayouts() {
    List<PuzzleLayout> puzzleLayouts = new ArrayList<>();
    //slant layout
    puzzleLayouts.addAll(SlantLayoutHelper.getAllThemeLayout(2));
    puzzleLayouts.addAll(SlantLayoutHelper.getAllThemeLayout(3));

    // straight layout
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(2));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(3));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(4));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(5));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(6));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(7));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(8));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(9));
    return puzzleLayouts;
  }

  public static List<PuzzleLayout> getPuzzleLayouts(int pieceCount) {
    List<PuzzleLayout> puzzleLayouts = new ArrayList<>();
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(pieceCount));
    puzzleLayouts.addAll(SlantLayoutHelper.getAllThemeLayout(pieceCount));
    return puzzleLayouts;
  }
}
