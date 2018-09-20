package com.zzl.demo.statusbar.statusbar.impl;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import com.zzl.demo.statusbar.statusbar.IStatusBarFontHelper;
import com.zzl.demo.statusbar.statusbar.OSUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * <pre>
 *     author: Zou Juequn
 *     email:15695947865@139.com
 *     desc:小米机型
 */
public class MIUIHelper implements IStatusBarFontHelper {
    /**
     * 设置状态栏字体图标为深色，需要MIUI6以上
     *
     * @param isFontColorDark 是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    @SuppressLint("PrivateApi")
    @Override
    public boolean setStatusBarLightMode(Activity activity, boolean isFontColorDark) {
        Window window = activity.getWindow();
        boolean result = false;
        if (window != null && getIsMiuiV6() && OSUtils.getRomType() == OSUtils.ROM_TYPE.MIUI) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag;
                int flag;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (isFontColorDark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {//设置白色字体
                        flag = window.getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                        window.getDecorView().setSystemUiVisibility(flag);
                    }
                }
                result = true;
            } catch (Exception e) {


            }
        }
        return result;
    }

    @SuppressLint("PrivateApi")
    private boolean getIsMiuiV6() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class<?> sysClass = Class.forName("android.os.SystemProperties");
                Method getStringMethod = sysClass.getDeclaredMethod("get", String.class);
                String invoke = (String) getStringMethod.invoke(sysClass, "ro.miui.ui.version.name");
                if (invoke.contains("V")) {
                    String[] vs = invoke.split("V");
                    if (vs.length > 1) {
                        return Integer.parseInt(vs[1]) >= 6;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
