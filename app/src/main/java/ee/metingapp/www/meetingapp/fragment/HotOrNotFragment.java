package ee.metingapp.www.meetingapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ee.metingapp.www.meetingapp.R;

/**
 * Created by Andreas on 10.12.2015.
 */
public class HotOrNotFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    public static HotOrNotFragment newInstance(int position) {
        HotOrNotFragment f = new HotOrNotFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle
                                     savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_hot_or_not,
                container, false);
        return view;
    }

}
