package ee.metingapp.www.meetingapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParsePushBroadcastReceiver;

import ee.metingapp.www.meetingapp.data.Message;
import ee.metingapp.www.meetingapp.fragment.ChatFragment;

/**
 * Created by mart22n on 5.01.2016.
 */
public class PushNotificationReceiver extends ParsePushBroadcastReceiver {

    public PushNotificationReceiver() {
    }

    @Override
    public void onPushOpen(Context context, Intent intent) {
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra("toOpen", "ChatFragment");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
