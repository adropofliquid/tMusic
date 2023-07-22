package com.adropofliquid.tmusic.views.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.adropofliquid.tmusic.views.mylibrary.album.AlbumsListFragment;
import com.adropofliquid.tmusic.views.mylibrary.artist.ArtistsFragment;
import com.adropofliquid.tmusic.views.mylibrary.song.SongsFragment;

public class ViewPagerFragmentAdapter extends FragmentStateAdapter {


    public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return new SongsFragment();
//        switch (position) {
//            case 0:
//                return new AlbumsListFragment();
//            default:
//                return new SongsFragment();
//        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}