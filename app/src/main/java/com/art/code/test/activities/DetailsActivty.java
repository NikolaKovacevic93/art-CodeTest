package com.art.code.test.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.art.code.test.R;
import com.art.code.test.URLs;
import com.art.code.test.VolleySingleton;
import com.art.code.test.controllers.LoginController;
import com.art.code.test.models.Restaurant;
import com.art.code.test.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Nikola on 11/16/2017.
 */

public class DetailsActivty extends AppCompatActivity {

    private final String TAG = DetailsActivty.class.getSimpleName();

    @BindView(R.id.details_main_image)
    ImageView detailsMainImage;

    @BindView(R.id.details_restaurant_name)
    TextView detailsRestaurantName;

    @BindView(R.id.details_restaurant_welcome_message)
    TextView detailsRestrountWelcomeMessage;

    @BindView(R.id.details_restaurant_intro)
    TextView detailsRestaurantIntro;

    @BindView(R.id.details_restaurant_is_open)
    TextView detailsRestaurantIsOpen;

    @BindView(R.id.progress_bar_holder)
    FrameLayout progressbarHolder;

    @BindView(R.id.is_open_holder)
    LinearLayout isRestaurantOpenHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        loadDatas();
    }


    private void loadDatas() {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonTableBeacon = new JSONObject();
        try {
            jsonTableBeacon.put("major", "5");
            jsonTableBeacon.put("minor", "1");

            jsonObject.put("table_beacon", jsonTableBeacon);
            jsonObject.put("access_token", LoginController.getInstance().getLoginToken());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs.GET_DATA_URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "---> On response");
                Restaurant restaurant = new Restaurant();
                try {
                    JSONObject restaurantJsonObject = response.getJSONObject("restaurant");

                    restaurant.setIntro(restaurantJsonObject.getString("intro"));
                    restaurant.setName(restaurantJsonObject.getString("name"));
                    restaurant.setIsOpen(restaurantJsonObject.getBoolean("is_open"));
                    restaurant.setWelcomeMessage(restaurantJsonObject.getString("welcome_message"));
                    restaurant.setThumbnailMedium(restaurantJsonObject.getJSONObject("images").getString("thumbnail_medium"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "---> " + e.getMessage());
                }
                setDetailsDatas(restaurant);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "---> " + error.getMessage());
                Toast.makeText(DetailsActivty.this, R.string.failed_to_load_data_toast_message, Toast.LENGTH_SHORT).show();
            }
        });
        VolleySingleton.getInstance(this).addTorequestQueue(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @OnClick(R.id.logout_image_btn)
    void onLogoutClick(){
        LoginController.getInstance().setLoginToken(null);
        SharedPreferences preferences = getSharedPreferences(Utils.SHARED_PR_TOKEN, MODE_PRIVATE);
        preferences.edit().remove(Utils.TOKEN_KEY).commit();
        finish();
    }

    private void setDetailsDatas(Restaurant restaurant) {
        detailsRestaurantName.setText(restaurant.getName());
        detailsRestrountWelcomeMessage.setText(restaurant.getWelcomeMessage());
        detailsRestaurantIntro.setText(restaurant.getIntro());
        detailsRestaurantIsOpen.setText(restaurant.isOpen() ? getString(R.string.restaurant_open_message) : getString(R.string.restaurant_closed_message));
        isRestaurantOpenHolder.setBackgroundColor(restaurant.isOpen() ? getResources().getColor(R.color.colorGreenOpen) : getResources().getColor(R.color.colorRedClosed));
        detailsMainImage.getLayoutParams().height = getImageHeigh();
        Picasso.with(this).load(restaurant.getThumbnailMedium()).into(detailsMainImage);
        progressbarHolder.setVisibility(View.GONE);
    }

    private int getImageHeigh() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return (displayMetrics.widthPixels / 3) * 2;
    }

}
