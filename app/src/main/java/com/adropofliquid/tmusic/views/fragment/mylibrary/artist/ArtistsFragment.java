package com.adropofliquid.tmusic.views.fragment.mylibrary.artist;

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

public class ArtistsFragment extends Fragment {

    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;

    public ArtistsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_artists, container, false);
        //        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_artists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        populateRecycler(view);
    }

    private void populateRecycler(View view) {

        RecyclerView.LayoutManager layoutManager;
        recyclerView = view.findViewById(R.id.artist_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        new LoadMediaData(getActivity(),  recyclerView, LoadMediaData.LOAD_ALL_ARTIST).execute();

    }


}