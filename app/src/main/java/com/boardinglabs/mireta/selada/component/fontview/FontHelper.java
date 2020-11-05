package com.boardinglabs.mireta.selada.component.fontview;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Dhimas on 9/25/17.
 */

public class FontHelper {
    private static Typeface robotoBold;
    private static Typeface robotoBoldItalic;
    private static Typeface robotoItalic;
    private static Typeface robotoLight;
    private static Typeface robotoLightItalic;
    private static Typeface robotoRegular;

    public static Typeface getRobotoBold(Context ctx) {
        if (robotoBold == null) {
            robotoBold = Typeface.createFromAsset(ctx.getAssets(), "fonts/Roboto-Bold.ttf");
        }
        return robotoBold;
    }

    public static Typeface getRobotoBoldItalic(Context ctx) {
        if (robotoBoldItalic == null) {
            robotoBoldItalic = Typeface.createFromAsset(ctx.getAssets(), "fonts/Roboto-BoldItalic.ttf");
        }
        return robotoBoldItalic;
    }

    public static Typeface getRobotoItalic(Context ctx) {
        if (robotoItalic == null) {
            robotoItalic = Typeface.createFromAsset(ctx.getAssets(), "fonts/Roboto-Italic.ttf");
        }
        return robotoItalic;
    }

    public static Typeface getRobotoLight(Context ctx) {
        if (robotoLight == null) {
            robotoLight = Typeface.createFromAsset(ctx.getAssets(), "fonts/Roboto-Light.ttf");
        }
        return robotoLight;
    }

    public static Typeface getRobotoLightItalic(Context ctx) {
        if (robotoLightItalic == null) {
            robotoLightItalic = Typeface.createFromAsset(ctx.getAssets(), "fonts/Roboto-LightItalic.ttf");
        }
        return robotoLightItalic;
    }

    public static Typeface getRobotoRegular(Context ctx) {
        if (robotoRegular == null) {
            robotoRegular = Typeface.createFromAsset(ctx.getAssets(), "fonts/Roboto-Regular.ttf");
        }
        return robotoRegular;
    }
}
