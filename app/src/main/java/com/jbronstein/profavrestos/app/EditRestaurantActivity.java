package com.jbronstein.profavrestos.app;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private EditText resto_name;
    private EditText city_name;
    private TextView mNum;
    private TextView mAdd;
    private TextView mYelpPage;
    private String mName;
    private String mAddress;
    private String mPhone;
    private String mUrl;
    private String mNote;
    private String mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_details);

               Button searchBtn = (Button) findViewById(R.id.fetch_info);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 0;
                resto_name = (EditText) findViewById(R.id.name);
                city_name = (EditText) findViewById(R.id.city);

                mQuery = resto_name.getText().toString();
                mCity = city_name.getText().toString();

                getYelpData f = new getYelpData();
                f.execute();
            }
        });

        Button cancelUpdate = (Button)findViewById(R.id.cancel);
        cancelUpdate.setOnClickListener(new View.OnClickListener(){
            @Override
        public void onClick(View v){
                Intent i = new Intent();
                setResult(RESULT_CANCELED, i);
                finish();

            }
        });

        Button saveDetails = (Button)findViewById(R.id.save);
        saveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("ON CLICK NAME: ", mName);
                Log.i("mCity: ", mCity);
                Log.i("mPhone: ", mPhone);
                Log.i("mAddress: ", mAddress);
                Log.i("mUrl:", mUrl);

                mNote = "COOOOL";

               // mDbAdapter.createRestaurant(mName, false, mCity, mPhone, mAddress, mUrl, mNote);

               // int please = mCursorAdapter.getCount();
                //Log.i("NUM: ", Integer.toString(please));
                Intent returnIntent = new Intent();

                returnIntent.putExtra("NAME", mName);
                returnIntent.putExtra("CHECK", false);
                returnIntent.putExtra("CITY", mCity);
                returnIntent.putExtra("PHONE", mPhone);
                returnIntent.putExtra("ADDRESS", mAddress);
                returnIntent.putExtra("YELP_URL", mUrl);
                returnIntent.putExtra("NOTE", mNote);

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


                for (int i = 0; i < 10; i++) {

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


    public void runChooser() {

        int count = 0;

        final CharSequence[] items = new CharSequence[10];
        for (int j = 0; j < 10; j++) {
            items[j] = "";
        }


        for (int i = 0; i < 10; i++) {
            items[i] = mListItems.get(i).toString();
            //    Log.i("mList: ", items[i].toString());
            //  Log.i("Count: ", Integer.toString(i));
        }

        AlertDialog.Builder b = new AlertDialog.Builder(EditRestaurantActivity.this);

        b.setTitle("Select A Restaurant");
        b.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int c) {
                mChoice = items[c].toString();

                for (int i = 0; i < 10; i++) {
                    try {

                        JSONObject entry = mJS.getJSONObject(i);
                        JSONObject address = entry.getJSONObject("location");
                        JSONArray a = address.getJSONArray("address");
                        String checkAddress = a.getString(0);

                        if (mChoice.contains(entry.getString("name")) && mChoice.contains(checkAddress)) {
                            mName = entry.getString("name");
                            mAddress = checkAddress;
                            mPhone = entry.getString("display_phone");
                            mUrl = entry.getString("url");

                            resto_name.setText(mName);

                            Log.i("MY CHOICE: ", mChoice);
                            Log.i("name: ", mName);
                            Log.i("address: ", mAddress);
                            Log.i("phone: ", mPhone);
                            Log.i("url: ", mUrl);

                            updateDetails();
                        }

                    } catch (JSONException e) {
                        Log.e("ERROR: ", "JSON PARSING ERROR HERE");
                    }

                }
                //mChoice = items[c].toString();
            }
        });

        AlertDialog alert = b.create();
        b.show();

    }

    public void updateDetails() {

        mNum = (TextView)findViewById(R.id.phone_num);
        mNum.setText(mPhone);
        mAdd = (TextView)findViewById(R.id.street_address);
        mAdd.setText(mAddress);
        mYelpPage = (TextView)findViewById(R.id.yelp_url);
        mYelpPage.setText(mUrl);

    }

}
