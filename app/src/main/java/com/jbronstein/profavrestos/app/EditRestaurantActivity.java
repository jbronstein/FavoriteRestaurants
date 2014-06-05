package com.jbronstein.profavrestos.app;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Josh on 5/31/14.
 */
public class EditRestaurantActivity extends RestaurantActivity {

    private String mYelpResponse = "";
    ArrayList<String> mListItems = new ArrayList<String>();
    private String mQuery;
    private String mCity;
    private JSONArray mJS;
    private String mChoice;
    JSONObject mJson;
    private boolean checked;
    private EditText resto_name;
    private EditText city_name;
    private EditText mNum;
    private EditText mAdd;
    private EditText mYelpPage;
    private String mName;
    private String mAddress;
    private String mPhone;
    private String mUrl;
    private String mNote;
    private String mImage;
    private int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_details);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Get Restaurant Details for Editing
        Intent i = getIntent();
        Restaurant r = (Restaurant)i.getSerializableExtra("Restaurant");
        if (r != null){
            fetchRestoDetails(r);
        }

        //Fetch Restos
        Button searchBtn = (Button) findViewById(R.id.fetch_info);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resto_name = (EditText) findViewById(R.id.name);
                city_name = (EditText) findViewById(R.id.city);
                mQuery = resto_name.getText().toString();
                mCity = city_name.getText().toString();

                getYelpData f = new getYelpData();
                f.execute();
            }
        });

        //Cancel any changes/info retrieval
        Button cancelUpdate = (Button)findViewById(R.id.cancel);
        cancelUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(RESULT_CANCELED, i);
                finish();

            }
        });

        //Save Restaurant to ListView
        Button saveDetails = (Button)findViewById(R.id.save);
        saveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CheckBox checkBox = (CheckBox) findViewById(R.id.check_box_imp);
                if(checkBox.isChecked()) {
                    checked = true;
                }
                else {
                    checked = false;
                }

                mNote = "";
                Intent returnIntent = new Intent();

                returnIntent.putExtra("NAME", mName);
                returnIntent.putExtra("CHECK", checked);
                returnIntent.putExtra("CITY", mCity);
                returnIntent.putExtra("PHONE", mPhone);
                returnIntent.putExtra("ADDRESS", mAddress);
                returnIntent.putExtra("YELP_URL", mUrl);
                returnIntent.putExtra("NOTE", mNote);
                returnIntent.putExtra("IMAGE", mImage);

                setResult(RESULT_OK, returnIntent);
                finish();

            }
        });


    }


    private class getYelpData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {

                String consumerKey = "B-xQiu8e8IIYDRAzsjdpcg";
                String consumerSecret = "bespKcQdSttf256rLo0EsOrcZ5Q";
                String token = "wGe8yYhIsOZT7Lj_r_5cLPhWDzeFyhnJ";
                String tokenSecret = "fVGZMRK2NpyD03Yk3Q69Zu4zs_s";

                Yelp yelp = new Yelp(consumerKey, consumerSecret, token, tokenSecret);
                //String response = yelp.search(mQuery, 30.361471, -87.164326);
                //String response = yelp.search(mQuery, 30.361471, -87.164326);
                String response = yelp.search(mQuery, mCity);

                mYelpResponse = response;


//               JSONObject jsonObj = new JSONObject(response);
//               mYelpResponse = jsonObj.toString();

                //Getting JSON Array node
                //currencyRates = jsonObj.getJSONObject("");

                //} catch (JSONException e) {
            } catch (Exception e) {

                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            try {
                mJson = new JSONObject(mYelpResponse);
                //json=json.getJSONObject("businesses");


                mJS = mJson.getJSONArray("businesses");

                size = mJS.length();
                if (size > 10){
                    size = 10;
                }

                for (int i = 0; i < size; i++) {

                    JSONObject entry = mJS.getJSONObject(i);

                    JSONObject address = entry.getJSONObject("location");
                    JSONArray a = address.getJSONArray("address");

                    String rAddr = a.getString(0);
                    String rName = entry.getString("name");


                    StringBuilder sb = new StringBuilder();
                    sb.append(rName);
                    sb.append(" | ");
                    sb.append(rAddr);

                    mListItems.add(sb.toString());
                    // Log.i("mListItems: ", sb.toString());

                }

                runChooser();

            } catch (JSONException e) {
                Log.e("ERROR:", "JSON PARSING ERROR");
            }
        }

    }


    //Make List of Potential Restaurants based on User Choice
    public void runChooser() {

        int count = 0;

        final CharSequence[] items = new CharSequence[size];
        for (int j = 0; j < size; j++) {
            items[j] = "";
        }

        for (int i = 0; i < size; i++) {
            items[i] = mListItems.get(i).toString();

        }

        AlertDialog.Builder b = new AlertDialog.Builder(EditRestaurantActivity.this);

        b.setTitle("Select A Restaurant");
        b.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int c) {
                mChoice = items[c].toString();

                for (int i = 0; i < size; i++) {
                    try {

                        JSONObject entry = mJS.getJSONObject(i);
                        JSONObject address = entry.getJSONObject("location");
                        JSONArray a = address.getJSONArray("address");
                        String checkAddress = a.getString(0);

                        if (mChoice.contains(entry.getString("name")) && mChoice.contains(checkAddress)) {
                            mName = entry.getString("name");
                            mAddress = checkAddress;
                            mPhone = entry.getString("phone");
                            mUrl = entry.getString("url");
                            mImage = entry.getString("image_url");
                            resto_name.setText(mName);

                            updateDetails();
                        }

                    } catch (JSONException e) {
                        Log.e("ERROR: ", "JSON PARSING ERROR HERE");
                    }
                }
            }
        });

        AlertDialog alert = b.create();
        b.show();

    }

    //Based on User Choice, Update all Details
    public void updateDetails() {

        try {
            ImageView i = (ImageView) findViewById(R.id.yelp_image);
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(mImage).getContent());
            i.setImageBitmap(bitmap);
        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }

        mNum = (EditText)findViewById(R.id.phone_num);
        mNum.setText(mPhone);
        mAdd = (EditText)findViewById(R.id.street_address);
        mAdd.setText(mAddress);
        mYelpPage = (EditText)findViewById(R.id.yelp_url);
        mYelpPage.setText(mUrl);

    }

    //Restore Resto Details
    public void fetchRestoDetails(Restaurant r){
        r.getContent();
        r.getCity();
        resto_name = (EditText) findViewById(R.id.name);
        resto_name.setText(r.getContent());
        city_name = (EditText) findViewById(R.id.city);
        city_name.setText(r.getCity());
        mNum = (EditText)findViewById(R.id.phone_num);
        mNum.setText(r.getPhone());
        mAdd = (EditText)findViewById(R.id.street_address);
        mAdd.setText(r.getAddress());
        mYelpPage = (EditText)findViewById(R.id.yelp_url);
        mYelpPage.setText(r.getUrl());

        try {
            ImageView j = (ImageView) findViewById(R.id.yelp_image);
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(r.getImage()).getContent());
            j.setImageBitmap(bitmap);
        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }

        mName = r.getContent();
        mCity = r.getCity();
        mAddress = r.getAddress();
        mPhone = r.getPhone();
        mUrl = r.getUrl();
        mImage = r.getImage();

    }
}
