package com.adropofliquid.tmusic.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.adropofliquid.tmusic.views.dialog.NeedPermission;

public class SystemPermissions {

    private final FragmentManager fragmentManager;
    private final Activity activity;
    public static final int REQUEST_STORAGE = 1;

    public SystemPermissions(Activity activity, FragmentManager fragmentManager) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
    }

    public boolean storageIsPermitted(){
        boolean permit = false;
        if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            permit = true;
        }
        return permit;
    }

    public void displayRequestPermission(){
        requestPermission();
        displayPermissionRationale();
    }
    public void displayPermissionRationale() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(
                activity,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            NeedPermission needPermission = new NeedPermission();
            needPermission.show(fragmentManager,"need_permission");
        }
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(
                activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
    }
}
