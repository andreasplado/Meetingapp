package ee.metingapp.www.meetingapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;
import java.util.HashMap;

import ee.metingapp.www.meetingapp.fragment.ChatFragment;
import ee.metingapp.www.meetingapp.fragment.HelpFragment;
import ee.metingapp.www.meetingapp.fragment.HomeFragment;
import ee.metingapp.www.meetingapp.fragment.HotOrNotFragment;
import ee.metingapp.www.meetingapp.fragment.PreferencesFragment;
import ee.metingapp.www.meetingapp.view.ImageConverter;
import ee.metingapp.www.meetingapp.view.NavigationDrawerActivity;
import ee.metingapp.www.meetingapp.view.TestAdapter;
import utils.SQLiteHandler;
import utils.SessionManager;

public class MainActivity extends AppCompatActivity{

    private static String TAG = MainActivity.class.getSimpleName();

    private ListView mDrawerList;
    private RelativeLayout mDrawerPane;
    private TextView userName;
    private LinearLayout app_layer;
    private ImageView imgProfilePic;
    private ImageButton imgBtnCapture;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private SQLiteHandler db;
    private SessionManager session;
    private boolean doubleBackToExitPressedOnce = false;
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    private HotOrNotFragment hotOrNotFragment = new HotOrNotFragment();
    private ChatFragment chatFragment = new ChatFragment();
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userName = (TextView)findViewById(R.id.userName);
        app_layer = (LinearLayout) findViewById (R.id.user_profile);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new TestAdapter(getSupportFragmentManager()));
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);

        goToUserProfile();
        makeCustomActionBar();
        //makeCustomActionBar();
        imgProfilePic = (ImageView)findViewById(R.id.profile_pic_small);
        imgBtnCapture = (ImageButton)findViewById(R.id.btn_capture_picture);
        mNavItems.add(new NavItem("Home", "Meetup destination", R.drawable.abc_btn_radio_material));
        mNavItems.add(new NavItem("Preferences", "Change your preferences", R.drawable.abc_btn_radio_material));
        mNavItems.add(new NavItem("About", "Get to know about us", R.drawable.abc_btn_radio_material));
        printPicture();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);
        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");
        userName.setText(name);

        // Displaying the user details on the screen
        //txtName.setText(name);
        //txtEmail.setText(email);
        imgBtnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakeSnapActivity fragment = new TakeSnapActivity();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.mainContent, fragment)
                        .commit();
                mDrawerLayout.closeDrawers();
                Log.e("info:", "Vajutasid nupule pildista");
            }
        });

        startMainFragment();


    }


    private void startMainFragment() {
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainContent, fragment)
                .commit();
    }


    private void goToUserProfile() {

        app_layer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        UserProfileActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void printPicture() {
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.lanny_barbie);
        Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 300);

        imgProfilePic.setImageBitmap(circularBitmap);
    }

    private void selectItemFromDrawer(int position) {
        Fragment fragment = new PreferencesFragment();
        switch (position){
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new PreferencesFragment();
                break;
            case 2:
                fragment = new HelpFragment();
                break;
            default:
                fragment = new HomeFragment();

        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainContent, fragment)
                .commit();

        mDrawerList.setItemChecked(position, true);
        setTitle(mNavItems.get(position).mTitle);

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                logoutUser();
                return true;
        }
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            try {
                Intent in = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                //in.putExtra("FrequencyExtractorSettings", FESettings);
                startActivity(in);
            } catch (Exception e) {
                Log.i("navigationdrawertest", "Failed to launch NavigationDrawerActivity: " + e.getMessage());
            }
            return true;
        }

            return super.onOptionsItemSelected(item);
    }
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    class NavItem {
        String mTitle;
        String mSubtitle;
        int mIcon;

        public NavItem(String title, String subtitle, int icon) {
            mTitle = title;
            mSubtitle = subtitle;
            mIcon = icon;
        }
    }

    class DrawerListAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<NavItem> mNavItems;

        public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
            mContext = context;
            mNavItems = navItems;
        }

            @Override
            public int getCount() {
                return mNavItems.size();
            }

            @Override
            public Object getItem(int position) {
                return mNavItems.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view;

                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.drawer_item, null);
                }
                else {
                    view = convertView;
                }

                TextView titleView = (TextView) view.findViewById(R.id.title);
                TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);
                ImageView iconView = (ImageView) view.findViewById(R.id.icon);

                titleView.setText( mNavItems.get(position).mTitle );
            subtitleView.setText( mNavItems.get(position).mSubtitle );
            iconView.setImageResource(mNavItems.get(position).mIcon);

            return view;
        }
    }

    private void makeCustomActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.actionbar_main, null);

        ImageButton menuButton = (ImageButton) mCustomView
                .findViewById(R.id.menu_button);
        final ImageButton hotOrNotButton = (ImageButton) mCustomView
                .findViewById(R.id.hot_or_not_button);
        final ImageButton chatButton = (ImageButton) mCustomView
                .findViewById(R.id.chat_button);
        menuButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    YoYo.with(Techniques.ZoomIn)
                            .duration(700)
                            .playOn(findViewById(R.id.menu_button));
                }else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                    YoYo.with(Techniques.ZoomIn)
                            .duration(700)
                            .playOn(findViewById(R.id.menu_button));
                }
            }
        });

        hotOrNotButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.mainContent, hotOrNotFragment).commit();
                YoYo.with(Techniques.Wobble)
                        .duration(700)
                        .playOn(findViewById(R.id.hot_or_not_button));
                mDrawerLayout.closeDrawers();
                hotOrNotButton.setClickable(false);
                chatButton.setClickable(true);
            }
        });
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.mainContent, chatFragment).commit();
                YoYo.with(Techniques.Wobble)
                        .duration(700)
                        .playOn(findViewById(R.id.chat_button));
                mDrawerLayout.closeDrawers();
                chatButton.setClickable(false);
                hotOrNotButton.setClickable(true);
            }
        });

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        }else{
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

}