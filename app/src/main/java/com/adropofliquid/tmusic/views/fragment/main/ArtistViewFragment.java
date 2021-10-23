package com.adropofliquid.tmusic.views.fragment.main;

import android.os.AsyncTask;
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
import com.adropofliquid.tmusic.mediastore.LoadMediaStore;
import com.adropofliquid.tmusic.views.activity.MainActivity;
import com.adropofliquid.tmusic.views.adapters.AlbumViewListAdapter;
import com.adropofliquid.tmusic.views.adapters.ArtistViewListAdapter;
import com.adropofliquid.tmusic.views.fragment.mylibrary.SongsListFragment;

public class ArtistViewFragment extends Fragment {

    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private final String artist;

    public ArtistViewFragment(String artist) {
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
        new LoadSongs().execute();
    }

    private  class LoadSongs extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            adapter = new ArtistViewListAdapter(getActivity(),
                    new LoadMediaStore(getContext()).getAlbums(artist));
            return "Executed";
        }
        @Override
        protected void onPostExecute(String result) {
            recyclerView.setAdapter(adapter);
        }
        @Override
        protected void onPreExecute() {
        }
    }
}