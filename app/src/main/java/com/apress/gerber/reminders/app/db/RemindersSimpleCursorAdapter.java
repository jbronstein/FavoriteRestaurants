package com.apress.gerber.reminders.app.db;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.apress.gerber.reminders.app.R;

/**
 * Created by Adam Gerber on 5/12/2014.
 * University of Chicago
 */
public class RemindersSimpleCursorAdapter extends SimpleCursorAdapter {

    public RemindersSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    //to use a viewholder, you must override the following two methods and define a ViewHolder class
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return super.newView(context, cursor, parent);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {

            holder = new ViewHolder();

            holder.colName = cursor.getColumnIndexOrThrow(RemindersDbAdapter.KEY_CONTENT);
            holder.colImp = cursor.getColumnIndexOrThrow(RemindersDbAdapter.KEY_IMPORTANT);

            holder.listTab =  view.findViewById(R.id.row_tab);
            holder.listText = (TextView) view.findViewById(R.id.row_text);

            view.setTag(holder);
        }

        holder.listText.setText(cursor.getString(holder.colName));
        if (cursor.getInt(holder.colImp) > 0)
            holder.listTab.setBackgroundColor(context.getResources().getColor(R.color.orange));
        else
            holder.listTab.setBackgroundColor(context.getResources().getColor(R.color.green));



    }

    static class ViewHolder {

        //store the column index
        int colName;
        int colImp;

        View listTab;
        TextView listText;

    }
}
