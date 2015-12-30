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

    public static final String ARG_PAGE = "ARG_PAGE";

    public static HotOrNotFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        HotOrNotFragment fragment = new HotOrNotFragment();
        fragment.setArguments(args);
        return fragment;
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
