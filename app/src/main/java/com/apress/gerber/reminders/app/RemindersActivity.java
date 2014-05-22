package com.apress.gerber.reminders.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.apress.gerber.reminders.app.db.RemindersDbAdapter;
import com.apress.gerber.reminders.app.db.RemindersSimpleCursorAdapter;


public class RemindersActivity extends ActionBarActivity {

    private ListView mListView;
    private RemindersDbAdapter mDbAdapter;
    private RemindersSimpleCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders_layout);

        mListView = (ListView) findViewById(R.id.reminders_list_view);
        mListView.setDivider(null);


        mDbAdapter = new RemindersDbAdapter(this);
        mDbAdapter.open();

        if (savedInstanceState == null) {
            //Clean all data
            mDbAdapter.deleteAllReminders();
            //Add some data
            mDbAdapter.insertSomeReminders();
        }

        Cursor cursor = mDbAdapter.fetchAllReminders();

        //from columns defined in the db
        String[] from = new String[]{
                RemindersDbAdapter.KEY_CONTENT
        };

        //to the ids of views in the layout
        int[] to = new int[]{
                R.id.row_text
        };

        mCursorAdapter = new RemindersSimpleCursorAdapter(
                //context
                RemindersActivity.this,
                //the layout of the row
                R.layout.reminders_row,
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
            public void onItemClick(AdapterView<?> parent, View view, int masterListPosition, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RemindersActivity.this);
                ListView modeList = new ListView(RemindersActivity.this);
                String[] stringArray = new String[] { "Edit Reminder", "Delete Reminder" };
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(RemindersActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
                modeList.setAdapter(modeAdapter);
                builder.setView(modeList);
                final Dialog dialog = builder.create();
                dialog.show();
                modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        //edit reminder
                        if (position == 0){

                            Toast.makeText(RemindersActivity.this, "edit", Toast.LENGTH_SHORT).show();

                            //delete reminder
                        } else {

                            Toast.makeText(RemindersActivity.this, "delete", Toast.LENGTH_SHORT).show();

                        }
                        dialog.dismiss();
                    }
                });


            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reminders_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_new:
                //create new Reminder
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
        super.onPause();
        mDbAdapter.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDbAdapter.open();
    }
}
