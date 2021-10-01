package com.adropofliquid.tmusic.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.adropofliquid.tmusic.R;
import com.adropofliquid.tmusic.adapters.ViewPagerFragmentAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class MyLibraryFragment extends Fragment {

    public MyLibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_my_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ViewPager2 viewPager2 = view.findViewById(R.id.viewPager);
        viewPager2.setAdapter(new ViewPagerFragmentAdapter(getActivity()));

        TabLayout tabLayout = getActivity().findViewById(R.id.library_tabs);
        /*TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
                tabLayout, viewPager2, (tab, position) -> {
                    switch (position){
                        case 0:{
                            tab.setText("PLAYLISTS");
                            break;}
                        case 1:{
                            tab.setText("ARTISTS");
                            break;}
                        case 2:{
                            tab.setText("ALBUMS");
                            break;}
                        case 3:{
                            tab.setText("SONGS");
                            break;}
                        case 4:{
                            tab.setText("GENRES");
                            break;}
                    }
                }
        );*/
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
                tabLayout, viewPager2, (tab, position) -> {
            switch (position){
                case 0:{
                    tab.setText("ARTISTS");
                    break;}
                case 1:{
                    tab.setText("ALBUMS");
                    break;}
                case 2:{
                    tab.setText("SONGS");
                }
            }
        }
        );
        tabLayoutMediator.attach();

//        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        ///TODO How to add TOolBar???
        /*toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.toolbar_my_library);
        getSupportActionBar().setDisplayShowTitleEnabled(true);*/
    }
}