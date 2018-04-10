package com.example.ilan.myfinalproject.Extra;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

// check for the required device related permissions
public class PermissionManager
{

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(Activity activity, String permission, int requestCode )
    {
    boolean hasPermission=hasPermission(activity, permission);

    if(!hasPermission && !isPermissionDenied(activity, permission) )
        {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }

    return hasPermission;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean requestPermission(Activity activity, String permission, int requestCode )
    {
    boolean hasPermission=hasPermission(activity, permission);

    if(!hasPermission)
        {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }

    return hasPermission;
    }


    public static boolean hasPermission(Activity activity, String permission )
    {
    return (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || ( ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED);
    }


    private static boolean isPermissionDenied(Activity activity, String permission )
    {
    return (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || ( ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED);
    }

}
