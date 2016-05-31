package ee.metingapp.www.meetingapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.AppConfig;
import app.AppController;
import ee.metingapp.www.meetingapp.customelements.SwitchButton;
import utils.SQLiteHandler;
import utils.SessionManager;

public class RegisterActivity extends Activity implements DatePickerDialog.OnDateSetListener {

    private Button btnRegister;
    private Button btnLinkToLogin;
    private Button btnSelectDate;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private SwitchButton btnGender, btnInterest;
    private TextView txtGender, txtInterest, txtAge;
    private TextView txtBirthdate;
    private DiscreteSeekBar radiusBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViews();
        createViews();
        addLinksToViews();
        manageSession();

    }

    private void manageSession() {
        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    private void addLinksToViews() {
        btnGender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if(isChecked) {
                    txtGender.setText("Woman");
                }else{
                    txtGender.setText("Man");
                }
            }
        });
        btnInterest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked) {
                    txtInterest.setText("Man");
                } else {
                    txtInterest.setText("Woman");
                }
            }
        });

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String gender = txtGender.getText().toString().trim().substring(0, 1);
                String interesetdIn = txtInterest.getText().toString().trim().substring(0, 1);
                String birthDate = txtBirthdate.getText().toString().trim();
                String age = txtAge.getText().toString().trim();
                String radius = Integer.toString(radiusBar.getProgress());

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !age.equals('0')) {
                    if(emailValidator(email) == false){
                        Toast.makeText(getApplicationContext(),
                                "The entered email is not valid", Toast.LENGTH_LONG)
                                .show();
                    }else if(Integer.parseInt(age)< 18) {
                        Toast.makeText(getApplicationContext(),
                                "You must be over 18 to use this application", Toast.LENGTH_LONG)
                                .show();
                    }else{
                        registerUser(name, email, password, gender, interesetdIn, birthDate, radius);
                    }
                }else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        RegisterActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });
    }

    private void createViews() {
    }

    private void findViews() {
        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        btnGender = (SwitchButton)findViewById(R.id.btn_gender);
        btnInterest = (SwitchButton)findViewById(R.id.btn_interest);
        txtGender = (TextView)findViewById(R.id.txt_gender_man);
        txtInterest = (TextView)findViewById(R.id.txt_interest_woman);
        btnSelectDate = (Button)findViewById(R.id.btn_select_birthdate);
        txtAge = (TextView)findViewById(R.id.txt_age);
        txtBirthdate = (TextView)findViewById(R.id.txt_birthdate);
        radiusBar = (DiscreteSeekBar) findViewById(R.id.radius);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String name, final String email,
                              final String password, final String gender, final String interestedIn, final String birthdate, final String radius) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";
        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("", "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user.getString("created_at");


                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                RegisterActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("", "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("birthdate", birthdate);
                params.put("gender", gender);
                params.put("interested_in", interestedIn);
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
    public void onBackPressed() {
        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String age =  getAge(year, monthOfYear, dayOfMonth);
        txtAge.setText(age);
        monthOfYear = monthOfYear + 1;
        if(monthOfYear < 10 && dayOfMonth < 10) {
            txtBirthdate.setText("0" + dayOfMonth + "/0" + monthOfYear + "/" + year);
        }else if(monthOfYear < 10) {
            txtBirthdate.setText(dayOfMonth + "/0" + monthOfYear + "/" + year);
        }else if(dayOfMonth < 10) {
            txtBirthdate.setText("0" + dayOfMonth + "/" + monthOfYear + "/" + year);
        }
    }

    public String getAge (int year, int month, int day) {
        GregorianCalendar cal = new GregorianCalendar();
        int y, m, d, a;

        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(year, month, day);
        a = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --a;
        }
        if(a < 3)
            Toast.makeText(getApplicationContext(),
                    "You are too young to use this application", Toast.LENGTH_LONG)
                    .show();
        return Integer.toString(a);
    }

    private boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
