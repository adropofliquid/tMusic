package com.adropofliquid.tmusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.adropofliquid.tmusic.adapters.SongListAdapter;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_STORAGE = 1;
    private static final String TAG = "MainActivity: ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.my_library);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        checkStoragePermission();

    }

    private void checkStoragePermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG,"Permission granted");
            populateRecycler();
        }
        else if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            NeedPermission needPermission = new NeedPermission();
            needPermission.show(getSupportFragmentManager(),"need_permission");
        }
        else {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
        }

    }

    private void populateRecycler() {
        Log.d(TAG, "Recycler View is being populated");

        RecyclerView recyclerView;
        RecyclerView.Adapter adapter;
        RecyclerView.LayoutManager layoutManager;

        recyclerView = findViewById(R.id.song_list_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new SongListAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission granted");
            populateRecycler();
        } else {
            Log.d(TAG, "Permission not granted");
            NeedPermission needPermission = new NeedPermission();
            needPermission.show(getSupportFragmentManager(),"need_permission");
        }
        return;
    }

}