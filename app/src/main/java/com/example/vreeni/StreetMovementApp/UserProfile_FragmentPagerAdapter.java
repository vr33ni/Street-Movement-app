//package com.example.vreeni.StreetMovementApp;
//
//import android.content.Context;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//
//public class UserProfile_FragmentPagerAdapter extends FragmentPagerAdapter {
//    final int PAGE_COUNT = 3;
//    private String tabTitles[] = new String[] { "Tab1", "Tab2", "Tab3" };
//    private Context context;
//
//    public UserProfile_FragmentPagerAdapter(FragmentManager fm, Context context) {
//        super(fm);
//        this.context = context;
//    }
//
//    @Override
//    public int getCount() {
//        return PAGE_COUNT;
//    }
//
//    @Override
//    public Fragment getItem(int position) {
//        return UserProfile_Account.newInstance(position + 1);
//    }
//
//    @Override
//    public CharSequence getPageTitle(int position) {
//        // Generate title based on item position
//        return tabTitles[position];
//    }
//}