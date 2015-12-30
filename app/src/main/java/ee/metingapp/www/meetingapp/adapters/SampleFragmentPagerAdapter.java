package ee.metingapp.www.meetingapp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
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
public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Users", "Chat", "Capture" };
    private int[] imageResId = {
            R.drawable.ic_view_headline_black_18dp,
            R.drawable.ic_face_black_18dp,
            R.drawable.ic_chat_black_18dp
    };
    private Context context;

    public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
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

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable image = ContextCompat.getDrawable(context, imageResId[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return tabTitles[position];
    }
}
