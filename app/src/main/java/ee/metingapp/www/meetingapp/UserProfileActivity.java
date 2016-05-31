package ee.metingapp.www.meetingapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import app.AppConfig;
import app.AppController;
import ee.metingapp.www.meetingapp.data.User;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class UserProfileActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private ImageView profilePic;
    private TextView userInfo;
    private MaterialProgressBar materialProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        findViews();
        createViews();
        addLinksToViews();
        fetchData();
    }


    private void findViews() {
        fab = (FloatingActionButton) findViewById(R.id.btn_edit_user);
        profilePic = (ImageView) findViewById(R.id.profile_pic);
        userInfo = (TextView) findViewById(R.id.user_info);
        materialProgressBar = (MaterialProgressBar) findViewById(R.id.imgProgressBar);


    }


    private void createViews() {
        String name = User.getName();
        userInfo.setText("Name: " + name + "   " + User.getAge() + " \n\n" + "Gender: " + User.getGender() + "\n\n" + "Interests: " + User.getInterestedIn());
    }
    private void addLinksToViews() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserProfileActivity.this, UserPreferencesActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    private void fetchData() {
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        materialProgressBar.setVisibility(View.VISIBLE);
        imageLoader.get(AppConfig.IMAGE_URL + User.getGender() + "/" + User.getId() + ".jpg", new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    // load image into imageview
                    profilePic.setImageBitmap(response.getBitmap());
                    materialProgressBar.setVisibility(View.GONE);
                    profilePic.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        //imageLoader.get(AppConfig.IMAGE_URL + User.getId() + ".jpg", ImageLoader.getImageListener(
                //profilePic, loading_icon, R.drawable.error_icon));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(UserProfileActivity.this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.logout);
        mp.start();
        finish();
    }
}
