package com.adropofliquid.tmusic.views.mylibrary.artist_view;

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
import com.adropofliquid.tmusic.data.mediastore.LoadMediaData;
import com.adropofliquid.tmusic.views.MainActivity;

public class ArtistViewFragment extends Fragment {

    private RecyclerView recyclerView;
    private final long artist;

    public ArtistViewFragment(long artist) {
        this.artist = artist;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artist_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        populateRecycler(view);
    }

    @Override
    public void onDetach() {
        ((MainActivity) getActivity()).showTabLayout();
        super.onDetach();
    }

    private void populateRecycler(View view) {

        RecyclerView.LayoutManager layoutManager;
        recyclerView = view.findViewById(R.id.artist_view_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        new LoadMediaData(getActivity(), recyclerView, LoadMediaData.LOAD_ALBUMS, artist).execute();
    }
}