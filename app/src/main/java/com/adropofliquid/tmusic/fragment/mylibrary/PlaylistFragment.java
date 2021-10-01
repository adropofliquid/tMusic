package com.adropofliquid.tmusic.fragment.mylibrary;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adropofliquid.tmusic.R;


public class PlaylistFragment extends Fragment {

/*
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;
    private LoadDb loadDb;*/

    //use d versatile database here
    //we already know defaults so Just list dem


    public PlaylistFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        setHasOptionsMenu(true);

        return view;
    }
/*

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        context = getActivity().getApplicationContext();


        loadDb = new LoadDb(getActivity());

        recyclerView = view.findViewById(R.id.playlist_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        new LoadPlaylist().execute("");

    }

    //TODO
    //Maybe put these in adapters.
    // so adapters can be set initially
    private class LoadPlaylist extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

//            adapter = new PlaylistAdapter(playlistList, getActivity().getApplicationContext());
            adapter = new PlaylistAdapter(loadDb.getPlaylistList(), getActivity().getApplicationContext(), fragmentManager);

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


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) { //appbar shiii
        inflater.inflate(R.menu.playlist_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
*/

}