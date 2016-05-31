package ee.metingapp.www.meetingapp;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.AppConfig;
import app.AppController;
import ee.metingapp.www.meetingapp.customelements.SeekBarPreference;
import ee.metingapp.www.meetingapp.data.User;
import utils.SQLiteHandler;

public class UserPreferencesActivity extends AppCompatPreferenceActivity implements DatePickerDialog.OnDateSetListener {

    private Preference editBirthdate, updateUser, editInterestedIn, editEmail, editGender, editName;
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    private SeekBarPreference seekBarPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_preference_holder);
        addPreferencesFromResource(R.xml.pref_general);
        findViews();
        createViews();
        addLinksToViews();



    }

    private void findViews() {
        editEmail = (Preference) findPreference("edit_email");
        editName = (Preference) findPreference("edit_name");
        editGender = (Preference) findPreference("edit_gender");
        editInterestedIn = (Preference) findPreference("edit_interested_in");
        editBirthdate = (Preference) findPreference("edit_birthdate");
        updateUser = (Preference) findPreference("btn_update_user");
        updateUser = (Preference) findPreference("btn_update_user");
        seekBarPreference = (SeekBarPreference) findPreference("edit_radius");
        if (User.getGender().equals("M")) {
            editGender.setDefaultValue(1);
        } else {
            editGender.setDefaultValue(0);
        }
        if (User.getInterestedIn().equals("M")) {
            editInterestedIn.setDefaultValue(1);
        } else {
            editInterestedIn.setDefaultValue(0);
        }
        editEmail.setSummary(User.getEmail());
        editName.setSummary(User.getName());
        editBirthdate.setSummary(User.getBirthdate());
        seekBarPreference.setProgress(Integer.parseInt(User.getRadius()));
        seekBarPreference.setSummary(User.getRadius());
        editEmail.setSummary(User.getEmail());
        editName.setSummary(User.getName());
        editGender.setSummary(User.getGender());
        editInterestedIn.setSummary(User.getInterestedIn());
        editBirthdate.setSummary(User.getBirthdate());


    }

    private void createViews() {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

    }

    private void addLinksToViews() {
        editBirthdate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showDateDialog();
                return false;
            }


            private void showDateDialog() {
                Calendar now = Calendar.getInstance();
                int year = Integer.parseInt(User.getBirthdate().substring(6, 10));
                int month = Integer.parseInt(User.getBirthdate().substring(3, 5));
                int day = Integer.parseInt(User.getBirthdate().substring(0, 2));
                DatePickerDialog dpd = DatePickerDialog.newInstance(UserPreferencesActivity.this, year, month, day);
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        updateUser.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String email = (String) editEmail.getSummary();
                String name = (String) editName.getSummary();
                String genderString = (String) editGender.getSummary();
                String interestedInString = (String) editInterestedIn.getSummary();
                String gender = genderString.substring(0,1);
                String interestedIn = interestedInString.substring(0,1);
                String birthdate = (String) editBirthdate.getSummary();
                String id = User.getId();
                String radius = Integer.toString(seekBarPreference.getProgress());
                updateUser(id, email, name, gender, interestedIn, birthdate, radius);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            return true;
        }
        return false;
    }

    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if (monthOfYear < 10 && dayOfMonth < 10) {
            editBirthdate.setSummary("0" + dayOfMonth + "/0" + monthOfYear + "/" + year);
        } else if (monthOfYear < 10) {
            editBirthdate.setSummary(dayOfMonth + "/0" + monthOfYear + "/" + year);
        } else if (dayOfMonth < 10) {
            editBirthdate.setSummary("0" + dayOfMonth + "/" + monthOfYear + "/" + year);
        }
    }

    private void updateUser(final String id, final String email, final String name, final String gender, final String interestedIn, final String birthdate, final String radius) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Updating user settings ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE_USER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();
                db = new SQLiteHandler(getApplicationContext());
                User.setId(id);
                User.setName(name);
                User.setBirthdate(birthdate);
                User.setGender(gender);
                User.setInterestedIn(interestedIn);
                db.addUser(id, name, email, birthdate, gender, interestedIn, radius);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("email", email);
                params.put("name", name);
                params.put("gender", gender);
                params.put("interested_in", interestedIn);
                params.put("birthdate", birthdate);
                params.put("radius", radius);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary("Silent");

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.btn_click);
        mp.start();
        finish();
    }

}
