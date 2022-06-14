package com.adropofliquid.tmusic.views.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.adropofliquid.tmusic.views.fragment.mylibrary.album.AlbumsListFragment;
import com.adropofliquid.tmusic.views.fragment.mylibrary.artist.ArtistsFragment;
import com.adropofliquid.tmusic.views.fragment.mylibrary.song.SongsFragment;

public class ViewPagerFragmentAdapter extends FragmentStateAdapter {


    public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
//            case 0:
//                return new PlaylistFragment();
            case 0:
                return new ArtistsFragment();
            case 1:
                return new AlbumsListFragment();
            default:
                return new SongsFragment();
//            default:
//                return new GenresFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}