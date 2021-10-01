package com.adropofliquid.tmusic.fragment.mylibrary;

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
import com.adropofliquid.tmusic.adapters.ArtistsAdapter;
import com.adropofliquid.tmusic.db.LoadMediaStore;

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

        new LoadArtists().execute("");

    }

    private  class LoadArtists extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            adapter = new ArtistsAdapter(getActivity(),
                    new LoadMediaStore(getContext()).getAllArtists());
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