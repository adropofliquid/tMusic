package com.adropofliquid.tmusic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public class NeedPermission extends DialogFragment{
    public static final String TAG = "Permission Dialog: ";

    public NeedPermission(){}//required empty costructor


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        LayoutInflater inflater =  requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.need_permission,null);




        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setView(view);
        alertDialog.setTitle(R.string.permission);

        Dialog disDialog = alertDialog.create();
        view.findViewById(R.id.app_info).setOnClickListener(view1 -> Log.d(TAG,"Link to Settings for Permission"));
        view.findViewById(R.id.cancel_permission).setOnClickListener(view12 -> disDialog.dismiss());
        return disDialog;
    }

}
