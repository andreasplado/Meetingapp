package ee.metingapp.www.meetingapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.nineoldandroids.animation.Animator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.AppConfig;
import app.AppController;
import ee.metingapp.www.meetingapp.R;
import ee.metingapp.www.meetingapp.data.Data;
import ee.metingapp.www.meetingapp.data.User;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import utils.GPSTracker;

/**
 * Created by Andreas on 05.12.2015.
 */
public class HomeFragment extends Fragment {

    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<Data> al;
    private SwipeFlingAdapterView flingContainer;
    private int i;
    public static final String ARG_PAGE = "ARG_PAGE";
    public static MyAppAdapter myAppAdapter;
    public static ViewHolder viewHolder;
    private GPSTracker gpsTracker;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle
                                     savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home,
                container, false);
        flingContainer = (SwipeFlingAdapterView) view.findViewById(R.id.frame);

        swipeView();
        return view;
    }


    private void swipeView(){
        al = new ArrayList<>();
        al.add(new Data("http://http://phonewe.freeiz.com/suhtlus/uploads", "Siia tuleb kasutajate kirjeldus to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness."));
        al.add(new Data("http://http://phonewe.freeiz.com/suhtlus/uploads", "But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness."));
        myAppAdapter = new MyAppAdapter(al, getContext());
        flingContainer.setAdapter(myAppAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {

            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                myAppAdapter.notifyDataSetChanged();
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject

            }

            @Override
            public void onRightCardExit(Object dataObject) {
                myAppAdapter.notifyDataSetChanged();
                likeUser(User.getId(), "42");
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

            }

            @Override
            public void onScroll(float scrollProgressPercent) {

                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.background).setAlpha(0);
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {

                View view = flingContainer.getSelectedView();
                YoYo.with(Techniques.FadeOutUp)
                        .duration(500).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        flingContainer.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                }).playOn(flingContainer);



                myAppAdapter.notifyDataSetChanged();
            }
        });
    }

    private void likeUser(final String userId, final String likeId) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LIKE_USER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", userId);
                params.put("like_id", likeId);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                if(AppConfig.SESSION_ID != ""){
                    headers.put("Cookie",AppConfig.SESSION_ID);
                }
                headers.put("Cookie",AppConfig.SESSION_ID);
                Log.e("CookieId", headers.toString());
                return headers;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public static class ViewHolder {
        public static FrameLayout background;
        public TextView DataText;
        public ImageView cardImage;
        public MaterialProgressBar materialProgressBar;


    }
    public class MyAppAdapter extends BaseAdapter {

        public List<Data> parkingList;
        public Context context;

        private MyAppAdapter(List<Data> apps, Context context) {
            this.parkingList = apps;
            this.context = context;
        }

        @Override
        public int getCount() {
            return parkingList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View rowView = convertView;


            if (rowView == null) {

                LayoutInflater inflater = getActivity().getLayoutInflater();
                rowView = inflater.inflate(R.layout.pic_item, parent, false);
                // configure view holder
                viewHolder = new ViewHolder();
                viewHolder.DataText = (TextView) rowView.findViewById(R.id.bookText);
                viewHolder.background = (FrameLayout) rowView.findViewById(R.id.background);
                viewHolder.cardImage = (ImageView) rowView.findViewById(R.id.cardImage);
                viewHolder.materialProgressBar = (MaterialProgressBar)rowView.findViewById(R.id.imgProgressBar);

                rowView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.DataText.setText(parkingList.get(position).getDescription() + "");

            //Glide.with(HomeFragment.this).load("http://phonewe.freeiz.com/suhtlus/uploads/N/43.jpg").into(viewHolder.cardImage);
            fetchData();
            return rowView;
        }

        private void fetchData() {
            ImageLoader imageLoader = AppController.getInstance().getImageLoader();
            Picasso.with(getContext())
                    .load("http://phonewe.freeiz.com/suhtlus/uploads/N/43.jpg")
                    .into(viewHolder.cardImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            viewHolder.materialProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                        }
                    });

            //imageLoader.get(AppConfig.IMAGE_URL + User.getId() + ".jpg", ImageLoader.getImageListener(
            //profilePic, loading_icon, R.drawable.error_icon));
        }
    }

}
