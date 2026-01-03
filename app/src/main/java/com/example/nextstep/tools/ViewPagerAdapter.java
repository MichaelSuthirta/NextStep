package com.example.nextstep.tools;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.nextstep.R;
import com.example.nextstep.tab_pages.CertificateFragment;
import com.example.nextstep.tab_pages.OtherSectionFragment;
import com.example.nextstep.tab_pages.ProfileFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private int[] iconList = {
            R.drawable.profile_tab_icon,
            R.drawable.achievement_tab_icon,
            R.drawable.plus_tab_icon
    };

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new ProfileFragment();
            case 1:
                return new CertificateFragment();
            case 2:
                return new OtherSectionFragment();
            default:
                return new ProfileFragment();
        }
    }

    @Override
    public int getItemCount() {
        return iconList.length;
    }

    public int getIcon(int index){
        return iconList[index];
    }
}
