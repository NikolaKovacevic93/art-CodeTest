package com.art.code.test.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.art.code.test.R;
import com.art.code.test.URLs;
import com.art.code.test.VolleySingleton;
import com.art.code.test.controllers.LoginController;
import com.art.code.test.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.login_edit_text_email)
    EditText loginEditTextEmail;

    @BindView(R.id.login_edit_text_password)
    EditText loginEditTextPassword;

    @BindView(R.id.progress_bar_holder)
    FrameLayout progressBarHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loadTestInformations();
        tokenLogin();
    }

    private void loadTestInformations(){
        loginEditTextEmail.setText("test@testmenu.com");
        loginEditTextPassword.setText("test1234");
    }

    @OnClick(R.id.login_button)
    void onLoginButtonClick() {
        progressBarHolder.setVisibility(View.VISIBLE);
        login();
    }

    private void login() {

        if (TextUtils.isEmpty(loginEditTextEmail.getText().toString())) {
            loginEditTextEmail.setError(getString(R.string.validation_email_error));
            loginEditTextEmail.requestFocus();
            progressBarHolder.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(loginEditTextPassword.getText().toString())) {
            loginEditTextPassword.setError(getString(R.string.validation_password_error));
            loginEditTextPassword.requestFocus();
            progressBarHolder.setVisibility(View.GONE);
            return;
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", loginEditTextEmail.getText().toString());
        params.put("password", loginEditTextPassword.getText().toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs.LOGIN_URL, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "---> On response");
                try {
                    LoginController.getInstance().setLoginToken(response.getString("access_token"));
                    saveTokenToSharedPreferences(LoginController.getInstance().getLoginToken());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "---> " + e.getMessage());
                }
                progressBarHolder.setVisibility(View.GONE);
                onLoginSuccess();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "---> " + error.getLocalizedMessage());
                Toast.makeText(LoginActivity.this, R.string.login_failed_toast_message, Toast.LENGTH_SHORT).show();
                progressBarHolder.setVisibility(View.GONE);
            }
        });
        VolleySingleton.getInstance(this).addTorequestQueue(jsonObjectRequest);
    }

    private void onLoginSuccess(){
        Intent intentStartDetailsActivity = new Intent(LoginActivity.this, DetailsActivty.class);
        startActivity(intentStartDetailsActivity);
    }

    private void tokenLogin(){
        SharedPreferences preferences = getSharedPreferences(Utils.SHARED_PR_TOKEN, MODE_PRIVATE);
        if(!preferences.getString(Utils.TOKEN_KEY, "").isEmpty()){
            onLoginSuccess();
        }
    }

    private void saveTokenToSharedPreferences(String token) {
        SharedPreferences preferences = getSharedPreferences(Utils.SHARED_PR_TOKEN, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Utils.TOKEN_KEY, token);
        editor.commit();
    }




}
