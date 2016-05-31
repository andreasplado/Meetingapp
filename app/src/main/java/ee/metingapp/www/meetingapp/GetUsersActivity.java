package ee.metingapp.www.meetingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import app.AppConfig;
import app.AppController;
import ee.metingapp.www.meetingapp.data.User;
import utils.GPSTracker;
import utils.SQLiteHandler;

public class GetUsersActivity extends AppCompatActivity {

    private View mContentView;
    private boolean mVisible;
    private boolean doubleBackToExitPressedOnce = false;
    private GPSTracker gpsTracker;
    private String id, name, email, age, interestedIn, birthdate, gender, radius;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_users);

        mVisible = true;
        fetchData();
        setData();
        manageGPS();
        fetchTwentyUsers(User.getLongitude(), User.getLatitude(), User.getRadius(), User.getInterestedIn());
        //fetchTwentyUsersRetrofit(User.getLongitude(), User.getLatitude(), User.getRadius(), User.getInterestedIn());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            switch (requestCode) {
                case 1:
                    break;
            }
        }
    }

    private void fetchTwentyUsersRetrofit(final double longitude,
                                          final double latitude, final String radius, final String interestedIn){
        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.MAIN_URL)
                .build(); //Defining base URL


        MyApi myApi = retrofit.create(MyApi.class);
        myApi.getAllUsers(AppConfig.SESSION_ID, new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                try {
                    JSONObject jObj = new JSONObject(response.body());
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Things what should happen then the response is not successful.

            }
        });
        */
    }

    private void fetchTwentyUsers(final double longitude,
                                      final double latitude, final String radius, final String interestedIn) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_USERS_LOCATION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(),
                        response, Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                Log.e("fetchtwentyusers", response);
                finish();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("longitude", String.valueOf(longitude));
                params.put("latitude", String.valueOf(latitude));
                params.put("radius", radius);
                params.put("interested_in", interestedIn);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("PHPSESSID",AppConfig.SESSION_ID);
                Log.e("CookieId", headers.toString());
                return headers;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void setData() {
        User.setId(id);
        User.setEmail(email);
        User.setName(name);
        User.setAge(age);
        User.setInterestedIn(interestedIn);
        User.setGender(gender);
        User.setBirthdate(birthdate);
        User.setRadius(radius);
    }


    private void fetchData() {
        // Fetching user details from sqlite
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        id = user.get("uid");
        name = user.get("name");
        email = user.get("email");
        age = user.get("age");
        interestedIn = user.get("interested_in");
        birthdate = user.get("birthdate");
        gender = user.get("gender");
        radius = user.get("radius");

    }

    private void manageGPS() {
        gpsTracker = new GPSTracker(this);
        if(gpsTracker.canGetLocation() == false) {
            gpsTracker.showSettingsAlert();
        }else {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            User.setLongitude(longitude);
            User.setLatitude(latitude);
            registerUserLocation(User.getId(), Double.toString(longitude), Double.toString(latitude));
        }
    }

    private void registerUserLocation(final String userId, final String longitude,
                                      final String latitude) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE_USER_LOCATION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(),
                        "User location updated(longitude: " + longitude + "latitude: " + latitude, Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", userId);
                params.put("longitude", longitude);
                params.put("latitude", latitude);
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
