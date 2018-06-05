package com.example.vreeni.StreetMovementApp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * Created by vreeni on 3/03/2018.
 */


public class GetCustomizedHomeWorkout_ExDescription_Fragment extends Fragment {

    private ViewPager viewPager;
    private ImageButton leftNav;
    private ImageButton rightNav;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_training_exercise_description, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //enable the easy navigation to open up nav drawer again
        ((MainActivity) getActivity()).showBackButton(false);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        leftNav = (ImageButton) view.findViewById(R.id.left_nav);
        rightNav = (ImageButton) view.findViewById(R.id.right_nav);

        // Images left navigation
        leftNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = viewPager.getCurrentItem();
                if (tab > 0) {
                    tab--;
                    viewPager.setCurrentItem(tab);
                } else if (tab == 0) {
                    viewPager.setCurrentItem(tab);
                }
            }
        });

        // Images right navigatin
        rightNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tab = viewPager.getCurrentItem();
                tab++;
                viewPager.setCurrentItem(tab);
            }
        });
    }
}
