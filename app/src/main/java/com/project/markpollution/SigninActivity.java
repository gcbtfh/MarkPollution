package com.project.markpollution;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.project.markpollution.Controller.ConnectivityReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SigninActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    public static GoogleApiClient googleApiClient;
    private SignInButton signInButton;
    private String url_insertUser = "http://2dev4u.com/dev/markpollution/InsertUser.php";
    private String url_checkUserByEmail = "http://2dev4u.com/dev/markpollution/RetrieveUserByEmail.php?email=";
    private String url_retrieve_pollutionPoint = "http://2dev4u.com/dev/markpollution/RetrievePollutionPoint.php";
    private Intent intent;
    private ProgressDialog progressDialog;
    RelativeLayout relativeLayoutSigin;
    TextView tvAppname;
    Typeface tf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        changeLocale(this, restoringPreferences());
        setContentView(R.layout.activity_signin);
        tvAppname = (TextView) findViewById(R.id.tvAppname);
        tf = Typeface.createFromAsset(getAssets(), "gunplayrg.ttf");
        tvAppname.setTypeface(tf);
        relativeLayoutSigin = (RelativeLayout) findViewById(R.id.activity_signin);
        changeStatusBarColor();
        checkConnection();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder()
                .requestEmail()
                .requestProfile()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .enableAutoManage(this, this)
                .build();

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        signInButton.setVisibility(View.INVISIBLE);

        getAllPollutionPoint();
    }

    private void signIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, 111);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                final GoogleSignInAccount acc = result.getSignInAccount();

                String urlCheckUserExistOrNot = url_checkUserByEmail + acc.getEmail();
                StringRequest stringReq = new StringRequest(Request.Method.GET, urlCheckUserExistOrNot, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.equals("user doesn't exist")) {     // When user exists
                            // Save id_user to SharedPreferences
                            saveUserIDtoSharedPreferences(null, response, true);
                            intent.putExtra("name", acc.getDisplayName());
                            intent.putExtra("email", acc.getEmail());
                            intent.putExtra("avatar", acc.getPhotoUrl().toString());
                            startActivity(intent);
                            finish();
                        } else {
                            insertUser(acc.getDisplayName(), acc.getEmail(), acc.getPhotoUrl().toString());
                            intent.putExtra("name", acc.getDisplayName());
                            intent.putExtra("email", acc.getEmail());
                            intent.putExtra("avatar", acc.getPhotoUrl().toString());
                            startActivity(intent);
                            finish();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SigninActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Volley", error.getMessage());
                    }
                });

                Volley.newRequestQueue(this).add(stringReq);
            } else {
                Toast.makeText(this, result.getStatus().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveUserIDtoSharedPreferences(String email, final String id_user, boolean isUserExist) {
        if (isUserExist) {
            SharedPreferences sharedPreferences = getSharedPreferences("sharedpref_id_user", MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("sharedpref_id_user", id_user);
            edit.commit();
        } else {
            String urlCheck = url_checkUserByEmail + email;
            StringRequest stringReq = new StringRequest(Request.Method.GET, urlCheck, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    SharedPreferences sharedPreferences = getSharedPreferences("sharedpref_id_user", MODE_PRIVATE);
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putString("sharedpref_id_user", response);
                    edit.commit();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SigninActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            Volley.newRequestQueue(this).add(stringReq);
        }
    }

    private void insertUser(final String name, final String email, final String avatar) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_insertUser, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("insert success")) {
                    Toast.makeText(SigninActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                    saveUserIDtoSharedPreferences(email, null, false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SigninActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Volley", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name_user", name);
                params.put("email", email);
                params.put("avatar", avatar);
                params.put("is_admin", "0");

                return params;
            }
        };

        RequestQueue reqQueue = Volley.newRequestQueue(this);
        reqQueue.add(stringRequest);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void getAllPollutionPoint() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_retrieve_pollutionPoint, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                intent = new Intent(SigninActivity.this, MainActivity.class);
                intent.putExtra("po_data", response);
                signInButton.setVisibility(View.VISIBLE);
                hideProgressDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                Toast.makeText(SigninActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Volley", error.getMessage());
            }
        });
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.loadingdata));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        progressDialog.hide();
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        int color;
        Snackbar snackbar;
        if (isConnected) {
//            color = Color.GREEN;
//            snackbar = Snackbar
//                    .make(relativeLayoutSigin, R.string.conection_good, Snackbar.LENGTH_LONG);
//            snackbar.setAction("OK",
//                    new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                        }
//                    });
        } else {
            snackbar = Snackbar
                    .make(relativeLayoutSigin, R.string.conection_error, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.action_settings,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.setComponent(new ComponentName("com.android.settings",
                                    "com.android.settings.wifi.WifiSettings"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });
            color = Color.RED;
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
    }

    public static void changeLocale(Activity activity, String language) {
        final Resources res = activity.getResources();
        final Configuration conf = res.getConfiguration();
        if (language == null || language.length() == 0) {
            conf.locale = Locale.getDefault();
        } else {
            conf.locale = new Locale(language);
        }
        res.updateConfiguration(conf, null);
    }
    public String restoringPreferences(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String language = prefs.getString("language","");
        return language;
    }
}
