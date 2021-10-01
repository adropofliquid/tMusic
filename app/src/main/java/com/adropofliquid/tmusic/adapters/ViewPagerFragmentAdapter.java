package com.adropofliquid.tmusic.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.adropofliquid.tmusic.fragment.mylibrary.*;

public class ViewPagerFragmentAdapter extends FragmentStateAdapter {


    public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
/*

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PlaylistFragment();
            case 1:
                return new ArtistsFragment();
            case 2:
                return new AlbumsFragment();
            case 3:
                return new SongsFragment();
            default:
                return new GenresFragment();
        }
    }
*/

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ArtistsFragment();
            case 1:
                return new AlbumsFragment();
            default:
                return new SongsFragment();
        }
    }
    @Override
    public int getItemCount() {
        return 3;
    }
}