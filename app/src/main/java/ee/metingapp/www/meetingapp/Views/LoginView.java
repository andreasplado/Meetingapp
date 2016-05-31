package ee.metingapp.www.meetingapp.Views;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;

import ee.metingapp.www.meetingapp.R;
import utils.SQLiteHandler;
import utils.SessionManager;

/**
 * Created by Andreas on 27.04.2016.
 */
public class LoginView extends Activity {

    private MediaPlayer mp;
    private Context context;
    private SQLiteHandler db;
    private SessionManager session;

    public LoginView(Context context){
        this.context = context;
        mp  = MediaPlayer.create(context, R.raw.btn_click);

    }


}
