package com.apress.gerber.reminders.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class RemindersActivity extends ActionBarActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders_layout);

        mListView = (ListView) findViewById(R.id.reminders_list_view);
        //Mediates model and view. This is the controller
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                //context
                this,
                //layout (view)
                R.layout.reminders_row,
                //row (view)
                R.id.row_text,
                //data (model) with bogus data to test our listview
                new String[]{"first record", "second record", "third record"});

        mListView.setAdapter(arrayAdapter);

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
}
