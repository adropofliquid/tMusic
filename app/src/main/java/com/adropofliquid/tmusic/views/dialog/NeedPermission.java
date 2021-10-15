package com.adropofliquid.tmusic.views.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.adropofliquid.tmusic.R;


public class NeedPermission extends DialogFragment{
    public static final String TAG = "Permission Dialog: ";

    public NeedPermission(){}//required empty costructor

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        LayoutInflater inflater =  requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.need_permission,null);


        Intent intent = new Intent();

        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + view.getContext().getPackageName()));


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setView(view);
        alertDialog.setTitle(R.string.permission);

        Dialog disDialog = alertDialog.create();
        view.findViewById(R.id.app_info).setOnClickListener(view1 -> {
            startActivity(intent);
        });
        view.findViewById(R.id.cancel_permission).setOnClickListener(view12 -> dismiss());
        return disDialog;
    }

}
