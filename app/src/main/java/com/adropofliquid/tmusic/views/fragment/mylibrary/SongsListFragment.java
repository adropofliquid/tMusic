package com.adropofliquid.tmusic.views.fragment.mylibrary;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.mediastore.LoadMediaStore;
import com.adropofliquid.tmusic.views.activity.MainActivity;
import com.adropofliquid.tmusic.views.adapters.AlbumViewListAdapter;
import com.adropofliquid.tmusic.views.adapters.SongListAdapter;


public class SongsListFragment extends Fragment {

    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private String albumId;

    public SongsListFragment(String albumId) {
        this.albumId = albumId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_album_view, container, false);
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
        recyclerView = view.findViewById(R.id.album_view_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        new LoadSongs().execute();
    }

    private  class LoadSongs extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            adapter = new AlbumViewListAdapter(getActivity(),
                    new LoadMediaStore(getContext()).getSongs(albumId));
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