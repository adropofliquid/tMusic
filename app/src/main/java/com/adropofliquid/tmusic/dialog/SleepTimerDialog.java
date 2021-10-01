package com.adropofliquid.tmusic.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.sleeptimer.SleepTimer;

public class SleepTimerDialog extends DialogFragment {

    private MediaControllerCompat mediaController;

    public SleepTimerDialog(){}

    public SleepTimerDialog(MediaControllerCompat mediaController){
        this.mediaController = mediaController;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String[] timesText = {"2 minutes", "5 minutes", "10 minutes", "15 minutes", "30 minutes"};
        final int[] choice = {2,5,10,15,30};
        final int[] actualChoice = {0};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Sleep Timer");

        builder.setSingleChoiceItems(timesText, 1, (dialog, which) -> actualChoice[0] = choice[which]);

        builder.setPositiveButton("OK", (dialog, which) -> {
            new SleepTimer(actualChoice[0], mediaController);
            Toast.makeText(getActivity(),"Song will stop in "+ actualChoice[0]+" minutes", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());

        return builder.create();
    }

}
