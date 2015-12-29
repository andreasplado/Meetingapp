package ee.metingapp.www.meetingapp.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ee.metingapp.www.meetingapp.fragment.HotOrNotFragment;


/**
 * Created by Andreas on 29.12.2015.
 */
public class TestAdapter extends FragmentPagerAdapter {

    private final String[] TITLES = {"Categories", "Home", "Top Paid", "Top Free", "Top Grossing", "Top New Paid",
            "Top New Free", "Trending"};

    public TestAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        return HotOrNotFragment.newInstance(position);
    }
}
