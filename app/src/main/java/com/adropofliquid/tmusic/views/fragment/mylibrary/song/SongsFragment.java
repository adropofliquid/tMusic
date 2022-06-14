package com.adropofliquid.tmusic.views.fragment.mylibrary.song;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.mediastore.LoadMediaData;


public class SongsFragment extends Fragment {


    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_songs, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        populateRecycler(view);
    }

    private void populateRecycler(View view) {

        RecyclerView.LayoutManager layoutManager;
        recyclerView = view.findViewById(R.id.song_list_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        new LoadMediaData(getActivity(),  recyclerView, LoadMediaData.LOAD_ALL_SONG).execute();
    }
}