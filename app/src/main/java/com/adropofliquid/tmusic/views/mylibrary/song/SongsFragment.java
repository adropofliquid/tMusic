package com.adropofliquid.tmusic.views.mylibrary.song;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adropofliquid.tmusic.App;
import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.data.SongRepository;
import com.adropofliquid.tmusic.uncat.items.SongItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;


public class SongsFragment extends Fragment {


    private RecyclerView recyclerView;
    private Executor executor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_songs, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        recyclerView = view.findViewById(R.id.song_list_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        executor = ((App) requireActivity().getApplicationContext()).getExecutor();
        populateRecycler();
    }

    private void populateRecycler(){

        SongRepository songRepository = new SongRepository(executor);
        songRepository.loadSongs(getContext(), this::spawnAdapter);
    }

    private void spawnAdapter(List<SongItem> songs) {
        AdapterSpawn adapterSpawn = new AdapterSpawn(executor);
        adapterSpawn.spawn(getActivity(), (ArrayList<SongItem>) songs, this::putAdapterOnMainThread);
    }

    private void putAdapterOnMainThread(RecyclerView.Adapter adapter){
        Handler mainThreadHandler = ((App) requireActivity().getApplicationContext()).getMainThreadHandler();
        mainThreadHandler.post(() -> recyclerView.setAdapter(adapter));
    }

}