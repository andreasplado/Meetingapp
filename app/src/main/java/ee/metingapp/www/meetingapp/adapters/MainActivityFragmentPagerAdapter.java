package ee.metingapp.www.meetingapp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import ee.metingapp.www.meetingapp.R;
import ee.metingapp.www.meetingapp.fragment.ChatFragment;
import ee.metingapp.www.meetingapp.fragment.HomeFragment;
import ee.metingapp.www.meetingapp.fragment.TakeSnapFragment;

/**
 * Created by Andreas on 30.12.2015.
 */
public class MainActivityFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String[] tabTitles = {"Main", "Chat", "Snap"};
    private int[] imageResId = {
            R.drawable.ic_whatshot_black_18dp,
            R.drawable.ic_face_black_18dp,
            R.drawable.ic_camera_black_18dp
    };
    private Context context;

    public MainActivityFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new ChatFragment();
            case 2:
                return new TakeSnapFragment();
            default:
                return new HomeFragment();
        }

    }

    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        Drawable image = context.getResources().getDrawable(imageResId[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        // Replace blank spaces with image icon
        SpannableString sb = new SpannableString("   " + tabTitles[position]);
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
}
