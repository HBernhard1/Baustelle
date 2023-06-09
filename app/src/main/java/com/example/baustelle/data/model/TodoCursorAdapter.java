package com.example.baustelle.data.model;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.baustelle.R;

public class TodoCursorAdapter extends CursorAdapter {
    public TodoCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_mitarbeiter_template, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvID = view.findViewById(R.id.tvID);
        TextView tvBody = view.findViewById(R.id.tvBody);
        TextView tvPriority = view.findViewById(R.id.tvPriority);
        // Extract properties from cursor
        String id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
        String body = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String priority = cursor.getString(cursor.getColumnIndexOrThrow("description"));
        // Populate fields with extracted properties
        tvID.setText(id);
        tvBody.setText(body);
        tvPriority.setText(String.valueOf(priority));
    }
}