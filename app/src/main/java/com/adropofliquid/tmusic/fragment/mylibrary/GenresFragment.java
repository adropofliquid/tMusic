package com.adropofliquid.tmusic.fragment.mylibrary;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adropofliquid.tmusic.R;


public class GenresFragment extends Fragment {
/*
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;
    private LoadDb loadDb;*/

    public GenresFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_genres, container, false);
        setHasOptionsMenu(true);
        return view;
    }
    /*
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        context = getActivity().getApplicationContext();
        loadDb = new LoadDb(getActivity());

        recyclerView = view.findViewById(R.id.genre_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        new LoadGenre().execute("");


    }
    private class LoadGenre extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            adapter = new GenreAdapter(loadDb.getGenreLists(), context);
            return "Executed";
        }
        @Override
        protected void onPostExecute(String result) {
            recyclerView.setAdapter(adapter);
        }
        @Override
        protected void onPreExecute() {
        }
    }*/
}