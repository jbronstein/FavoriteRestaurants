package com.jbronstein.profavrestos.app;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class RestaurantActivity extends ActionBarActivity {
    private ListView mListView;
    protected RestaurantDbAdapter mDbAdapter;
    protected RestaurantSimpleCursorAdapter mCursorAdapter;


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restos_layout);

        mListView = (ListView) findViewById(R.id.restos_list_view);
        mListView.setDivider(null);
        mDbAdapter = new RestaurantDbAdapter(this);
        mDbAdapter.open();

        Cursor cursor = mDbAdapter.fetchAllRestaurants();

        //from columns defined in the db
        String[] from = new String[]{
                RestaurantDbAdapter.KEY_CONTENT
        };

        //to the ids of views in the layout
        int[] to = new int[]{
                R.id.row_text
        };

        mCursorAdapter = new RestaurantSimpleCursorAdapter(
                //context
                RestaurantActivity.this,
                //the layout of the row
                R.layout.restos_row,
                //cursor
                cursor,
                //from columns defined in the db
                from,
                //to the ids of views in the layout
                to,
                //flag - not used
                0);


        //the cursorAdapter (controller) is now updating the listView (view) with data from the db (model)
        mListView.setAdapter(mCursorAdapter);

        //when we click an individual item in the listview
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int masterListPosition, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RestaurantActivity.this);
                ListView modeList = new ListView(RestaurantActivity.this);
                String[] stringArray = new String[] { "Edit", "Navigate to", "Map of", "Dial", "Yelp site", "Notes", "Delete" };
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(RestaurantActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
                modeList.setAdapter(modeAdapter);
                builder.setView(modeList);
                final Dialog dialog = builder.create();
                dialog.show();
                modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int nId = getIdFromPosition(masterListPosition);
                        Restaurant r = mDbAdapter.fetchRestaurantById(nId);

                        //edit restaurant
                        if (position == 0){
                            Intent editRestaurant = new Intent(RestaurantActivity.this, EditRestaurantActivity.class);
                            editRestaurant.putExtra("Restaurant", r);
                            startActivity(editRestaurant);
                        }
                        //NavigateTo
                        else if (position == 1) {
                            String navAddress = r.getAddress();
                            String navCity = r.getCity();
                            String nav = "google.navigation:q=" + navAddress + " " + navCity;
                            nav = nav.replace(' ','+');
                            Intent intentNavTo = new Intent(Intent.ACTION_VIEW, Uri.parse(nav));
                            startActivity(intentNavTo);
                        }
                        //MapOf
                        else if (position == 2) {
                                String strAddress = r.getAddress();
                                String strCity = r.getCity();
                                String mapUrl = "geo:0,0?q=" + strAddress + " " + strCity;
                                mapUrl = mapUrl.replace(' ', '+');
                                Log.i("MapOf: ", mapUrl);
                                Intent intentMapOf = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mapUrl));
                                startActivity(intentMapOf);
                        }
                        //Dial
                        else if (position == 3) {
                            String phone = r.getPhone();
                            Intent intentDial = new Intent(Intent.ACTION_CALL);
                            intentDial.setData(Uri.parse("tel:"+ phone));
                            startActivity(intentDial);
                        }
                        //Yelp
                        else if (position == 4) {
                            String strYelpUrl = r.getUrl();
                            Intent intentYelp = new Intent(Intent.ACTION_VIEW);
                            intentYelp.setData(Uri.parse(strYelpUrl));
                            startActivity(intentYelp);
                        }
                        //Add Note
                        else if (position == 5) {
                            fireCustomDialog(r);
                        }
                        //Delete Resto
                        else if (position == 6) {
                            mDbAdapter.deleteRestaurantById(getIdFromPosition(masterListPosition));
                            mCursorAdapter.changeCursor(mDbAdapter.fetchAllRestaurants());
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

        //contextual action mode set-up
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.cam_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.menu_item_delete_reminder:
                            for (int nC = mCursorAdapter.getCount() - 1; nC >= 0; nC--) {
                                if (mListView.isItemChecked(nC)) {

                                    mDbAdapter.deleteRestaurantById(getIdFromPosition(nC));

                                }
                            }
                            mode.finish();
                            mCursorAdapter.changeCursor(mDbAdapter.fetchAllRestaurants());
                            return true;

                    }

                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });

        }
    }

    private int getIdFromPosition(int nPosition){
        Cursor cursor = mDbAdapter.fetchAllRestaurants();
        cursor.move(nPosition);
        return cursor.getInt(RestaurantDbAdapter.KEY_ID_INDEX);
    }


    //Updated CustomDialog to Provide Note functionality
    private void fireCustomDialog(final Restaurant restaurant){


        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);


        TextView textView = (TextView) dialog.findViewById(R.id.custom_title);
        final EditText editCustom = (EditText) dialog.findViewById(R.id.custom_note);
        Button buttonCancel = (Button) dialog.findViewById(R.id.custom_button_cancel);
        Button buttonSave = (Button) dialog.findViewById(R.id.custom_button_commit);
       // final CheckBox checkBox = (CheckBox) dialog.findViewById(R.id.custom_check_box);
       LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.custom_root_layout);

        textView.setText("Notes for: " + restaurant.getContent() + " in " + restaurant.getCity());
            //checkBox.setChecked(restaurant.getImportant() == 1);
        editCustom.setText(restaurant.getNote());
        linearLayout.setBackgroundColor(getResources().getColor(R.color.black));

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strCustom = editCustom.getText().toString();
                restaurant.setNote(strCustom);
                mDbAdapter.updateRestaurant(restaurant);
                mCursorAdapter.changeCursor(mDbAdapter.fetchAllRestaurants());
                dialog.dismiss();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.restos_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_new:
                //create new Restaurant
                Intent i = new Intent(RestaurantActivity.this, EditRestaurantActivity.class);
                startActivityForResult(i, 1);
                //fireCustomDialog(null);
                return true;

            case R.id.action_exit:
                finish();
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void onPause() {
        Log.i("PAUSED: ", "PAUSED");
        super.onPause();
        mDbAdapter.close();
    }

    @Override
    protected void onResume() {
        Log.i("RESUME: ", "RESUME");
        super.onResume();
        mDbAdapter.open();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                mDbAdapter.open();

                String name = data.getStringExtra("NAME");
                Boolean bool = data.getExtras().getBoolean("CHECK");
                String city = data.getStringExtra("CITY");
                String phone = data.getStringExtra("PHONE");
                String address = data.getStringExtra("ADDRESS");
                String url = data.getStringExtra("YELP_URL");
                String note = data.getStringExtra("NOTE");
                String image = data.getStringExtra("IMAGE");

                mDbAdapter.createRestaurant(name, bool, city, phone, address, url, note, image);
                mCursorAdapter.changeCursor(mDbAdapter.fetchAllRestaurants());
                mCursorAdapter.notifyDataSetChanged();

                //Log.i("RESULT: ", result);
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    }




