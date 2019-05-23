package com.example.dell.mygitdemo;

import android.app.Activity;
import android.os.Build;
import android.view.DisplayCutout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public  class HasNotchInScreen {
    /**
     * 是否有刘海屏
     *
     * @return
     */
    public static boolean hasNotchInScreen(Activity activity) {

        // android  P 以上有标准 API 来判断是否有刘海屏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            DisplayCutout displayCutout = activity.getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
            if (displayCutout != null) {
                // 说明有刘海屏
                return true;
            }
        } else {
            // 通过其他方式判断是否有刘海屏  目前官方提供有开发文档的就 小米，vivo，华为（荣耀），oppo
            String manufacturer = Build.MANUFACTURER;

            if (manufacturer.isEmpty()) {
                return false;
            } else if (manufacturer.equalsIgnoreCase("HUAWEI")) {
                return hasNotchHw(activity);
            } else if (manufacturer.equalsIgnoreCase("xiaomi")) {
                return hasNotchXiaoMi(activity);
            } else if (manufacturer.equalsIgnoreCase("oppo")) {
                return hasNotchOPPO(activity);
            } else if (manufacturer.equalsIgnoreCase("vivo")) {
                return hasNotchVIVO(activity);
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 判断vivo是否有刘海屏
     * https://swsdl.vivo.com.cn/appstore/developer/uploadfile/20180328/20180328152252602.pdf
     *
     * @param activity
     * @return
     */
    private static boolean hasNotchVIVO(Activity activity) {
        try {
            Class<?> c = Class.forName("android.util.FtFeature");
            Method get = c.getMethod("isFeatureSupport", int.class);
            return (boolean) (get.invoke(c, 0x20));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断oppo是否有刘海屏
     * https://open.oppomobile.com/wiki/doc#id=10159
     *
     * @param activity
     * @return
     */
    private static boolean hasNotchOPPO(Activity activity) {
        return activity.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    /**
     * 判断xiaomi是否有刘海屏
     * https://dev.mi.com/console/doc/detail?pId=1293
     *
     * @param activity
     * @return
     */
    private static boolean hasNotchXiaoMi(Activity activity) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("getInt", String.class, int.class);
            return (int) (get.invoke(c, "ro.miui.notch", 0)) == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断华为是否有刘海屏
     * https://devcenter-test.huawei.com/consumer/cn/devservice/doc/50114
     *
     * @param activity
     * @return
     */
    private static boolean hasNotchHw(Activity activity) {

        try {
            ClassLoader cl = activity.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            return (boolean) get.invoke(HwNotchSizeUtil);
        } catch (Exception e) {
            return false;
        }
    }



    /**
     * 小米刘海屏判断.
     * @return 0 if it is not notch ; return 1 means notch
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static int getInt(String key,Activity activity) {
        int result = 0;
        if (isXiaomi()){
            try {
                ClassLoader classLoader = activity.getClassLoader();
                @SuppressWarnings("rawtypes")
                Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
                //参数类型
                @SuppressWarnings("rawtypes")
                Class[] paramTypes = new Class[2];
                paramTypes[0] = String.class;
                paramTypes[1] = int.class;
                Method getInt = SystemProperties.getMethod("getInt", paramTypes);
                //参数
                Object[] params = new Object[2];
                params[0] = new String(key);
                params[1] = new Integer(0);
                result = (Integer) getInt.invoke(SystemProperties, params);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return result;
    }



    // 是否是小米手机
    public static boolean isXiaomi() {
        return "Xiaomi".equals(Build.MANUFACTURER);
    }

    }
