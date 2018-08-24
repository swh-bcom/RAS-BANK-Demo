package com.example.ras.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class AppThemeUtil {

    private static final String ACTION_BAR_COLOR = "ACTION_BAR_COLOR";

    private static final String ACTION_BAR_TITLE_COLOR = "ACTION_BAR_TITLE_COLOR";

    public static final String THEME_PREFS_NAME = "ThemePrefsFile";
    private static final String ACTION_BAR_COLOR_PICKER = "ACTION_BAR_COLOR_PICKER";

    private static final String ACTION_BAR_TITLE_COLOR_PICKER = "ACTION_BAR_TITLE_COLOR_PICKER";

    private static final String TEXT_COLOR = "TEXT_COLOR";

    private static final String BUTTON_COLOR = "BUTTON_COLOR";

    private static final String BUTTON_TEXT_COLOR = "BUTTON_TEXT_COLOR";

    private static final String APP_NAME = "APP_NAME";

    private static final String LOGO_ICON = "LOGO_ICON";

    private static final String BACKGROUND_IMAGE = "BACKGROUND_IMAGE";


    public static void initAppConstants (Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(THEME_PREFS_NAME, Context.MODE_PRIVATE);
        AppThemeConstants.ACTION_BAR_COLOR = sharedPref.getInt(ACTION_BAR_COLOR, 0);
        AppThemeConstants.ACTION_BAR_TITLE_COLOR = sharedPref.getInt(ACTION_BAR_TITLE_COLOR, 0);
        AppThemeConstants.ACTION_BAR_COLOR_PICKER = sharedPref.getInt(ACTION_BAR_COLOR_PICKER, 0);
        AppThemeConstants.ACTION_BAR_TITLE_COLOR_PICKER = sharedPref.getInt(ACTION_BAR_TITLE_COLOR_PICKER, 0);
        AppThemeConstants.TEXT_COLOR = sharedPref.getInt(TEXT_COLOR, 0);
        AppThemeConstants.BUTTON_COLOR = sharedPref.getInt(BUTTON_COLOR, 0);
        AppThemeConstants.BUTTON_TEXT_COLOR = sharedPref.getInt(BUTTON_TEXT_COLOR, 0);
        AppThemeConstants.APP_NAME = sharedPref.getString(APP_NAME, "");
        AppThemeConstants.LOGO_ICON = sharedPref.getString(LOGO_ICON, "");
        AppThemeConstants.BACKGROUND_IMAGE = sharedPref.getString(BACKGROUND_IMAGE, "");

    }

    public static void updateAppConstants (Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(THEME_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(ACTION_BAR_COLOR, AppThemeConstants.ACTION_BAR_COLOR);
        editor.putInt(ACTION_BAR_TITLE_COLOR, AppThemeConstants.ACTION_BAR_TITLE_COLOR);
        editor.putInt(ACTION_BAR_COLOR_PICKER, AppThemeConstants.ACTION_BAR_COLOR_PICKER);
        editor.putInt(ACTION_BAR_TITLE_COLOR_PICKER, AppThemeConstants.ACTION_BAR_TITLE_COLOR_PICKER);
        editor.putInt(TEXT_COLOR, AppThemeConstants.TEXT_COLOR);
        editor.putInt(BUTTON_COLOR,AppThemeConstants.BUTTON_COLOR);
        editor.putInt(BUTTON_TEXT_COLOR,AppThemeConstants.BUTTON_TEXT_COLOR);
        editor.putString(APP_NAME,AppThemeConstants.APP_NAME);
        editor.putString(LOGO_ICON,AppThemeConstants.LOGO_ICON);
        editor.putString(BACKGROUND_IMAGE,AppThemeConstants.BACKGROUND_IMAGE);
      // activity.getResources().setString(R.color.colorPrimary)
        editor.commit();
    }





}
